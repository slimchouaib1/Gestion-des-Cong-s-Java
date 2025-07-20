package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.bfpme.models.Conge;
import tn.bfpme.services.ServiceConge;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.SessionManager;
import tn.bfpme.utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class HistoriqueCongeController implements Initializable {
    private final ServiceConge CongeS = new ServiceConge();
    private Connection cnx;
    @FXML
    private TextField Recherche_conge;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private GridPane congeContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
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

    public void load() {
        congeContainer.getColumnConstraints().clear();
        int row = 0;
        try {
            for (Conge conge : CongeS.afficher()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);
                CardBox.prefWidthProperty().bind(congeContainer.widthProperty()/*.divide(2).subtract(20)*/);
                congeContainer.add(CardBox, 0, row++);
                //GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                GridPane.setMargin(CardBox, new Insets(10));
                //GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Recherche(KeyEvent event) {
        String recherche = Recherche_conge.getText();
        int row = 0;
        try {
            congeContainer.getChildren().clear();
            for (Conge conge : CongeS.Rechreche(recherche)) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                AnchorPane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);
                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void tri_statut(ActionEvent actionEvent) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        congeContainer.getColumnConstraints().add(columnConstraints);
        congeContainer.setVgap(4);
        congeContainer.setPadding(new Insets(4));
        int row = 0;
        try {
            congeContainer.getChildren().clear();
            for (Conge conge : CongeS.TriparStatut()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);

                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void tri_datedeb(ActionEvent event) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        congeContainer.getColumnConstraints().add(columnConstraints);
        congeContainer.setVgap(4);
        congeContainer.setPadding(new Insets(4));
        int row = 0;
        try {
            congeContainer.getChildren().clear(); // Clear existing items

            for (Conge conge : CongeS.TriparDateD()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);

                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void tri_datefin(ActionEvent event) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        congeContainer.getColumnConstraints().add(columnConstraints);
        congeContainer.setVgap(4);
        congeContainer.setPadding(new Insets(4));
        int row = 0;
        try {
            congeContainer.getChildren().clear(); // Clear existing items

            for (Conge conge : CongeS.TriparDateF()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);

                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void tri_desc(ActionEvent event) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        congeContainer.getColumnConstraints().add(columnConstraints);
        congeContainer.setVgap(4);
        congeContainer.setPadding(new Insets(4));
        int row = 0;
        try {
            congeContainer.getChildren().clear(); // Clear existing items

            for (Conge conge : CongeS.TriparDesc()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);

                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void tri_type(ActionEvent event) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        congeContainer.getColumnConstraints().add(columnConstraints);
        congeContainer.setVgap(4);
        congeContainer.setPadding(new Insets(4));
        int row = 0;
        try {
            congeContainer.getChildren().clear(); // Clear existing items

            for (Conge conge : CongeS.TriparType()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/CongeCarte.fxml"));
                Pane CardBox = fxmlLoader.load();
                CongeCarteController cardC = fxmlLoader.getController();
                cardC.setData(conge);

                congeContainer.add(CardBox, 0, row++);
                GridPane.setMargin(CardBox, new Insets(4, 4, 4, 4));
                CardBox.setMaxWidth(Double.MAX_VALUE);
                congeContainer.setColumnSpan(CardBox, GridPane.REMAINING);
                GridPane.setHalignment(CardBox, javafx.geometry.HPos.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
