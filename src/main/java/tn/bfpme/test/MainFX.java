package tn.bfpme.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import javafx.stage.Stage;
import tn.bfpme.utils.StageManager;

import java.io.IOException;

public class MainFX extends Application {
    static {
        System.setProperty("java.library.path", "C:\\opencv\\build\\java\\x64");
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(Region.EU_CENTRAL_1) // Choose the region you are using
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // Your Rekognition code goes here

        // Close the client
        rekClient.close();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestion de Congés - Connection");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/imgs/logo_bfpme.png")));
            primaryStage.setScene(scene);
            primaryStage.show();
            Platform.runLater(() -> {
                primaryStage.setMaximized(true);
            });
            StageManager.addStage(primaryStage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
