package tn.bfpme.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import tn.bfpme.models.Conge;
import tn.bfpme.models.User;
import tn.bfpme.utils.FontResizer;
import tn.bfpme.utils.StageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserCarteController implements Initializable {
    @FXML
    private Pane UserCard;
    @FXML
    private Label email_card;
    @FXML
    private Label nomprenom_card;
    @FXML
    private ImageView pdp_card;
    @FXML
    private Label periode_card;
    @FXML
    private Label statut_card;
    @FXML
    private Label type_card;
    private Conge conge;
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(UserCard); // Replace rootAnchorPane with your actual root element
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight())); // Replace rootAnchorPane with your actual root element
                FontResizer.resizeFonts(UserCard, stage.getWidth(), stage.getHeight()); // Replace rootAnchorPane with your actual root element
            }
        });
    }

    public void setData(Conge conge, User user) {
        this.conge = conge;
        this.user = user;
        String imagePath = user.getImage();
        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                FileInputStream inputStream = new FileInputStream(file);
                Image image = new Image(inputStream);
                pdp_card.setImage(image);
            } catch (FileNotFoundException e) {
                //System.err.println("Image file not found: " + imagePath);
            }
        }
        nomprenom_card.setText(user.getPrenom() + " " + user.getNom());
        email_card.setText("Email: " + user.getEmail());
        periode_card.setText("De    " + conge.getDateDebut() + "   →   " + conge.getDateFin());
        statut_card.setText("Statut: " + conge.getStatut());
        type_card.setText("Type: " + conge.getDesignation());
        UserCard.setStyle("-fx-border-radius: 5px; -fx-border-color: #808080;");
    }

    @FXML
    void AfficherDemande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeDep.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            DemandeDepController DemDepC = loader.getController();
            DemDepC.setData(conge, user);
            newStage.setTitle("Demande de congé");
            newStage.setScene(new Scene(root));
            Stage demandeDepStage = new Stage();
            StageManager.addStage("DemandeDep", demandeDepStage);
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            newStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
