package tn.bfpme.controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.application.Platform;
import tn.bfpme.models.EmailsTemplates;
import tn.bfpme.models.Conge;
import tn.bfpme.models.Statut;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.services.ServiceEmailTemp;
import tn.bfpme.services.ServiceNotification;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MailingDemandeController implements Initializable {

    @FXML
    private Label mail_dest;
    @FXML
    private TextField mail_obj;
    @FXML
    private TextArea mail_text;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private ComboBox<EmailsTemplates> raison_mail;

    private Conge conge;
    private User user;
    private final ServiceConge serviceConge = new ServiceConge();
    private final ServiceEmailTemp emailtempService = new ServiceEmailTemp();
    private final ServiceNotification notifService = new ServiceNotification();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Add listener to the ComboBox in the initialize method
        raison_mail.getSelectionModel().selectedItemProperty().addListener(this::onRaisonSelected);
        Platform.runLater(() -> {
            Stage stage = (Stage) MainAnchorPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
        });
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

        try {
            List<EmailsTemplates> emailtempList = emailtempService.getAllEmailsTemplates();
            ObservableList<EmailsTemplates> emailstemps = FXCollections.observableArrayList(emailtempList);
            raison_mail.setItems(emailstemps);
            raison_mail.setCellFactory(param -> new ListCell<EmailsTemplates>() {
                @Override
                protected void updateItem(EmailsTemplates item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getObject() == null) {
                        setText(null);
                    } else {
                        setText(item.getObject());
                    }
                }
            });
            raison_mail.setButtonCell(new ListCell<EmailsTemplates>() {
                @Override
                protected void updateItem(EmailsTemplates item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getObject() == null) {
                        setText(null);
                    } else {
                        setText(item.getObject());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        User manager = SessionManager.getInstance().getUser();
        String role = SessionManager.getInstance().getUserRoleName();
        if (manager != null) {
            String managerName = manager.getPrenom() + " " + manager.getNom();
            String managerRole = String.valueOf(role);
        }
    }

    private void onRaisonSelected(ObservableValue<? extends EmailsTemplates> observable, EmailsTemplates oldValue, EmailsTemplates newValue) {
        if (newValue != null) {
            mail_obj.setText(newValue.getObject());
            mail_text.setText(newValue.getMessage());
        }
    }

    public void setData(Conge conge, User user) {
        this.conge = conge;
        this.user = user;
        String employeeName = user.getPrenom() + " " + user.getNom();
        String startDate = String.valueOf(conge.getDateDebut());
        String endDate = String.valueOf(conge.getDateFin());
        mail_dest.setText(user.getEmail());
    }

    @FXML
    void selectRaison(ActionEvent event) {
        /* raison_mail.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mail_obj.setText(newValue.getObject());
                mail_text.setText(newValue.getMessage());
            }
        });*/
    }

    @FXML
    void Annuler_mail(ActionEvent event) {
        mail_text.setText("");
        mail_obj.setText("");
    }

    @FXML
    void Envoyer_mail(ActionEvent event) {
        String to = mail_dest.getText();
        String subject = mail_obj.getText();
        String messageText = mail_text.getText();
        serviceConge.NewMessage(messageText, user.getIdUser(), conge.getIdConge());
        if (conge.getStatut() == Statut.Approuvé) {
            String NotifSubject = "Votre Demande de congé " + conge.getDesignation() + " a été approuvé.";
            notifService.NewNotification(user.getIdUser(), NotifSubject, 1, messageText);
        } else if (conge.getStatut() == Statut.Rejeté) {
            String NotifSubject = "Votre Demande de congé " + conge.getDesignation() + " a été rejeté à cause de " + subject;
            notifService.NewNotification(user.getIdUser(), NotifSubject, 0, messageText);
        }
        //Mails.sendEmail(to,subject,messageText);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeDepListe.fxml"));
            Parent root = loader.load();
            DemandeDepListeController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            String title = "Liste des demandes - " + SessionManager.getInstance().getUserDepartmentName();
            stage.setTitle(title);
            StageManager.closeAllStages();
            stage.show();
            StageManager.addStage(title, stage);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
