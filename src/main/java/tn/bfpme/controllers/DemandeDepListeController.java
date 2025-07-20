package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.bfpme.models.*;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

public class DemandeDepListeController implements Initializable {
    @FXML
    private GridPane DemandesContainer;
    @FXML
    private TextField Recherche_demande;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private ComboBox<String> comboTri;
    @FXML
    public Button NotifBtn;
    private Conge conge;
    private final ServiceConge CongeS = new ServiceConge();
    private final ServiceUtilisateur UserS = new ServiceUtilisateur();
    ObservableList<String> TriListe = FXCollections.observableArrayList("Type", "Nom", "Prenom", "Date Debut", "Date Fin");
    private UserConge EnAtt = UserS.AfficherEnAttente();
    private UserConge App = UserS.AfficherApprove();
    private UserConge Reg = UserS.AfficherReject();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load(EnAtt);
        comboTri.setValue("Selectioner");
        comboTri.setItems(TriListe);
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
    }

    public void setData(UserConge enAtt, UserConge app, UserConge reg) {
        this.EnAtt = enAtt;
        this.App = app;
        this.Reg = reg;
        load(EnAtt); // Load the initial data
    }

    @FXML
    void Recherche(KeyEvent event) {
        UserConge rechUC = UserS.RechercheUserCongeEnAtt(Recherche_demande.getText());
        load(rechUC);
    }

    public void load(UserConge Arg) {
        DemandesContainer.getColumnConstraints().clear();
        for (int i = 0; i < 3; i++) { // Three columns
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            DemandesContainer.getColumnConstraints().add(columnConstraints);
        }
        DemandesContainer.setVgap(8);
        DemandesContainer.setHgap(10);
        DemandesContainer.setPadding(new Insets(8));
        int row = 0;
        int column = 0;
        try {
            DemandesContainer.getChildren().clear();
            List<User> users = Arg.getUsers();
            List<Conge> conges = Arg.getConges();
            for (Conge conge : conges) {
                for (User user : users) {
                    if (conge.getIdUser() == user.getIdUser()) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/UserCarte.fxml"));
                        try {
                            Pane cardBox = fxmlLoader.load();
                            UserCarteController cardu = fxmlLoader.getController();
                            cardu.setData(conge, user);
                            cardBox.prefWidthProperty().bind(DemandesContainer.widthProperty());
                            DemandesContainer.add(cardBox, column, row);
                            GridPane.setMargin(cardBox, new Insets(10));
                            cardBox.setMaxWidth(Double.MAX_VALUE);
                            column++;
                            if (column == 1) {
                                column = 0;
                                row++;
                            }
                        } catch (IOException e) {
                            System.err.println("Error loading UserCarte.fxml: " + e.getMessage());
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in load method: " + e.getMessage());
            e.printStackTrace();
        }
       /* DemandesContainer.getColumnConstraints().clear();
        for (int i = 0; i < 3; i++) { // Three columns
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            DemandesContainer.getColumnConstraints().add(columnConstraints);
        }
        DemandesContainer.setVgap(8);
        DemandesContainer.setHgap(10);
        DemandesContainer.setPadding(new Insets(8));
        int row = 0;
        int column = 0;
        try {
            DemandesContainer.getChildren().clear();
            List<User> users = Arg.getUsers();
            List<Conge> conges = Arg.getConges();
            User loggedInUser = SessionManager.getInstance().getUser();
            if (loggedInUser != null) {
                User interimForManager = UserS.getInterimOfUsersManager(loggedInUser.getIdUser());
                if (interimForManager != null) {
                    if (UserS.isUsersManagerOnLeave(interimForManager.getIdUser())) {
                        List<User> subordinates = UserS.getSubordinatesIfManagerOnLeave(interimForManager.getIdUser());
                        users.addAll(subordinates);
                    } else {
                    }
                }
            }
            for (Conge conge : conges) {
                for (User user : users) {
                    if (conge.getIdUser() == user.getIdUser()) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/UserCarte.fxml"));
                        try {
                            Pane cardBox = fxmlLoader.load();
                            UserCarteController cardu = fxmlLoader.getController();
                            cardu.setData(conge, user);
                            cardBox.prefWidthProperty().bind(DemandesContainer.widthProperty());
                            DemandesContainer.add(cardBox, column, row);
                            GridPane.setMargin(cardBox, new Insets(10));
                            cardBox.setMaxWidth(Double.MAX_VALUE);
                            column++;
                            if (column == 1) {
                                column = 0;
                                row++;
                            }
                        } catch (IOException e) {
                            System.err.println("Error loading UserCarte.fxml: " + e.getMessage());
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in load method: " + e.getMessage());
            e.printStackTrace();
        }*/
    }


    @FXML
    void LesApprouves(ActionEvent event) {
        load(App);
    }

    @FXML
    void LesEnAttente(ActionEvent event) {
        load(EnAtt);
    }

    @FXML
    void LesRejetes(ActionEvent event) {
        load(Reg);
    }

    @FXML
    void TriPar(ActionEvent event) {
        String TYPE = comboTri.getValue();
        if (TYPE != null) {
            switch (TYPE) {
                case "Type":
                    triGenerique(UserS.TriType());
                    break;
                case "Nom":
                    triGenerique(UserS.TriNom());
                    break;
                case "Prenom":
                    triGenerique(UserS.TriPrenom());
                    break;
                case "Date Debut":
                    triGenerique(UserS.TriDateDebut());
                    break;
                case "Date Fin":
                    triGenerique(UserS.TriDateFin());
                    break;
                default:
                    break;
            }
        }
    }

    private void triGenerique(UserConge userConge) {
        int row = 0;
        int column = 0;
        try {
            DemandesContainer.getChildren().clear();
            List<User> users = userConge.getUsers();
            List<Conge> conges = userConge.getConges();
            for (Conge conge : conges) {
                for (User user : users) {
                    if (conge.getIdUser() == user.getIdUser()) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/UserCarte.fxml"));
                        try {
                            Pane cardBox = fxmlLoader.load();
                            UserCarteController cardu = fxmlLoader.getController();
                            cardu.setData(conge, user);
                            DemandesContainer.add(cardBox, column, row);
                            GridPane.setMargin(cardBox, new Insets(10));
                            cardBox.setMaxWidth(Double.MAX_VALUE);
                            column++;
                            if (column == 1) {
                                column = 0;
                                row++;
                            }
                        } catch (IOException e) {
                            System.err.println("Error loading UserCarte.fxml: " + e.getMessage());
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
