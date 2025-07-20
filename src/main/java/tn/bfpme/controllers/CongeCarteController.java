package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tn.bfpme.models.*;
import tn.bfpme.models.Statut;
import tn.bfpme.models.TypeConge;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
public class CongeCarteController implements Initializable {
    @FXML
    private Pane Card;
    @FXML
    private Label cardStatus, cardType, cardDescription, cardDatefin, cardDatedeb;
    @FXML
    private Tooltip tooltip_desc, TTViewFile;
    @FXML
    private Button btnViewFile, btnViewMsg, btnEdit, btnDelete;
    private int cUser, cid;
    private String cdesc, cfile, cmessage;
    private LocalDate cdebut, cfin;
    private Statut cstatut;
    private TypeConge ctype;
    private Conge conge;
    private final ServiceConge CongeS = new ServiceConge();

    public void setData(Conge conge) {
        this.conge = conge;
        cardType.setText(conge.getDesignation());
        cardDatedeb.setText(String.valueOf(conge.getDateDebut()));
        cardDatefin.setText(String.valueOf(conge.getDateFin()));
        cardDescription.setText(conge.getDescription());
        cardStatus.setText(conge.getStatut() != null ? conge.getStatut().toString() : "Unknown Status");

        Connection cnx = MyDataBase.getInstance().getCnx();
        btnViewMsg.setDisable(true);
        String qry = "SELECT TypeConge, Statut FROM conge WHERE ID_User = ? AND ID_Conge = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(qry);
            stm.setInt(1, SessionManager.getInstance().getUser().getIdUser());
            stm.setInt(2, this.conge.getIdConge());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if (Statut.En_Attente.toString().equals(rs.getString("Statut"))) {
                    btnDelete.setDisable(false);
                    btnEdit.setDisable(false);
                }
                if (Statut.Rejeté.toString().equals(rs.getString("Statut"))) {
                    btnViewMsg.setDisable(false);
                }
                if ("Annuel".equals(rs.getString("TypeConge")) || "Sous_solde".equals(rs.getString("TypeConge"))) {
                    btnViewFile.setDisable(true);
                    TTViewFile = new Tooltip();
                    TTViewFile.setText("Fichier non disponible");
                    Tooltip.install(btnViewFile, TTViewFile);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tooltip_desc = new Tooltip();
        tooltip_desc.setText(conge.getDescription());
        tooltip_desc.getStyleClass().add("tooltip");
        tooltip_desc.setMaxWidth(200);
        tooltip_desc.setWrapText(true);

        String css = getClass().getResource("/assets/css/style.css").toExternalForm();
        tooltip_desc.getScene().getStylesheets().add(css);

        Tooltip.install(cardDescription, tooltip_desc);
        cUser = conge.getIdUser();
        cid = conge.getIdConge();
        ctype = conge.getTypeConge();
        cdesc = conge.getDescription();
        cdebut = conge.getDateDebut();
        cfin = conge.getDateFin();
        cfile = conge.getFile();
        cstatut = conge.getStatut();
        cmessage = conge.getMessage();
    }

    public void refreshData(Conge conge) {
        setData(conge);
    }

    @FXML
    void modifConge(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierConge.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            ModifierCongeController modifierCongeController = loader.getController();
            modifierCongeController.setData(conge, this);
            newStage.setTitle("Modifier Congé");
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
            newStage.showAndWait();
            StageManager.addStage("ModifierConge", newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void suppConge(ActionEvent event) {
        CongeS.deleteCongeByID(cid);
        ((GridPane) Card.getParent()).getChildren().remove(Card);
    }

    @FXML
    void ViewFile(ActionEvent event) {
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
    void ViewMessage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Message.fxml"));
            Parent root = loader.load();
            MessageController msgController = loader.getController();
            msgController.setDataConge(cmessage);
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Stage stage = (Stage) Card.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight());
        });
    }
}
