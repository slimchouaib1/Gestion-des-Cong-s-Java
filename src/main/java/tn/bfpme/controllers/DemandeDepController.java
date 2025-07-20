package tn.bfpme.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tn.bfpme.models.*;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.services.ServiceNotification;
import tn.bfpme.services.ServiceUserSolde;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.Mails;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DemandeDepController implements Initializable {
    @FXML
    private Label CongePerson;
    @FXML
    private Label labelDD;
    @FXML
    private Label labelDF;
    @FXML
    private Label labelDesc;
    @FXML
    private Label labelJours;
    @FXML
    private Label labelType;
    @FXML
    private HBox DocFichHBOX;
    @FXML
    private HBox HBoxAppRef;
    @FXML
    private TextField Int_field;
    @FXML
    private Label InterimName;
    @FXML
    private ListView<User> ListeInterim;

    Connection cnx = MyDataBase.getInstance().getCnx();
    private Conge conge;
    User user;
    public User selectedInterim;
    private int CongeDays;
    int id_manager;
    String employeeName, startDate, endDate, managerName, managerRole;
    String to, Subject, MessageText;
    private final ServiceConge serviceConge = new ServiceConge();
    public FilteredList<User> filteredInterim;
    private final ServiceUserSolde serviceUserSolde = new ServiceUserSolde();
    private final ServiceUtilisateur serviceUser = new ServiceUtilisateur();
    private final ServiceNotification serviceNotif = new ServiceNotification();
    UserConge enAtt = serviceUser.AfficherEnAttente();
    UserConge app = serviceUser.AfficherApprove();
    UserConge reg = serviceUser.AfficherReject();
    DemandeDepListeController controller;
    private ChangeListener<User> userSelectionListener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            try {
                handleInterimSelection(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };


    private void handleInterimSelection(User selectedInterim) throws SQLException {
        this.selectedInterim = selectedInterim;
        Int_field.setText(selectedInterim.getPrenom() + " " + selectedInterim.getNom());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User manager = SessionManager.getInstance().getUser();
        String role = SessionManager.getInstance().getUserRoleName();
        String departement = SessionManager.getInstance().getUserDepartmentName();
        if (manager != null) {
            managerName = manager.getPrenom() + " " + manager.getNom();
            managerRole = String.valueOf(role);
        }
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

    public void setData(Conge conge, User user) {
        this.conge = conge;
        this.user = user;
        id_manager = user.getIdManager();
        CongePerson.setText(user.getNom() + " " + user.getPrenom());
        labelDD.setText(String.valueOf(conge.getDateDebut()));
        labelDF.setText(String.valueOf(conge.getDateFin()));
        labelDesc.setText(conge.getDescription());
        labelType.setText(conge.getDesignation());
        CongeDays = (int) ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateFin());
        labelJours.setText(String.valueOf(CongeDays) + " Jours");
        if (this.conge.getFile() == null) {
            DocFichHBOX.setVisible(false);
        }
        if (serviceUser.getManagerIdByUserId2(conge.getIdUser()) == SessionManager.getInstance().getUser().getIdUser()) {
            HBoxAppRef.setVisible(true);
            if (this.conge.getStatut() != Statut.En_Attente) {
                HBoxAppRef.setVisible(false);
            }
        } else {
            HBoxAppRef.setVisible(false);
        }
        employeeName = user.getPrenom() + " " + user.getNom();
        startDate = String.valueOf(conge.getDateDebut());
        endDate = String.valueOf(conge.getDateFin());
        to = user.getEmail();
        loadInterims();
    }

    @FXML
    void AfficherCongFichier(ActionEvent event) {
        String filePath = "src/main/resources/assets/files/" + conge.getFile();
        File file = new File(filePath);
        if (file.exists()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException e) {
                Logger.getLogger(CongeCarteController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            System.out.println("File not found: " + filePath);
        }
    }

    @FXML
    void ApproverConge(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes-vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir approuver cette demande ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            String Subject = "Approvation de Demande de Congé";
            String NotifContent = "";
            String MessageText = Mails.generateApprobationDemande(employeeName, startDate, endDate, managerName, managerRole);
            // xMails.sendEmail(to, Subject, MessageText); // Mailing
            try {
                boolean isManager = serviceConge.hasSubordinates(this.user.getIdUser());
                FXMLLoader loader;
                if (isManager) {
                    System.out.println("User is a manager, redirecting to Interim.fxml");
                    if (selectedInterim != null) {
                        try {
                            serviceUser.assignInterimManager(this.user.getIdUser(), selectedInterim.getIdUser());
                            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                            loader = new FXMLLoader(getClass().getResource("/DemandeDepListe.fxml"));
                            Parent root = loader.load();
                            Scene scene = currentStage.getScene();
                            scene.setRoot(root);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Une erreur s'est produite lors de l'affectation de l'intérim : " + e.getMessage());
                        }
                    } else {
                        showError("Veuillez sélectionner un utilisateur pour l'intérim.");
                        return; // Stop the process if no interim is selected
                    }

                } else {
                    System.out.println("User is not a manager, redirecting to DemandeDepListe.fxml");
                    // Load DemandeDepListe.fxml
                    loader = new FXMLLoader(getClass().getResource("/DemandeDepListe.fxml"));
                    Parent root = loader.load();
                    Scene currentScene = ((Node) event.getSource()).getScene();
                    currentScene.setRoot(root);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            int congeDays = (int) ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateFin());
            Alert cbon = new Alert(Alert.AlertType.INFORMATION);
            cbon.setTitle("Demande approuvée");
            cbon.setHeaderText("La demande de congé " + this.conge.getDesignation() + " de " + this.user.getNom() + " " + this.user.getPrenom() + " a été approuvée");
            cbon.showAndWait();
            this.conge.setStatut(Statut.Approuvé);
            serviceConge.updateUserSolde(this.user.getIdUser(), conge.getTypeConge().getIdTypeConge(), congeDays);
            serviceConge.updateStatutConge(this.conge.getIdConge(), Statut.Approuvé);
            String NotifSubject = "Votre Demande de congé " + this.conge.getDesignation() + " a été approuvé.";
            String messageText = "Votre Demande de congé " + this.conge.getDesignation() + " du " + this.conge.getDateDebut() + " jusqu'au " + this.conge.getDateFin() + " a été approuvé.";
            serviceNotif.NewNotification(user.getIdUser(), NotifSubject, 1, messageText);
        }
    }

    @FXML
    void RefuserConge(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir rejeter cette demande ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MailingDemande.fxml"));
                Parent root = loader.load();
                MailingDemandeController controller = loader.getController();
                controller.setData(conge, user);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Mailing de Demande");
                StageManager.closeAllStages();
                stage.show();
                StageManager.addStage("Mailing de Demande", stage);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("An unexpected error occurred");
            }

            int congeDays = (int) ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateFin()) + 1;

            // Re-increment TotalSolde
            serviceUserSolde.refuseLeave(this.user.getIdUser(), conge.getTypeConge().getIdTypeConge(), congeDays);

            this.conge.setStatut(Statut.Rejeté);
            serviceConge.updateStatutConge(this.conge.getIdConge(), Statut.Rejeté);
        }
    }

    @FXML
    void retour(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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
                    boolean isInterim = serviceUser.isUserAnInterim(user.getIdUser());
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

    void loadInterims() {
        List<User> interimList = serviceUser.getAppropriateUsers(id_manager);
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
                    boolean isInterim = serviceUser.isUserAnInterim(user.getIdUser());
                    if (isInterim) {
                        setDisable(true);
                        setStyle("-fx-background-color: #d3d3d3;");
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
