package tn.bfpme.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.MyDataBase;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class InterimController implements Initializable {
    private User user;
    @FXML
    private HBox HBoxAppRef;

    @FXML
    private TextField Int_field;
    int id_manager;

    @FXML
    private Label InterimName;

    @FXML
    private ListView<User> ListeInterim;
    public User selectedInterim;
    public FilteredList<User> filteredInterim;
    private final ServiceUtilisateur userService = new ServiceUtilisateur();
    private ChangeListener<User> userSelectionListener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            try {
                handleInterimSelection(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };
    Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadInterims();
        ListeInterim.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    handleInterimSelection(newValue);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setData(User user) {
        this.user = user;
        InterimName.setText(user.getNom() + " " + user.getPrenom());
        id_manager = user.getIdUser();
        System.out.println("aaaaaaaa");
        loadInterims();
    }

    @FXML
    void AffecterInterim(ActionEvent event) {
        if (selectedInterim != null) {
            boolean isAffected = false;
            try {
                userService.assignInterimManager(id_manager, selectedInterim.getIdUser());
                isAffected = true;
                if (isAffected) {
                    // Close all stages and show DemandeDepListe.fxml
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));

                    // Load and replace the current scene with DemandeDepListe.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeDepListe.fxml"));
                    Parent root = loader.load();
                    Scene scene = currentStage.getScene();
                    scene.setRoot(root);
                } else {
                    showError("Veuillez sélectionner un Intérim à affecter.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Une erreur s'est produite : " + e.getMessage());
            }
        } else {
            showError("Veuillez sélectionner un utilisateur.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleInterimSelection(User selectedInterim) throws SQLException {
        this.selectedInterim = selectedInterim;
        Int_field.setText(selectedInterim.getPrenom() + " " + selectedInterim.getNom());
    }

    private void loadInterims() {
        List<User> interimList = userService.getAppropriateUsers(id_manager);
        ObservableList<User> users = FXCollections.observableArrayList(interimList);
        filteredInterim = new FilteredList<>(users, p -> true);
        ListeInterim.setItems(filteredInterim);
        ListeInterim.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(user.getPrenom() + " " + user.getNom());
                    boolean isInterim = userService.isUserAnInterim(user.getIdUser());
                    if (isInterim) {
                        setDisable(true);
                        setStyle("-fx-background-color: #d3d3d3;"); // Grey out disabled items
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });
    }

    @FXML
    void Annuler(ActionEvent event) {
        showError("Vous devez affecter un intérim.");
    }

    @FXML
    void RechercheInt(KeyEvent event) {
        String searchText = Int_field.getText().trim().toLowerCase();
        filteredInterim.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            boolean matchesFilter = user.getNom().toLowerCase().contains(searchText) ||
                    user.getPrenom().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);

            return matchesFilter;
        });

        ListeInterim.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(user.getPrenom() + " " + user.getNom());
                    boolean isInterim = userService.isUserAnInterim(user.getIdUser());
                    if (isInterim) {
                        setDisable(true);
                        setStyle("-fx-background-color: #d3d3d3;"); // Grey out disabled items
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });
    }

    @FXML
    void clear(ActionEvent event) {
        Int_field.setText("");
        ListeInterim.getSelectionModel().clearSelection();
        filteredInterim.setPredicate(user -> true);
        ListeInterim.setItems(filteredInterim);
    }
}
