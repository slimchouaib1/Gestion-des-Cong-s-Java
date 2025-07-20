package tn.bfpme.controllers.RHC;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceDepartement;
import tn.bfpme.services.ServiceRole;
import tn.bfpme.services.ServiceSubordinateManager;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.EncryptionUtil;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.StageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CardUserRHController implements Initializable {
    @FXML
    private HBox HBoxBtns;
    @FXML
    private Pane UserCard;
    @FXML
    private Label carddepart;
    @FXML
    private Label cardemail;
    @FXML
    private ImageView cardimage;
    @FXML
    private Label cardnameprename;
    @FXML
    private Label cardrole;
    @FXML
    private Label cardmarticule;

    private static Connection cnx = MyDataBase.getInstance().getCnx();
    private final ServiceUtilisateur UserS = new ServiceUtilisateur();
    private final ServiceDepartement depService = new ServiceDepartement();
    private final ServiceRole roleService = new ServiceRole();
    private final ServiceSubordinateManager SSM = new ServiceSubordinateManager(roleService, depService);
    int uid;
    String unom, uprenom, uemail, umdp, urole, udepart, updp;

    public void setData(User user, String roleName, String departmentName) {
        String imagePath = user.getImage();
        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                FileInputStream inputStream = new FileInputStream(file);
                Image image = new Image(inputStream);
                cardimage.setImage(image);
            } catch (FileNotFoundException e) {
                System.err.println("Image file not found: " + imagePath);
            }
        } else {
            System.err.println("Image path is null for user: " + user);
        }
        cardnameprename.setText(user.getNom() + " " + user.getPrenom());
        cardemail.setText(user.getEmail());
        cardrole.setText(roleName);
        carddepart.setText(departmentName);
        UserCard.setStyle("-fx-border-radius: 5px;-fx-border-color:#808080");
        cardmarticule.setText(String.valueOf(user.getIdUser()));
        uprenom = user.getPrenom();
        uid = user.getIdUser();
        unom = user.getNom();
        uemail = user.getEmail();
        umdp = user.getMdp();
        urole = roleName;
        udepart = departmentName;
        updp = user.getImage();
        /*SAnn = user.getSoldeAnnuel();
        SMala = user.getSoldeMaladie();
        SMater = user.getSoldeMaternite();
        SExp = user.getSoldeExceptionnel();*/
    }

    @FXML
    void ModifierUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paneUsers.fxml"));
            Parent paneUsersRoot = loader.load();
            paneUserController pUC = loader.getController();

            // Decrypt the password
            String decryptedPassword = EncryptionUtil.decrypt(umdp);

            pUC.ID_A.setText(String.valueOf(uid));
            pUC.nom_A.setText(unom);
            pUC.Prenom_A.setText(uprenom);
            pUC.email_A.setText(uemail);
            pUC.MDP_A.setText(decryptedPassword); // Set the decrypted password here
            pUC.image_A.setText(updp);

            String imagePath = updp;
            if (imagePath != null) {
                try {
                    File file = new File(imagePath);
                    FileInputStream inputStream = new FileInputStream(file);
                    Image image = new Image(inputStream);
                    pUC.PDPimageHolder.setImage(image);
                } catch (FileNotFoundException e) {
                    System.err.println("Image file not found: " + imagePath);
                }
            }

            User user = new User(uid, unom, uprenom, uemail, decryptedPassword, updp);
            pUC.populateSoldeFields(user);

            pUC.state = 2;
            pUC.Hfirst.setDisable(false);
            pUC.Hfirst.setVisible(true);
            pUC.adduserbtn.setDisable(true);
            pUC.adduserbtn.setVisible(false);
            pUC.ID_A.setDisable(true);
            pUC.nom_A.setDisable(false);
            pUC.Prenom_A.setDisable(false);
            pUC.email_A.setDisable(false);
            pUC.MDP_A.setDisable(false);
            pUC.upload.setDisable(false);

            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/RH_Interface.fxml"));
            Parent mainRoot = mainLoader.load();
            RHController mainController = mainLoader.getController();
            AnchorPane paneUsersAnchorPane = (AnchorPane) paneUsersRoot;
            paneUsersAnchorPane.prefWidthProperty().bind(mainController.getPaneCont().widthProperty());
            paneUsersAnchorPane.prefHeightProperty().bind(mainController.getPaneCont().heightProperty());
            mainController.getPaneCont().getChildren().setAll(paneUsersAnchorPane);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.show();
            StageManager.addStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void SupprimerUser(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir supprimer cette utilisateur ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            String query = "SELECT ID_User FROM user WHERE ID_Manager = ?";

            try {
                if (cnx == null || cnx.isClosed()) {
                    cnx = MyDataBase.getInstance().getCnx();
                }
                PreparedStatement statement = cnx.prepareStatement(query);
                statement.setInt(1, uid);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int subordinateId = resultSet.getInt("ID_User");
                    SSM.updateUserManager(subordinateId, null);
                    System.out.println("Set manager to null for subordinate ID: " + subordinateId);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error removing role and department: " + e.getMessage(), e);
            }
            UserS.DeleteByID(uid);
            ((GridPane) UserCard.getParent()).getChildren().remove(UserCard);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(UserCard);
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight())
                );
                stage.heightProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight())
                );
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(100),
                        event -> FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight())
                ));
                timeline.setCycleCount(1);
                timeline.play();
                stage.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                    if (isNowShowing) {
                        FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight());
                    }
                });
            }
        });
    }
}
