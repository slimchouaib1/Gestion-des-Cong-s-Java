package tn.bfpme.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.bfpme.services.ServiceTypeConge;
import tn.bfpme.models.*;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ChatBot extends Application {
    @FXML
    private TextArea conversationArea;
    @FXML
    private TextField inputField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        conversationArea = new TextArea();
        conversationArea.setEditable(false);

        inputField = new TextField();
        inputField.setOnAction(event -> handleUserInput());

        VBox layout = new VBox(10, conversationArea, inputField);
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setTitle("Bot Congé");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void handleUserInput() {
        String userInput = inputField.getText();
        inputField.clear();

        conversationArea.appendText("You: " + userInput + "\n");

        // Handle the user input and generate a response
        String response = generateResponse(userInput);
        conversationArea.appendText("Bot: " + response + "\n");
    }

    private String generateResponse(String userInput) {
        if (userInput.toLowerCase().contains("crédits") || userInput.toLowerCase().contains("mon solde")) {
            String currentUserId = String.valueOf(SessionManager.getInstance().getUser().getIdUser());
            return getAllUserCredits(currentUserId);
        } else {
            return "I'm sorry, I don't understand the question.";
        }
    }
    private String getAllUserCredits(String userId) {
        ServiceTypeConge serviceTypeConge = new ServiceTypeConge();
        List<TypeConge> types = serviceTypeConge.getAllTypeConge(); // Fetch all types of leaves

        // Debug statement to check the number of types retrieved
        StringBuilder response = new StringBuilder();
        response.append("Voici vos soldes:\n");

        for (TypeConge type : types) {
            // Debug statement to check each TypeConge record
            //System.out.println("Debug: Processing TypeConge - ID: " + type.getID_TypeConge() + ", Designation: " + type.getDesignation());

            double solde = getSoldeForType(userId, type.getID_TypeConge());
            response.append("Type: ").append(type.getDesignation())
                    .append(", Solde: ").append(solde)
                    .append("\n");
        }

        return response.toString();
    }

    private double getSoldeForType(String userId, int typeCongeId) {
        String query = "SELECT TotalSolde FROM user_solde WHERE ID_User = ? AND ID_TypeConge = ?";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.setInt(2, typeCongeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Debug statement to check the solde fetched
                double solde = rs.getDouble("TotalSolde");
                System.out.println("Debug: Fetched solde for TypeConge ID: " + typeCongeId + " is " + solde);
                return solde;
            } else {
                // Debug statement if no solde is found
                System.out.println("Debug: No solde found for TypeConge ID: " + typeCongeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
