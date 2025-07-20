package tn.bfpme.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.MyDataBase;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class ListeEmployésController implements Initializable {
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private TextField Recherche_conge;
    @FXML
    private GridPane UserContainer;
    @FXML
    private ScrollPane scrollPane;

    ServiceUtilisateur UserS = new ServiceUtilisateur();
    Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::load);
        });
        Platform.runLater(() -> {
            Stage stage = (Stage) MainAnchorPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(MainAnchorPane, stage.getWidth(), stage.getHeight());
        });
    }

    public void load() {
        List<User> users = UserS.ShowUnder();
        UserContainer.getChildren().clear();
        int column = 0;
        int row = 0;
        int columns = calculateNumberOfColumns();
        try {
            for (User user : users) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/UserCard.fxml"));
                Pane userBox = fxmlLoader.load();
                UserCardController cardC = fxmlLoader.getController();
                cardC.HBoxBtns.setVisible(false);
                cardC.setDataUser(user);
                if (column == columns) {
                    column = 0;
                    ++row;
                }
                UserContainer.add(userBox, column++, row);
                GridPane.setMargin(userBox, new Insets(12));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateNumberOfColumns() {
        double scrollPaneWidth = scrollPane.getWidth();
        double userCardWidth = 332; // Width of your UserCard.fxml
        int columns = (int) (scrollPaneWidth / userCardWidth);
        return Math.max(columns, 1); // Ensure at least one column
    }

    @FXML
    void Recherche(KeyEvent event) {
        UserContainer.getChildren().clear();
        String recherche = Recherche_conge.getText();
        int column = 0;
        int row = 0;
        int columns = calculateNumberOfColumns();
        try {
            for (User user : UserS.RechercheUnder(recherche)) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/UserCard.fxml"));
                Pane userBox = fxmlLoader.load();
                UserCardController cardC = fxmlLoader.getController();
                cardC.HBoxBtns.setVisible(false);
                cardC.setDataUser(user);
                if (column == columns) {
                    column = 0;
                    ++row;
                }
                UserContainer.add(userBox, column++, row);
                GridPane.setMargin(userBox, new Insets(12));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
