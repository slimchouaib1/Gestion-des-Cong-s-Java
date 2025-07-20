package tn.bfpme.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.bfpme.models.Notification;
import tn.bfpme.services.ServiceNotification;

import java.net.URL;
import java.util.ResourceBundle;

public class MessageController implements Initializable {
    @FXML
    private Label labelCC;
    @FXML
    private Label labelMSG;
    @FXML
    private AnchorPane MainAnchorPane;
    private final ServiceNotification notifService = new ServiceNotification();
    private Notification notif;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setDataNotif(Notification notif) {
        this.notif = notif;
        labelMSG.setText(notif.getNotifcontent());
        labelCC.setText(notif.getNotification());
    }
    public void setDataConge(String cmessage) {
        labelMSG.setText(cmessage);
        labelCC.setText("Message de refus");
    }

    @FXML
    void fermer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
