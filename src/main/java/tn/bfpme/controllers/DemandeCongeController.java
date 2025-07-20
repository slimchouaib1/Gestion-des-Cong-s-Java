package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.bfpme.models.Conge;
import tn.bfpme.models.Statut;
import tn.bfpme.models.TypeConge;
import tn.bfpme.services.*;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class DemandeCongeController implements Initializable {
    @FXML
    private DatePicker datedebut;
    @FXML
    private DatePicker datefin;
    @FXML
    private TextArea Desc;
    @FXML
    private TextField Doc_Link;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private Label TypeTitle, ForDoc1;
    @FXML
    private ComboBox<TypeConge> cb_typeconge;
    @FXML
    private Pane paneConge;
    @FXML
    private HBox ForDoc2;

    private final ServiceTypeConge serviceTypeConge = new ServiceTypeConge();
    private final ServiceUserSolde serviceUserSolde = new ServiceUserSolde();
    private final ServiceConge serviceConge = new ServiceConge();
    private final ServiceNotification notifService = new ServiceNotification();
    private final ServiceUtilisateur userService = new ServiceUtilisateur();
    LocalDate currentDate = LocalDate.now();
    String NotifSubject = "";
    String messageText = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            Stage stage = FontResizer.getStageFromNode(MainAnchorPane);
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
                stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
                FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
            }
        });
        List<TypeConge> typeConges = serviceTypeConge.getAllTypeConge();
        if (typeConges != null && !typeConges.isEmpty()) {
            ObservableList<TypeConge> observableTypeConges = FXCollections.observableArrayList(typeConges);
            cb_typeconge.setItems(observableTypeConges);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Type de congé non disponible", "Aucun type de congé n'a été trouvé dans la base de données.");
        }
        cb_typeconge.setConverter(new StringConverter<TypeConge>() {
            @Override
            public String toString(TypeConge typeConge) {
                return typeConge != null ? typeConge.getDesignation() : "";
            }

            @Override
            public TypeConge fromString(String string) {
                return cb_typeconge.getItems().stream().filter(TypeConge ->
                        TypeConge.getDesignation().equals(string)).findFirst().orElse(null);
            }
        });
        cb_typeconge.setOnAction(this::TypeSelec);
        ForDoc1.setVisible(false);
        ForDoc2.setVisible(false);
        paneConge.setVisible(false);
    }

    @FXML
    void Demander(ActionEvent event) {
        LocalDate DD = datedebut.getValue();
        LocalDate DF = datefin.getValue();
        String DOCLINK = Doc_Link.getText();
        String DESC = Desc.getText();
        TypeConge selectedTypeConge = cb_typeconge.getSelectionModel().getSelectedItem();
        if (selectedTypeConge == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Type de congé non sélectionné", "Veuillez sélectionner un type de congé.");
            return;
        }
        int IDTYPE = selectedTypeConge.getIdTypeConge();
        boolean requiresFile = selectedTypeConge.isFile();
        String docLinkToUse = requiresFile ? DOCLINK : null;
        if (datedebut.getValue() == null || datefin.getValue() == null || Desc.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
            return;
        }
        if (DD.isBefore(currentDate) || DF.isBefore(currentDate) || DD.isAfter(DF)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Dates invalides", "La date de début et la date de fin doivent être postérieures ou égales à la date actuelle, et la date de début doit être antérieure ou égale à la date de fin.");
            return;
        }
        long daysBetween = ChronoUnit.DAYS.between(DD, DF) + 1;
        int userId = SessionManager.getInstance().getUser().getIdUser();
        double currentBalance = getCurrentBalance(userId, IDTYPE);
        if (selectedTypeConge.getLimite() > 0 && daysBetween > selectedTypeConge.getLimite()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Limite de congé dépassée", "La période demandée dépasse la limite autorisée pour ce type de congé.");
            return;
        }
        if (currentBalance < daysBetween) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Solde insuffisant", "Votre solde est insuffisant pour la période demandée.");
            return;
        }
        boolean success = serviceUserSolde.requestLeave(userId, IDTYPE, daysBetween);
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Solde insuffisant", "Votre solde est insuffisant pour la période demandée.");
            return;
        }

        int ID_MANAGER = userService.getManagerIdByUserId(SessionManager.getInstance().getUser().getIdUser());
        if(userService.isUser_OFFDUTY(ID_MANAGER)){
            ID_MANAGER = userService.getHisInterim(ID_MANAGER);
        }

        serviceConge.AddConge(new Conge(0, DD, DF, IDTYPE, Statut.En_Attente, userId, docLinkToUse, DESC));
        String NotifSubject = "Vous avez reçu une nouvelle demande de congé " + selectedTypeConge.getDesignation();
        String messageText = "Vous avez reçu une nouvelle demande de congé " + selectedTypeConge.getDesignation() + " de la part de " + SessionManager.getInstance().getUser().getNom() + " " + SessionManager.getInstance().getUser().getPrenom() + " du " + DD + " au " + DF;
        notifService.NewNotification(ID_MANAGER, NotifSubject, 2, messageText);

        Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText("Demande de congé créée avec succès !");
        ButtonType buttonHistorique = new ButtonType("Aller à l'historique");
        successAlert.getButtonTypes().setAll(buttonHistorique);
        Optional<ButtonType> result = successAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonHistorique) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HistoriqueConge.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Historique congé");
                stage.show();
                StageManager.addStage("DemandeDepListe", stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Closing current scene...");
        }
    }

    private double getCurrentBalance(int userId, int typeCongeId) {
        String query = "SELECT TotalSolde FROM user_Solde WHERE ID_User = ? AND ID_TypeConge = ?";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, typeCongeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TotalSolde");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @FXML
    void Doc_Imp(ActionEvent event) {
        String documentPath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir votre document justificatif");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichier", "*.pdf", "*.docx"));
        Stage stage = (Stage) datedebut.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                Path destinationFolder = Paths.get("src/main/resources/assets/files");
                if (!Files.exists(destinationFolder)) {
                    Files.createDirectories(destinationFolder);
                }
                String fileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                Path destinationPath = destinationFolder.resolve(fileName);
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                documentPath = destinationPath.toString();
                System.out.println("Document uploaded successfully: " + documentPath);
                Doc_Link.setText(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void TypeSelec(ActionEvent event) {
        ForDoc1.setVisible(false);
        ForDoc2.setVisible(false);
        TypeConge selectedTypeConge = cb_typeconge.getSelectionModel().getSelectedItem();
        if (selectedTypeConge != null) {
            paneConge.setVisible(true);
            TypeTitle.setText("Congé " + selectedTypeConge.getDesignation());
            if (selectedTypeConge.isFile()) {
                ForDoc1.setVisible(true);
                ForDoc2.setVisible(true);
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
