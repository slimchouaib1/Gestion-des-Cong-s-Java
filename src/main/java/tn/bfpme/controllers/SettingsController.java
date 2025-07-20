package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Label User_name;
    @FXML
    private Pane SettingsCard;
    @FXML
    private ImageView User_pdp;
    @FXML
    private Label User_role;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        try {
            User_name.setText(SessionManager.getInstance().getUser().getNom() + " " + SessionManager.getInstance().getUser().getPrenom());
            User_role.setText(String.valueOf(SessionManager.getInstance().getUserRoleName()));
            String imagePath = SessionManager.getInstance().getUser().getImage();

            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        Image image = new Image(inputStream);
                        User_pdp.setImage(image);
                    } catch (FileNotFoundException e) {
                        System.err.println("Image file not found: " + imagePath);
                    }
                } else {
                    System.err.println("Image file does not exist: " + imagePath);
                }
            } else {
                System.err.println("Image path is null or empty for user: " + SessionManager.getInstance().getUser());
            }
            /*String userImagePath = SessionManager.getInstance().getUtilisateur().getImage();
            System.out.println("Image path: " + userImagePath); // Debugging statement
            Image image = new Image(userImagePath);
            User_pdp.setImage(image);*/

            // Make the ImageView round
            Circle clip = new Circle(User_pdp.getFitWidth() / 2, User_pdp.getFitHeight() / 2, Math.min(User_pdp.getFitWidth(), User_pdp.getFitHeight()) / 2);
            User_pdp.setClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(SettingsCard); // Replace rootAnchorPane with your actual root element
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(SettingsCard, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(SettingsCard, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                FontResizer.resizeFonts(SettingsCard, stage.getWidth(), stage.getHeight()); // Replace rootAnchorPane with your actual root element
            }
        });
    }

    @FXML
    void Contact_button(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MailingContact.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion de Congés - Connection");
            stage.show();
            StageManager.closeAllStages();
            StageManager.addStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Deconnection(ActionEvent event) {
        SessionManager.getInstance().cleanUserSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion de Congés - Connection");
            stage.setMaximized(true); // Ensure the stage is maximized
            stage.show();

            // Ensure the stage is resized and repositioned after it has been shown
            Platform.runLater(() -> {
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX(primaryScreenBounds.getMinX());
                stage.setY(primaryScreenBounds.getMinY());
                stage.setWidth(primaryScreenBounds.getWidth());
                stage.setHeight(primaryScreenBounds.getHeight());
            });

            StageManager.closeAllStages();
            StageManager.addStage(stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Help_button(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Statistiques.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques");
            stage.show();
            StageManager.closeAllStages();
            StageManager.addStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void FacialRecognition() {
        try {
            // Load the CameraFeed window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CameraFeed.fxml"));
            Parent root = loader.load();
            CameraFeedController controller = loader.getController();

            // Create a new stage (window)
            Stage stage = new Stage();
            stage.setTitle("Facial Recognition");
            stage.setScene(new Scene(root));

            // Pass the stage to the controller
            controller.setStage(stage);

            // Show the stage
            stage.show();

            // Start the facial recognition process
            controller.startFacialRecognition();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
