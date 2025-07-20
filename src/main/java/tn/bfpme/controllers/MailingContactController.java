package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.Mails;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class MailingContactController implements Initializable {
    @FXML
    private TextField mail_dest;
    @FXML
    private TextField mail_obj;
    @FXML
    private TextArea mail_text;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private ComboBox<String> raison_mail;


    String employeeName, startDate, endDate, managerName, managerRole;
    ServiceUtilisateur UserS = new ServiceUtilisateur();
    private List<User> usersInDepartment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMail();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NavigationHeader.fxml"));
            AnchorPane departementPane = loader.load();
            AnchorPane.setTopAnchor(departementPane, 0.0);
            AnchorPane.setLeftAnchor(departementPane, 0.0);
            AnchorPane.setRightAnchor(departementPane, 0.0);
            MainAnchorPane.getChildren().add(departementPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            Stage stage = (Stage) MainAnchorPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
        });
        User manager = SessionManager.getInstance().getUser();
        String role = SessionManager.getInstance().getUserRoleName();
        String departement = SessionManager.getInstance().getUserDepartmentName();
        if (manager != null) {
            managerName = manager.getPrenom() + " " + manager.getNom();
            managerRole = String.valueOf(role);
            usersInDepartment = UserS.getUsersByDepartment(departement); // Load users in the department
        }
    }

    @FXML
    void Envoyer_mail(ActionEvent event) {
        String to = mail_dest.getText();
        String subject = mail_obj.getText();
        String messageText = mail_text.getText();
        Mails.sendEmail(to,subject,messageText);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();
            EmployeController controller = loader.getController();
            StageManager.closeAllStages();
            Stage profileStage = new Stage();
            Scene scene = new Scene(root);
            profileStage.setScene(scene);
            profileStage.setTitle("Profil");
            profileStage.show();
            StageManager.addStage("profile",profileStage );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Annuler_mail(ActionEvent event) {
        mail_text.setText("");
        mail_obj.setText("");
    }

    public void loadMail() {
        mail_dest.setText("slimchouaib2003@gmail.com");
        mail_dest.setDisable(true);
    }

}
