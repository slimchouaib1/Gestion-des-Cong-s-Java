package tn.bfpme.controllers.RHC;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.bfpme.models.EmailsTemplates;
import tn.bfpme.models.Role;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceEmailTemp;
import tn.bfpme.services.ServiceRole;
import tn.bfpme.utils.FontResizer;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class paneEmailTempController implements Initializable {
    @FXML
    private TextArea MessageTF;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private ListView<EmailsTemplates> ObjListView;
    @FXML
    private TextField RechercheTemp;
    @FXML
    private TextField objectTF;

    @FXML
    private Button Add;
    @FXML
    private Button Enregistrer;
    private int state =0;

    @FXML
    private Button Annuler;
    @FXML
    private Button Update;
    @FXML
    private Button Delete;

    @FXML
    private HBox Hfirst;

    @FXML
    private HBox Hsecond;


    private final ServiceEmailTemp emailtempService = new ServiceEmailTemp();
    private FilteredList<EmailsTemplates> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Load();
        Platform.runLater(() -> {
            Stage stage = (Stage) MainAnchorPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
        });
        RechercheTemp.setDisable(false);
        RechercheTemp.setText("");
        MessageTF.setDisable(true);
        objectTF.setDisable(true);
        Add.setDisable(false);
        Update.setDisable(true);
        Delete.setDisable(true);
        Hsecond.setDisable(true);
        Hsecond.setVisible(false);
        ObjListView.setDisable(false);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);
        ObjListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                objectTF.setText(newValue.getObject());
                MessageTF.setText(newValue.getMessage());
                objectTF.setDisable(true);
                MessageTF.setDisable(true);
                RechercheTemp.setDisable(false);
                ObjListView.setDisable(false);
                Add.setDisable(true);
                Update.setDisable(false);
                Delete.setDisable(false);

            }
        });
        RechercheTemp.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(emailtemp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return emailtemp.getObject().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void Load() {
        try {
            List<EmailsTemplates> emailtempList = emailtempService.getAllEmailsTemplates();
            ObservableList<EmailsTemplates> emailstemps = FXCollections.observableArrayList(emailtempList);
            ObjListView.setItems(emailstemps);
            ObjListView.setCellFactory(param -> new ListCell<EmailsTemplates>() {
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
            showError("Failed to load roles: " + e.getMessage());
        }
    }

    @FXML
    void AjouterTemp(ActionEvent event) {
        state=1;
        RechercheTemp.setDisable(true);
        Hfirst.setVisible(false);
        Hfirst.setDisable(true);
        Hsecond.setDisable(false);
        Hsecond.setVisible(true);
        objectTF.setDisable(false);
        MessageTF.setDisable(false);
        ObjListView.setDisable(true);
    }

    @FXML
    void ModifierTemp(ActionEvent event) {
        state=2;
        RechercheTemp.setDisable(true);
        Hfirst.setVisible(false);
        Hfirst.setDisable(true);
        Hsecond.setDisable(false);
        Hsecond.setVisible(true);
        objectTF.setDisable(false);
        MessageTF.setDisable(false);
        ObjListView.setDisable(true);

    }
    @FXML
    void EmailTempRecherche(ActionEvent actionEvent) {
        String searchText = RechercheTemp.getText().trim();
        for (EmailsTemplates emailtemp : emailtempService.getAllEmailsTemplates()) {
            if (emailtemp.getObject().equalsIgnoreCase(searchText)) {
                RechercheTemp.setText(emailtemp.getObject());
                break;
            }
        }
    }

    @FXML
    void SupprimerTemp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir supprimer ce modèle de correspondance ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            try {
                EmailsTemplates selectedEmailTemp = ObjListView.getSelectionModel().getSelectedItem();
                if (selectedEmailTemp != null) {
                    emailtempService.DeleteEmailTelp(selectedEmailTemp.getId_Email());
                    Load();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        reset1();
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void AnnulerEmail(ActionEvent event) {
        reset1();

    }

    @FXML
    void EnregistrerEmail(ActionEvent event) {
        if (state==1){
            String obj = objectTF.getText();
            String msg = MessageTF.getText();
            if (MessageTF.getText().isEmpty() || objectTF.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
                return;
            }
            emailtempService.AddEmailTemp(obj, msg);
            Load();
            reset1();
        } else if (state==2) {
            EmailsTemplates selectedEmailTemp = ObjListView.getSelectionModel().getSelectedItem();
            String obj = objectTF.getText();
            String msg = MessageTF.getText();
            if (MessageTF.getText().isEmpty() || objectTF.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
                return;
            }
            emailtempService.UpdateEmailTemp(selectedEmailTemp.getId_Email(), obj, msg);
            Load();
            reset1();

        }

    }

    void reset1(){
        ObjListView.getSelectionModel().clearSelection();
        ObjListView.setDisable(false);
        objectTF.setText("");
        MessageTF.setText("");
        MessageTF.setDisable(true);
        objectTF.setDisable(true);
        RechercheTemp.setDisable(false);
        RechercheTemp.setText("");
        Add.setDisable(false);
        Update.setDisable(true);
        Delete.setDisable(true);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);
        Hsecond.setDisable(true);
        Hsecond.setVisible(false);
    }

}
