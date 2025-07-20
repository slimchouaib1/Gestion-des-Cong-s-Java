package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import tn.bfpme.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.StageManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifierCongeController implements Initializable {
    @FXML
    private Label text_info;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private DatePicker modif_datedeb;
    @FXML
    private DatePicker modif_datefin;
    @FXML
    private TextArea modif_description;
    private Conge conge;
    private CongeCarteController congeCarteController;
    private final ServiceConge CongeS = new ServiceConge();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Stage stage = (Stage) MainAnchorPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
        });
    }

    public void setData(Conge conge, CongeCarteController congeCarteController) {
        this.conge = conge;
        this.congeCarteController = congeCarteController;
        modif_datedeb.setValue(conge.getDateDebut());
        modif_datefin.setValue(conge.getDateFin());
        modif_description.setText(conge.getDescription());
    }

    @FXML
    void annuler_conge(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void modifier_conge(ActionEvent actionEvent) {
        LocalDate startDate = modif_datedeb.getValue();
        LocalDate endDate = modif_datefin.getValue();
        String description = modif_description.getText();
        if (startDate != null && endDate != null && !description.isEmpty()) {
            conge.setDateDebut(startDate);
            conge.setDateFin(endDate);
            conge.setDescription(description);
            CongeS.updateConge(conge);
            text_info.setText("Modification effectuée");
            Stage stage = (Stage) modif_datedeb.getScene().getWindow();
            stage.close();
            if (congeCarteController != null) {
                congeCarteController.refreshData(conge);
            }
        } else {
            text_info.setText("Veuillez remplir tous les champs");
        }
    }


}
