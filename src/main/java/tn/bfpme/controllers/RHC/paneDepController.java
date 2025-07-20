package tn.bfpme.controllers.RHC;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.bfpme.models.Departement;
import tn.bfpme.services.ServiceDepartement;
import javafx.scene.layout.*;
import tn.bfpme.utils.FontResizer;

import java.net.URL;
import java.util.*;

public class paneDepController implements Initializable {
    @FXML
    private ListView<Departement> departementListView;
    @FXML
    private TextField deptNameField, deptDescriptionField;
    @FXML
    private AnchorPane DepartementPane;
    @FXML
    private ComboBox<Departement> parentDeptComboBox;
    @FXML
    private VBox comboBoxContainer;
    private final ServiceDepartement depService = new ServiceDepartement();
    private RHController RHC;
    @FXML
    private Button Enregistrer, Delete, Update, Add ,btnRemoveFiter;
    @FXML
    private HBox Hfirst, Hsecond;

    private int state = 0;
    private paneUserController PUC;
    private ComboBox<Departement> lastSelectedComboBox = null;
    private Departement lastSelectedParent = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDepartments();
        departementListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                deptNameField.setText(newValue.getNom());
                deptDescriptionField.setText(newValue.getDescription());
                parentDeptComboBox.getSelectionModel().select(newValue.getParentDept() != 0 ? depService.getDepartmentById(newValue.getParentDept()) : null);
                Add.setDisable(true);
                Update.setDisable(false);
                Delete.setDisable(false);
                comboBoxContainer.getChildren().clear();
                addParentDepartmentComboBoxes(newValue);
            }
        });
        Platform.runLater(() -> {
            Stage stage = FontResizer.getStageFromNode(DepartementPane);
            if (stage != null) {
                stage.widthProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(DepartementPane, stage.getWidth(), stage.getHeight())
                );
                stage.heightProperty().addListener((obs, oldVal, newVal) ->
                        FontResizer.resizeFonts(DepartementPane, stage.getWidth(), stage.getHeight())
                );
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(100),
                        event -> FontResizer.resizeFonts(DepartementPane, stage.getWidth(), stage.getHeight())
                ));
                timeline.setCycleCount(1);
                timeline.play();
                stage.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                    if (isNowShowing) {
                        FontResizer.resizeFonts(DepartementPane, stage.getWidth(), stage.getHeight());
                    }
                });
            }
        });
        parentDeptComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                comboBoxContainer.getChildren().clear();
                lastSelectedParent = newSelection;
                addSubDepartmentComboBox(newSelection.getIdDepartement());
            } else {
                lastSelectedParent = null;
            }
        });
        Add.setDisable(false);
        Update.setDisable(true);
        Delete.setDisable(true);
        deptDescriptionField.setDisable(true);
        deptNameField.setDisable(true);
        comboBoxContainer.setDisable(true);
        parentDeptComboBox.setDisable(true);
        Hsecond.setDisable(true);
        Hsecond.setVisible(false);
        departementListView.setDisable(false);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);
    }

    @FXML
    private void handleAddDepartment() {
        state = 1;
        Hfirst.setVisible(false);
        Hfirst.setDisable(true);
        Hsecond.setDisable(false);
        Hsecond.setVisible(true);
        deptDescriptionField.setDisable(false);
        deptNameField.setDisable(false);
        comboBoxContainer.setDisable(false);
        parentDeptComboBox.setDisable(false);
        departementListView.setDisable(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleEditDepartment() {
        state = 2;
        Hfirst.setVisible(false);
        Hfirst.setDisable(true);
        Hsecond.setDisable(false);
        Hsecond.setVisible(true);
        deptDescriptionField.setDisable(false);
        deptNameField.setDisable(false);
        comboBoxContainer.setDisable(false);
        parentDeptComboBox.setDisable(false);
        departementListView.setDisable(true);
    }

    @FXML
    private void handleDeleteDepartment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir supprimer ce département ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == Oui) {
            try {
                Departement selectedDept = departementListView.getSelectionModel().getSelectedItem();
                if (selectedDept != null) {
                    depService.deleteDepartment(selectedDept.getIdDepartement());
                    loadDepartments();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        reset();
    }

    @FXML
    void AnuulerDepart(ActionEvent event) {
        reset();
    }

    @FXML
    void EnregistrerDepart(ActionEvent event) {
        if (state == 1) {
            String name = deptNameField.getText();
            String description = deptDescriptionField.getText();
            if (deptNameField.getText().isEmpty() || deptDescriptionField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs requis non remplis", "Veuillez remplir toutes les informations nécessaires.");
                return;
            }
            Departement parent = lastSelectedParent != null ? lastSelectedParent : parentDeptComboBox.getSelectionModel().getSelectedItem();
            if (parent == null) {
                depService.addDepartement2(name, description);
            } else {
                depService.addDepartement(name, description, parent.getIdDepartement() != 0 ? parent.getIdDepartement() : 0, parent.getLevel() + 1);
            }
            loadDepartments();
            reset();

        } else if (state == 2) {
            Departement selectedDept = departementListView.getSelectionModel().getSelectedItem();
            if (selectedDept != null) {
                String name = deptNameField.getText();
                String description = deptDescriptionField.getText();
                Departement parent = lastSelectedParent != null ? lastSelectedParent : parentDeptComboBox.getSelectionModel().getSelectedItem();
                depService.updateDepartment(selectedDept.getIdDepartement(), name, description, parent != null ? parent.getIdDepartement() : null);
                loadDepartments();
            }
            reset();
        }
        state = 0;
    }

    protected void loadDepartments() {
        try {
            List<Departement> departmentList = depService.getAllDepartments();
            List<Departement> departmentParentList = depService.getAllDepartmentParents();
            Departement noParentDept = new Departement(0, "Pas de département", "", 0);
            departmentList.add(0, noParentDept);

            ObservableList<Departement> departments = FXCollections.observableArrayList(departmentList);
            ObservableList<Departement> departmentParents = FXCollections.observableArrayList(departmentParentList);
            departementListView.setItems(departments);
            departementListView.setCellFactory(param -> new ListCell<Departement>() {
                @Override
                protected void updateItem(Departement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
            parentDeptComboBox.setItems(departmentParents);
            parentDeptComboBox.setCellFactory(param -> new ListCell<Departement>() {
                @Override
                protected void updateItem(Departement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
            parentDeptComboBox.setButtonCell(new ListCell<Departement>() {
                @Override
                protected void updateItem(Departement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
        } catch (Exception e) {
            showError("Failed to load departments: " + e.getMessage());
        }
    }

    private void addParentDepartmentComboBoxes(Departement department) {
        List<Departement> parentHierarchy = getParentHierarchy(department);
        Collections.reverse(parentHierarchy);
        for (Departement parent : parentHierarchy) {
            ComboBox<Departement> subDeptComboBox = new ComboBox<>();
            subDeptComboBox.setPrefHeight(31);
            subDeptComboBox.setPrefWidth(443);
            subDeptComboBox.setCellFactory(param -> new ListCell<Departement>() {
                @Override
                protected void updateItem(Departement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
            subDeptComboBox.setButtonCell(new ListCell<Departement>() {
                @Override
                protected void updateItem(Departement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getNom() == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });
            subDeptComboBox.setItems(FXCollections.observableArrayList(depService.getDepItsParent(parent.getParentDept())));
            subDeptComboBox.getSelectionModel().select(parent);
            subDeptComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    lastSelectedParent = newSelection;
                    addSubDepartmentComboBox(newSelection.getIdDepartement());
                }
            });
            comboBoxContainer.getChildren().add(subDeptComboBox);
            lastSelectedComboBox = subDeptComboBox;
        }
    }

    private List<Departement> getParentHierarchy(Departement department) {
        List<Departement> hierarchy = new ArrayList<>();
        while (department != null && department.getParentDept() != 0) {
            department = depService.getDepartmentById(department.getParentDept());
            if (department != null) {
                hierarchy.add(department);
            }
        }
        return hierarchy;
    }

    private void addSubDepartmentComboBox(int parentId) {
        List<Departement> subDepartments = depService.getDepItsParent(parentId);
        if (subDepartments.isEmpty()) {
            return;
        }
        ComboBox<Departement> subDeptComboBox = new ComboBox<>();
        subDeptComboBox.setPrefHeight(31);
        subDeptComboBox.setPrefWidth(443);
        subDeptComboBox.setCellFactory(param -> new ListCell<Departement>() {
            @Override
            protected void updateItem(Departement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNom() == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
        subDeptComboBox.setButtonCell(new ListCell<Departement>() {
            @Override
            protected void updateItem(Departement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNom() == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
        ObservableList<Departement> subDepartmentParents = FXCollections.observableArrayList(subDepartments);
        subDeptComboBox.setItems(subDepartmentParents);
        subDeptComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lastSelectedParent = newSelection;
                addSubDepartmentComboBox(newSelection.getIdDepartement());
            }
        });
        comboBoxContainer.getChildren().add(subDeptComboBox);
        lastSelectedComboBox = subDeptComboBox;
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    void reset() {
        departementListView.getSelectionModel().clearSelection();
        departementListView.setDisable(false);
        parentDeptComboBox.getSelectionModel().clearSelection();
        comboBoxContainer.getChildren().clear();
        deptNameField.setText("");
        deptDescriptionField.setText("");
        deptDescriptionField.setDisable(true);
        deptNameField.setDisable(true);
        comboBoxContainer.setDisable(true);
        parentDeptComboBox.setDisable(true);
        Add.setDisable(false);
        Update.setDisable(true);
        Delete.setDisable(true);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);
        Hsecond.setDisable(true);
        Hsecond.setVisible(false);
    }

    public void removeFilter(ActionEvent actionEvent) {
            // Clear any filters
            departementListView.getSelectionModel().clearSelection();

            // Reload all departments
            loadDepartments();

            // Reset any fields or UI elements related to filtering
            deptNameField.clear();
            deptDescriptionField.clear();
            parentDeptComboBox.getSelectionModel().clearSelection();
            comboBoxContainer.getChildren().clear();

            // Reset buttons and state
        Add.setDisable(false);
        Update.setDisable(true);
        Delete.setDisable(true);
        deptDescriptionField.setDisable(true);
        deptNameField.setDisable(true);
        comboBoxContainer.setDisable(true);
        parentDeptComboBox.setDisable(true);
        Hsecond.setDisable(true);
        Hsecond.setVisible(false);
        departementListView.setDisable(false);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);


    }
}
