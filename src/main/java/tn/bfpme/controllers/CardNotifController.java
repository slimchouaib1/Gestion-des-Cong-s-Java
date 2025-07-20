package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.bfpme.models.Notification;
import tn.bfpme.services.ServiceNotification;
import tn.bfpme.utils.FontResizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CardNotifController implements Initializable {
    @FXML private ImageView IconNotif;
    @FXML private Label NotifContent;
    @FXML private Pane Card;

    private Notification notification;
    private final ServiceNotification ServiceNotif = new ServiceNotification();
    private paneNotifController paneNotifController;

    public void setPaneNotifController(paneNotifController paneNotifController) {
        this.paneNotifController = paneNotifController;
    }

    public void setData(Notification notification) {
        this.notification = notification;
        String iconN = "";
        if (notification.getStatut() == 2) {
            iconN = "src/main/resources/assets/imgs/newmessage.png";
        }
        if (notification.getStatut() == 1) {
            iconN = "src/main/resources/assets/imgs/approved.png";
        }
        if (notification.getStatut() == 0) {
            iconN = "src/main/resources/assets/imgs/declined.png";
        }

        NotifContent.setText(notification.getNotification());
        try {
            File file = new File(iconN);
            FileInputStream inputStream = new FileInputStream(file);
            Image image = new Image(inputStream);
            IconNotif.setImage(image);
        } catch (FileNotFoundException e) {
            System.err.println("Image file not found: " + iconN);
        }
        Card.setStyle("-fx-border-radius: 5px;-fx-border-color:#808080");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void DeleteNotif(ActionEvent event) {
        ServiceNotif.DeleteNotif(this.notification.getIdNotif());
        if (paneNotifController != null) {
            paneNotifController.load();
        }
    }
    @FXML
    void ViewMessage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Message.fxml"));
            Parent root = loader.load();
            MessageController msgController = loader.getController();
            msgController.setDataNotif(this.notification);
            Stage newStage = new Stage();
            newStage.setTitle("Message Window");
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
