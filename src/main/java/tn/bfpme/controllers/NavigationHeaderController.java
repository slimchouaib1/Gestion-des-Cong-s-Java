package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

public class NavigationHeaderController implements Initializable {
    @FXML
    private Pane NavPane;
    @FXML
    private AnchorPane NavHeaderPane;
    @FXML
    private Button NotifBtn;
    @FXML
    private Button btnListe;
    @FXML
    private Button btnRH;
    @FXML
    private Button test_interfaceID;
    @FXML
    private ImageView pdp2;
    @FXML
    private Button admin_interface;
    @FXML
    private Button settingsButton,btnMonEquipe;

    private Popup settingsPopup;
    private Popup notifPopup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userRoleName = SessionManager.getInstance().getUserRoleName();
        String userDepartmentName = SessionManager.getInstance().getUserDepartmentName();
        btnMonEquipe.setVisible(!"Employe".equals(userRoleName));
        btnListe.setVisible(!"Employe".equals(userRoleName) || "AdminIT".equals(userRoleName));
        btnListe.setVisible(!userRoleName.equals("Employe")||userRoleName.equals("AdminIT"));

        if ((userDepartmentName != null && userDepartmentName.equals("RH") && "Directeur".equals(userRoleName)) || "AdminIT".equals(userRoleName)) {
            btnRH.setVisible(true);
        } else {
            btnRH.setVisible(false);
        }
        admin_interface.setVisible("AdminIT".equals(userRoleName));

        settingsPopup = new Popup();
        settingsPopup.setAutoHide(true);

        try {
            Parent settingsContent = FXMLLoader.load(getClass().getResource("/Settings.fxml"));
            settingsPopup.getContent().add(settingsContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        notifPopup = new Popup();
        notifPopup.setAutoHide(true);
        try {
            Parent settingsContent = FXMLLoader.load(getClass().getResource("/paneNotif.fxml"));
            notifPopup.getContent().add(settingsContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Demander(ActionEvent event) {
        navigateToScene(event, "/DemandeConge.fxml", "Demande congé");
    }

    @FXML
    void Historique(ActionEvent event) {
        navigateToScene(event, "/HistoriqueConge.fxml", "Historique congé");
    }

    @FXML
    void ListeDesDemandes(ActionEvent event) {
        navigateToScene(event, "/DemandeDepListe.fxml", "Liste des demandes - " + SessionManager.getInstance().getUserDepartmentName());
        Stage demandeDepListeStage = new Stage();
        StageManager.addStage("DemandeDepListe", demandeDepListeStage);
    }

    @FXML
    void ListeEmployés(ActionEvent event) {
        navigateToScene(event, "/ListeEmployés.fxml", "Liste des employés");
    }

    @FXML
    void OpenNotifPane(ActionEvent event) {
        if (notifPopup.isShowing()) {
            notifPopup.hide();
        } else {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            double x = window.getX() + NotifBtn.localToScene(0, 0).getX() + NotifBtn.getScene().getX() - 250;
            double y = window.getY() + NotifBtn.localToScene(0, 0).getY() + NotifBtn.getScene().getY() + NotifBtn.getHeight();
            notifPopup.show(window, x, y);
        }
    }

    @FXML
    void RH_Interface(ActionEvent event) {
        navigateToScene(event, "/RH_Interface.fxml", "Ressource Humaine");
    }

    @FXML
    void goto_profil(ActionEvent event) {
        navigateToScene(event, "/profile.fxml", "Mon profil");
    }

    @FXML
    void test_interface(ActionEvent event) {
        navigateToScene(event, "/ResponsableStructure.fxml", "TEST");
    }

    @FXML
    void admin_interfacebtn(ActionEvent event) {
        navigateToScene(event, "/AdminIT.fxml", "AdminIT");
    }

    @FXML
    void settings_button(ActionEvent event) {
        if (settingsPopup.isShowing()) {
            settingsPopup.hide();
        } else {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            double x = window.getX() + settingsButton.localToScene(0, 0).getX() + settingsButton.getScene().getX() - 150;
            double y = window.getY() + settingsButton.localToScene(0, 0).getY() + settingsButton.getScene().getY() + settingsButton.getHeight();
            settingsPopup.show(window, x, y);
        }
    }

    private void navigateToScene(ActionEvent actionEvent, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            //stage.setMaximized(true);
            stage.show();
            StageManager.addStage(title, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
