package tn.bfpme.controllers.RHC;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.bfpme.models.Departement;
import tn.bfpme.models.Role;
import tn.bfpme.services.ServiceRole;
import tn.bfpme.utils.FontResizer;

import java.net.URL;
import java.util.*;

public class paneRoleController implements Initializable {
    @FXML
    private TextArea roleDescriptionField;
    @FXML
    private AnchorPane RolesPane;
    @FXML
    private ListView<Role> roleListView;
    @FXML
    private TextField roleNameField;
    @FXML
    private VBox roleParentVBox;
    @FXML
    private Button btnEnregistrer, btnAnnuler, btnAjouter, btnModifier, btnSupprimer, AddNewRoleCombo;
    @FXML
    private HBox ParentRoleHBbox, btnEAHbox, btnCRUDHbox;

    private boolean updatingComboBox = false;
    public Role selectedRole;
    private final ServiceRole roleService = new ServiceRole();
    private int state = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(RolesPane);
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                );
                stage.heightProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                );
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(100),
                        event -> FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                ));
                timeline.setCycleCount(1);
                timeline.play();
                stage.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                    if (isNowShowing) {
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight());
                    }
                });
            }
        });
        loadRoles();
        roleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                roleNameField.setText(newValue.getNom());
                roleDescriptionField.setText(newValue.getDescription());
                populateParentRolesComboBoxes(newValue.getIdRole());
                btnAjouter.setDisable(true);
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
            }
        });
        fieldsDisable(true);
        btnCRUDHbox.setVisible(true);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnEAHbox.setVisible(false);
    }

    @FXML
    public void Annuler() {
        reset();
    }

    @FXML
    public void Enregistrer() {
        if (state == 1) { // Adding a new role
            String name = roleNameField.getText();
            String description = roleDescriptionField.getText();
            if (roleNameField.getText().isEmpty() || roleDescriptionField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
                return;
            }
            Role newRole = roleService.addRole2(name, description, 0);
            int id_role = newRole.getIdRole();
            if (newRole != null) {
                for (Node node : roleParentVBox.getChildren()) {
                    if (node instanceof HBox) {
                        HBox hBox = (HBox) node;
                        for (Node childNode : hBox.getChildren()) {
                            if (childNode instanceof ComboBox) {
                                ComboBox<Role> comboBox = (ComboBox<Role>) childNode;
                                Role parentRole = comboBox.getValue();
                                if (parentRole != null) {
                                    roleService.addRoleHierarchy(parentRole.getIdRole(), newRole.getIdRole());
                                }
                            }
                        }
                    }
                }
                showSuccess("Message de succès", "Le rôle a été ajouté avec succès.");
                loadRoles();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout du rôle échoué", "Le rôle n'a pas pu être ajouté.");
            }
            roleListView.setDisable(false);
            state = 0;
            fieldsDisable(true);
            btnEAHbox.setVisible(false);
            btnCRUDHbox.setVisible(true);
        }
        if (state == 2) { // Modifying an existing role
            Role selectedRole = roleListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                String name = roleNameField.getText();
                String description = roleDescriptionField.getText();
                if (roleNameField.getText().isEmpty() || roleDescriptionField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
                    return;
                }
                roleService.updateRole(selectedRole.getIdRole(), name, description);
                List<Role> currentParents = roleService.getRoleParents2(selectedRole.getIdRole());
                Set<Role> newParentsSet = new HashSet<>();
                for (Node node : roleParentVBox.getChildren()) {
                    if (node instanceof HBox) {
                        HBox hBox = (HBox) node;
                        for (Node childNode : hBox.getChildren()) {
                            if (childNode instanceof ComboBox) {
                                ComboBox<Role> comboBox = (ComboBox<Role>) childNode;
                                Role parentRole = comboBox.getValue();
                                if (parentRole != null) {
                                    newParentsSet.add(parentRole);
                                }
                            }
                        }
                    }
                }
                List<Role> newParents = new ArrayList<>(newParentsSet);
                for (Role currentParent : currentParents) {
                    if (!newParents.contains(currentParent)) {
                        roleService.removeRoleHierarchy(currentParent.getIdRole(), selectedRole.getIdRole());
                    }
                }
                for (Role newParent : newParents) {
                    if (!currentParents.contains(newParent)) {
                        roleService.addRoleHierarchy(newParent.getIdRole(), selectedRole.getIdRole());
                    }
                }
                showSuccess("Message de succès", "Le rôle a été modifié avec succès");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun rôle sélectionné", "Veuillez sélectionner un rôle à modifier.");
            }
            loadRoles();
            roleListView.setDisable(false);
            state = 0;
            fieldsDisable(true);
            btnEAHbox.setVisible(false);
            btnCRUDHbox.setVisible(true);
        }
    }

    private void populateParentRolesComboBoxes(int roleId) {
        roleParentVBox.getChildren().clear();
        List<Role> parentRoles = roleService.getParentRoles2(roleId);
        List<Role> allRoles = roleService.getAllRoles();
        allRoles.removeIf(role -> role.getIdRole() == roleId);
        ObservableList<Role> allRolesObservable = FXCollections.observableArrayList(allRoles);

        // Sort parentRoles by Level in ascending order
        parentRoles.sort(Comparator.comparingInt(Role::getLevel));

        for (Role parentRole : parentRoles) {
            ComboBox<Role> comboBox = createRoleComboBox(allRolesObservable);
            comboBox.setValue(parentRole);
            addComboBoxToVBox(comboBox);
        }
    }

    @FXML
    private void handleAddRole() {
        state = 1;
        btnCRUDHbox.setVisible(false);
        btnEAHbox.setVisible(true);
        fieldsDisable(false);
        roleListView.setDisable(true);
        fieldsClear();
    }

    @FXML
    private void handleEditRole() {
        state = 2;
        btnCRUDHbox.setVisible(false);
        btnEAHbox.setVisible(true);
        fieldsDisable(false);
        roleListView.setDisable(true);
    }

    @FXML
    private void handleDeleteRole() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir supprimer ce rôle ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            try {
                Role selectedRole = roleListView.getSelectionModel().getSelectedItem();
                if (selectedRole != null) {
                    roleService.deleteRole(selectedRole.getIdRole());
                    loadRoles();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        reset();
    }

    @FXML
    private void addRole() {
        String roleNameToExclude = roleNameField.getText();
        Role selectedRole = roleListView.getSelectionModel().getSelectedItem();
        List<Role> allRoles = roleService.getAllRoles();
        List<Role> filteredRoles = new ArrayList<>();
        Set<Role> selectedRoles = new HashSet<>();

        for (Node node : roleParentVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                for (Node childNode : hBox.getChildren()) {
                    if (childNode instanceof ComboBox) {
                        ComboBox<Role> comboBox = (ComboBox<Role>) childNode;
                        Role selected = comboBox.getValue();
                        if (selected != null) {
                            selectedRoles.add(selected);
                        }
                    }
                }
            }
        }

        for (Role role : allRoles) {
            if (selectedRole != null && role.getIdRole() == selectedRole.getIdRole()) {
                continue;
            }
            if (role.getNom().equals(roleNameToExclude)) {
                continue;
            }
            if (!selectedRoles.contains(role)) {
                filteredRoles.add(role);
            }
        }

        // Sort roles by their level before adding to the ComboBox
        filteredRoles.sort(Comparator.comparingInt(Role::getLevel));

        ComboBox<Role> comboBox = createRoleComboBox(FXCollections.observableArrayList(filteredRoles));
        addComboBoxToVBox(comboBox);
    }

    private ComboBox<Role> createRoleComboBox(ObservableList<Role> items) {
        ComboBox<Role> comboBox = new ComboBox<>(items);
        comboBox.setPrefHeight(31);
        comboBox.setPrefWidth(281);

        comboBox.setCellFactory(param -> new ListCell<Role>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<Role>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });

        comboBox.setOnAction(e -> {
            if (!updatingComboBox) {
                updateComboBoxOptions();
            }
        });
        UpdateFontSize();
        return comboBox;
    }

    private void addComboBoxToVBox(ComboBox<Role> comboBox) {
        Button removeButton = new Button("-");
        removeButton.setPrefHeight(25);
        removeButton.setPrefWidth(25);
        removeButton.getStyleClass().addAll("btn-primary", "FontSize-12", "RobotoRegular");
        removeButton.setOnAction(e -> {
            roleParentVBox.getChildren().remove(comboBox.getParent());
            updateComboBoxOptions();
        });

        HBox hBox = new HBox(comboBox, removeButton);
        hBox.setSpacing(5);
        roleParentVBox.getChildren().add(hBox);
    }

    private void updateComboBoxOptions() {
        updatingComboBox = true;
        List<Role> allRoles = roleService.getAllRoles();
        Set<Role> selectedRoles = new HashSet<>();

        for (Node node : roleParentVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                for (Node childNode : hBox.getChildren()) {
                    if (childNode instanceof ComboBox) {
                        ComboBox<Role> comboBox = (ComboBox<Role>) childNode;
                        Role selected = comboBox.getValue();
                        if (selected != null) {
                            selectedRoles.add(selected);
                        }
                    }
                }
            }
        }

        for (Node node : roleParentVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                for (Node childNode : hBox.getChildren()) {
                    if (childNode instanceof ComboBox) {
                        ComboBox<Role> comboBox = (ComboBox<Role>) childNode;
                        Role selected = comboBox.getValue();
                        List<Role> filteredRoles = new ArrayList<>(allRoles);
                        filteredRoles.removeAll(selectedRoles);
                        if (selected != null && !filteredRoles.contains(selected)) {
                            filteredRoles.add(selected);
                        }
                        filteredRoles.sort(Comparator.comparingInt(Role::getLevel));
                        comboBox.setItems(FXCollections.observableArrayList(filteredRoles));
                        if (selected != null) {
                            comboBox.setValue(selected);
                        }
                    }
                }
            }
        }
        updatingComboBox = false;
    }

    void loadRoles() {
        try {
            List<Role> roleList = roleService.getAllRoles();
            ObservableList<Role> roles = FXCollections.observableArrayList(roleList);
            roleListView.setItems(roles);
            roleListView.setCellFactory(param -> new ListCell<Role>() {
                @Override
                protected void updateItem(Role item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
        } catch (Exception e) {
            showError("Failed to load roles: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showSuccess(String message, String titre) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fieldsDisable(boolean arg) {
        roleNameField.setDisable(arg);
        roleDescriptionField.setDisable(arg);
        roleParentVBox.setDisable(arg);
        AddNewRoleCombo.setDisable(arg);
    }

    private void fieldsClear() {
        roleNameField.clear();
        roleDescriptionField.clear();
        roleParentVBox.getChildren().clear();
    }

    void reset() {
        state = 0;
        btnCRUDHbox.setVisible(true);
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnEAHbox.setVisible(false);
        fieldsDisable(true);
        roleListView.setDisable(false);
        fieldsClear();
    }

    void UpdateFontSize(){
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(RolesPane);
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                );
                stage.heightProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                );
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(100),
                        event -> FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight())
                ));
                timeline.setCycleCount(1);
                timeline.play();
                stage.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                    if (isNowShowing) {
                        FontResizer.resizeFonts(RolesPane, stage.getWidth(), stage.getHeight());
                    }
                });
            }
        });
    }
}
