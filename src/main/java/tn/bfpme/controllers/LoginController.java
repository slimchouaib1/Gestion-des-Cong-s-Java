package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.FaceMatch;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageRequest;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tn.bfpme.models.User;
import tn.bfpme.utils.EncryptionUtil;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private TextField LoginEmail;
    @FXML
    private PasswordField LoginMDP;
    @FXML
    private TextField showPasswordField;
    @FXML
    private Button toggleButton;
    private VideoCapture camera;

    private Connection cnx;

    @FXML
    public void initialize() {
        cnx = MyDataBase.getInstance().getCnx();
        initializeCamera(); // Initialize camera once
    }

    private void initializeCamera() {
        try {
            camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                showAlert("Error", "Unable to access camera.");
                System.err.println("Error: Camera could not be opened.");
            } else {
                System.out.println("Camera initialized successfully.");
            }
        } catch (Exception e) {
            showAlert("Error", "Camera initialization failed.");
            e.printStackTrace();
        }
    }

    @FXML
    void Login(ActionEvent event) {
        String qry = "SELECT u.*, ur.ID_Role " +
                "FROM `user` as u " +
                "JOIN `user_role` ur ON ur.ID_User = u.ID_User " +
                "WHERE u.`Email`=?";
        try {
            PreparedStatement stm = cnx.prepareStatement(qry);
            stm.setString(1, LoginEmail.getText());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("MDP");
                String decryptedPassword = EncryptionUtil.decrypt(storedEncryptedPassword);
                if (decryptedPassword.equals(LoginMDP.getText())) {
                    User connectedUser = new User(
                            rs.getInt("ID_User"),
                            rs.getString("Nom"),
                            rs.getString("Prenom"),
                            rs.getString("Email"),
                            storedEncryptedPassword,
                            rs.getString("Image"),
                            rs.getInt("ID_Manager"),
                            rs.getInt("ID_Departement"),
                            rs.getInt("ID_Role"),
                            rs.getString("face_data1"),
                            rs.getString("face_data2"),
                            rs.getString("face_data3"),
                            rs.getString("face_data4")
                    );
                    connectedUser.setIdRole(rs.getInt("ID_Role"));
                    populateUserSolde(connectedUser);

                    // Initialize SessionManager with the connected user
                    SessionManager.getInstance(connectedUser);

                    // Navigate to profile after successful login
                    navigateToProfile(event);
                } else {
                    showAlert("Login failed", "Invalid email or password.");
                }
            } else {
                showAlert("Login failed", "Invalid email or password.");
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
String MSG ="";
    @FXML
    private void FacialRecognitionButton(ActionEvent event) {
        try {
            if (camera == null || !camera.isOpened()) {
                showAlert("Error", "Camera is not accessible.");
                return;
            }

            Mat capturedFrame = captureImageFromCamera();
            System.out.println(MSG);
            if (capturedFrame == null || capturedFrame.empty()) {
                showAlert("Error", "Failed to capture image from camera.");
                return;
            }

            // Perform facial recognition locally
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    System.out.println("Starting local facial recognition...");
                    return performLocalFacialRecognition(capturedFrame);
                }

                @Override
                protected void succeeded() {
                    System.out.println("Facial recognition task succeeded.");

                    if (getValue()) {
                        try {
                            User recognizedUser = SessionManager.getInstance().getUser();

                            if (recognizedUser != null) {
                                // Navigate to profile after successful login
                                navigateToProfile(event);
                            } else {
                                showAlert("Face not recognized", "Please try again.");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        showAlert("Face not recognized", "Please try again.");
                    }
                }

                @Override
                protected void failed() {
                    System.err.println("Facial recognition task failed.");
                    showAlert("Error", "An error occurred during facial recognition.");
                }
            };
            new Thread(task).start();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred during facial recognition.");
        }
    }
    private boolean performLocalFacialRecognition(Mat capturedFrame) {
        try {
            System.out.println("Attempting to recognize user based on the captured frame...");

            // Attempt to recognize the user based on the captured frame
            User recognizedUser = getRecognizedUser(capturedFrame);

            if (recognizedUser != null) {
                // Initialize the SessionManager with the recognized user
                System.out.println("User recognized: " + recognizedUser.getEmail());
                SessionManager.getInstance(recognizedUser);
                return true;
            } else {
                System.out.println("No matching user found.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error during facial recognition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    private Mat captureImageFromCamera() {
        try {
            Mat frame = new Mat();
            if (camera.isOpened()) {
                camera.read(frame);
                if (frame.empty()) {
                    System.err.println("Error: Captured frame is empty.");
                    return null;
                }
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale
                System.out.println("Image captured successfully.");
                return frame;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error during image capture: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private String saveCapturedImage(Mat frame) {
        // Save the captured frame to a temporary file
        String tempImagePath = System.getProperty("java.io.tmpdir") + "captured_face.jpg";
        Imgcodecs.imwrite(tempImagePath, frame);
        System.out.println("Saved captured image to: " + tempImagePath);

        // Upload the image to S3
        uploadImageToS3(tempImagePath);

        return tempImagePath;
    }
    private void uploadImageToS3(String imagePath) {
        try {
            S3Client s3 = S3Client.builder()
                    .region(Region.EU_CENTRAL_1)
                    .credentialsProvider(ProfileCredentialsProvider.create())
                    .build();

            String bucketName = "facialrecjava";
            String key = "captured_face.jpg"; // You might want to use a unique name for each upload

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromFile(new File(imagePath)));

            System.out.println("Uploaded image to S3: " + key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean basicFaceDetectionTest(Mat capturedFrame) {
        try {
            System.out.println("Starting basic face detection...");

            // Load the Haar cascade for face detection
            String haarCascadePath = "src/main/resources/assets/FacialRegDATA/XML/haarcascades/haarcascade_frontalface_default.xml"; // Replace with the correct path to the Haar cascade
            CascadeClassifier faceDetector = new CascadeClassifier(haarCascadePath);

            if (faceDetector.empty()) {
                System.err.println("Failed to load Haar cascade file.");
                return false;
            }

            // Detect faces in the captured frame
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(capturedFrame, faceDetections);

            if (faceDetections.toArray().length > 0) {
                System.out.println("Face detected in the captured frame.");
                return true;
            } else {
                System.out.println("No faces detected in the captured frame.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception during basic face detection: " + e.getMessage());
            return false;
        }
    }

    private User getRecognizedUser(Mat capturedFrame) throws SQLException {
        System.out.println("Loading users from the database...");

        String qry = "SELECT u.*, ur.ID_Role " +
                "FROM `user` as u " +
                "JOIN `user_role` ur ON ur.ID_User = u.ID_User";
        PreparedStatement stm = cnx.prepareStatement(qry);
        ResultSet rs = stm.executeQuery();

        User recognizedUser = null;
        int userCount = 0;

        while (rs.next()) {
            System.out.println("Processing user ID: " + rs.getInt("ID_User"));
            User user = initializeUserFromResultSet(rs);
            System.out.println("Comparing with user: " + user.getEmail());

            List<Mat> storedImages = loadStoredImages(user);

            // Check if the stored images list is populated
            if (storedImages.isEmpty()) {
                System.out.println("No stored images found for user: " + user.getEmail());
                continue; // Skip this user and continue to the next
            }

            if (compareFaces(capturedFrame, storedImages)) {
                System.out.println("Face matched with user: " + user.getEmail());
                recognizedUser = user;  // Save the recognized user
                break;  // Exit the loop as we've found a match
            } else {
                System.out.println("No match found for user: " + user.getEmail());
            }
        }


        if (recognizedUser == null) {
            System.out.println("No matching user found after checking " + userCount + " users.");
        }

        return recognizedUser;
    }


    /*private List<Mat> loadStoredImagesFromS3(User user) {
        List<Mat> images = new ArrayList<>();
        String[] faceDataPaths = {user.getFace_data1(), user.getFace_data2(), user.getFace_data3(), user.getFace_data4()};

        for (String s3Key : faceDataPaths) {
            if (s3Key != null && !s3Key.isEmpty()) {
                // Download image from S3 and convert it to Mat
                Mat image = downloadImageFromS3(s3Key);
                if (image != null && !image.empty()) {
                    images.add(image);
                } else {
                    System.err.println("Failed to download image from S3 with key: " + s3Key);
                }
            }
        }

        return images;
    }*/



    private boolean compareFaces(Mat capturedFrame, List<Mat> storedImages) {
        try {
            // Convert the captured frame to grayscale if it's not already
            Mat grayscaleCapturedFrame = new Mat();
            if (capturedFrame.channels() > 1) {
                Imgproc.cvtColor(capturedFrame, grayscaleCapturedFrame, Imgproc.COLOR_BGR2GRAY);
            } else {
                grayscaleCapturedFrame = capturedFrame;
            }

            // Debugging output: Check if images are correctly loaded
            System.out.println("Number of stored images: " + storedImages.size());

            // Attempt to create the recognizer
            FaceRecognizer faceRecognizer;
            try {
                System.out.println("Attempting to create LBPHFaceRecognizer...");
                faceRecognizer = LBPHFaceRecognizer.create();
                System.out.println("LBPHFaceRecognizer created successfully.");
            } catch (Exception e) {
                System.err.println("Error creating LBPHFaceRecognizer:");
                e.printStackTrace();
                return false;  // Early return if recognizer creation fails
            }

            // Prepare labels for training
            List<Mat> images = new ArrayList<>();
            Mat labels = new Mat(storedImages.size(), 1, CvType.CV_32SC1);

            for (int i = 0; i < storedImages.size(); i++) {
                images.add(storedImages.get(i));
                labels.put(i, 0, i);  // Assuming each image has a unique label
                // Debugging output: Check each image label
                System.out.println("Image " + i + " associated with label " + i);
            }

            // Debugging output: Check if training starts
            System.out.println("Starting training phase...");

            try {
                faceRecognizer.train(images, labels);
                System.out.println("Training completed. Starting prediction...");
            } catch (Exception e) {
                System.err.println("Error during training phase:");
                e.printStackTrace();
                return false;  // Early return if training fails
            }

            // Predict the label of the captured frame
            int[] label = new int[1];
            double[] confidence = new double[1];
            try {
                faceRecognizer.predict(grayscaleCapturedFrame, label, confidence);
            } catch (Exception e) {
                System.err.println("Error during prediction phase:");
                e.printStackTrace();
                return false;  // Early return if prediction fails
            }

            // Debugging output: Show prediction result
            System.out.println("Predicted label: " + label[0]);
            System.out.println("Confidence: " + confidence[0]);

            // Set a threshold for confidence to consider the prediction as successful
            double confidenceThreshold = 50.0; // You can adjust this value as needed

            if (confidence[0] < confidenceThreshold) {
                System.out.println("Face recognized with confidence: " + confidence[0]);
                return true; // Face recognized successfully
            } else {
                System.out.println("Face not recognized. Confidence: " + confidence[0]);
                return false; // Face not recognized
            }
        } catch (Exception e) {
            // Catch and log any exceptions
            e.printStackTrace();
            System.err.println("Exception during face comparison: " + e.getMessage());
            return false; // Return false if any exception occurs
        }
    }


    private List<Mat> loadStoredImages(User user) {
        List<Mat> images = new ArrayList<>();
        String[] faceDataPaths = {user.getFace_data1(), user.getFace_data2(), user.getFace_data3(), user.getFace_data4()};

        for (String path : faceDataPaths) {
            if (path != null && !path.isEmpty()) {
                Mat image = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE);
                if (image != null && !image.empty()) {
                    images.add(image);
                    System.out.println("Loaded image size: " + image.size());
                } else {
                    System.err.println("Failed to load image from path: " + path);
                }
            } else {
                System.out.println("No valid path for this image slot.");
            }
        }

        System.out.println("Total images loaded for user: " + images.size());
        return images;
    }



    private User initializeUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("ID_User"),
                rs.getString("Nom"),
                rs.getString("Prenom"),
                rs.getString("Email"),
                rs.getString("MDP"),
                rs.getString("Image"),
                rs.getInt("ID_Manager"),
                rs.getInt("ID_Departement"),
                rs.getInt("ID_Role"),
                rs.getString("face_data1"),
                rs.getString("face_data2"),
                rs.getString("face_data3"),
                rs.getString("face_data4")
        );
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void navigateToProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Profile");
        stage.show();
    }

    private void populateUserSolde(User user) {
        String soldeQuery = "SELECT us.*, tc.Designation FROM user_solde us JOIN typeconge tc ON us.ID_TypeConge = tc.ID_TypeConge WHERE us.ID_User = ?";
        try (PreparedStatement soldeStm = cnx.prepareStatement(soldeQuery)) {
            soldeStm.setInt(1, user.getIdUser());
            ResultSet soldeRs = soldeStm.executeQuery();
            while (soldeRs.next()) {
                int typeCongeId = soldeRs.getInt("ID_TypeConge");
                double totalSolde = soldeRs.getDouble("TotalSolde");
                String typeConge = soldeRs.getString("Designation");
                user.setSoldeByType(typeCongeId, totalSolde, typeConge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
