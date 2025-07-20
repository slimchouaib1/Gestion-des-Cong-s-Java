package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.bfpme.controllers.RHC.RHController;
import tn.bfpme.controllers.RHC.paneUserController;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceUtilisateur;
import tn.bfpme.utils.FontResizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UserCardController implements Initializable {
    @FXML
    private Pane Card;
    @FXML
    private Label carddepart;
    @FXML
    private Label cardemail;
    @FXML
    private ImageView cardimage;
    @FXML
    private Label cardnameprename;
    @FXML
    public HBox HBoxBtns;
    @FXML
    private Label cardrole;

    private final ServiceUtilisateur UserS = new ServiceUtilisateur();
    int uid;
    String unom, uprenom, uemail, umdp, urole, udepart, updp;
    double SAnn, SExp, SMala, SMater;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(Card); // Replace rootAnchorPane with your actual root element
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                FontResizer.resizeFonts(Card, stage.getWidth(), stage.getHeight()); // Replace rootAnchorPane with your actual root element
            }
        });
    }
    public void setData(User user, String roleName, String departmentName) {
        String imagePath = user.getImage();
        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                FileInputStream inputStream = new FileInputStream(file);
                Image image = new Image(inputStream);
                cardimage.setImage(image);
            } catch (FileNotFoundException e) {
                System.err.println("Image file not found: " + imagePath);
            }
        } else {
            System.err.println("Image path is null for user: " + user);
        }
        cardnameprename.setText(user.getNom() + " " + user.getPrenom());
        cardemail.setText(user.getEmail());
        cardrole.setText(roleName);
        carddepart.setText(departmentName);
        Card.setStyle("-fx-border-radius: 5px;-fx-border-color:#808080");

        uprenom = user.getPrenom();
        uid = user.getIdUser();
        unom = user.getNom();
        uemail = user.getEmail();
        umdp = user.getMdp();
        urole = roleName;
        udepart = departmentName;
        updp = user.getImage();
        /*SAnn = user.getSoldeAnnuel();
        SMala = user.getSoldeMaladie();
        SMater = user.getSoldeMaternite();
        SExp = user.getSoldeExceptionnel();*/
    }

    public void setDataUser(User user) {
        String imagePath = user.getImage();
        String DepName = UserS.getDepNameByUserID(user.getIdUser());
        String roleName = UserS.getRoleNameByUserID(user.getIdUser());
        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                FileInputStream inputStream = new FileInputStream(file);
                Image image = new Image(inputStream);
                cardimage.setImage(image);
            } catch (FileNotFoundException e) {
                System.err.println("Image file not found: " + imagePath);
            }
        } else {
            System.err.println("Image path is null for user: " + user);
        }
        cardnameprename.setText(user.getNom() + " " + user.getPrenom());
        cardemail.setText(user.getEmail());
        cardrole.setText(roleName);
        carddepart.setText(DepName);
        Card.setStyle("-fx-border-radius: 5px;-fx-border-color:#808080");

        uprenom = user.getPrenom();
        uid = user.getIdUser();
        unom = user.getNom();
        uemail = user.getEmail();
        umdp = user.getMdp();
        urole = roleName;
        udepart = DepName;
        updp = user.getImage();
       /* SAnn = user.getSoldeAnnuel();
        SMala = user.getSoldeMaladie();
        SMater = user.getSoldeMaternite();
        SExp = user.getSoldeExceptionnel();*/
    }

    @FXML
    void ModifierUser(ActionEvent event) {
        try {
            // Load the paneUsers.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paneUsers.fxml"));
            Parent paneUsersRoot = loader.load();
            paneUserController pUC = loader.getController();

            // Set user data in the paneUserController
            pUC.ID_A.setText(String.valueOf(uid));
            pUC.nom_A.setText(unom);
            pUC.Prenom_A.setText(uprenom);
            pUC.email_A.setText(uemail);
            pUC.MDP_A.setText(umdp);
            pUC.image_A.setText(updp);


            String imagePath = updp;
            if (imagePath != null) {
                try {
                    File file = new File(imagePath);
                    FileInputStream inputStream = new FileInputStream(file);
                    Image image = new Image(inputStream);
                    pUC.PDPimageHolder.setImage(image);
                } catch (FileNotFoundException e) {
                    System.err.println("Image file not found: " + imagePath);
                }
            }

            // Get the main layout controller
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/RH_Interface.fxml"));
            Parent mainRoot = mainLoader.load();
            RHController mainController = mainLoader.getController();

            // Set paneUsers.fxml into the PaneCont of RH_Interface.fxml
            mainController.getPaneCont().getChildren().setAll(paneUsersRoot);

            // Switch to the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void SupprimerUser(ActionEvent event) {
        UserS.DeleteByID(uid);
        ((GridPane) Card.getParent()).getChildren().remove(Card);
    }


}

