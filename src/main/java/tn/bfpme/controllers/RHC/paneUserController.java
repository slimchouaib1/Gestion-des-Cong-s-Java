package tn.bfpme.controllers.RHC;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.layout.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileOutputStream;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.Cell;
import tn.bfpme.models.*;
import tn.bfpme.services.*;
import tn.bfpme.utils.*;

import java.io.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Provider;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class paneUserController extends AttributionSoldeController implements Initializable {
    public int state = 0;
    @FXML
    private TextField Manager_field;
    @FXML
    private TextField Interim_field;
    public int state1 = 0;


    @FXML
    private Label InterimLabel;

    @FXML
    private TextField Depart_field;
    @FXML
    private TreeTableView<Role> roleTable;
    @FXML
    private TreeTableColumn<Role, Integer> idRoleColumn;
    @FXML
    private TreeTableColumn<Role, String> nomRoleColumn;
    @FXML
    private TreeTableColumn<Role, String> DescRoleColumn;
    @FXML
    private TreeTableColumn<Role, Integer> RoleParColumn;
    @FXML
    private TreeTableColumn<Role, String> RoleFilsColumn;

    @FXML
    private TreeTableView<Departement> deptTable;
    @FXML
    private TreeTableColumn<Departement, Integer> idDapartementColumn;
    @FXML
    private TreeTableColumn<Departement, String> nomDeptColumn;
    @FXML
    private TreeTableColumn<Departement, String> DescriptionDeptColumn;
    @FXML
    private TreeTableColumn<Departement, Integer> DeptparColumn;

    @FXML
    private TreeTableView<User> userTable;
    @FXML
    private TreeTableColumn<User, Integer> idUserColumn;
    @FXML
    private TreeTableColumn<User, String> prenomUserColumn;
    @FXML
    private TreeTableColumn<User, String> nomUserColumn;
    @FXML
    private TreeTableColumn<User, String> departUserColumn;
    @FXML
    private TreeTableColumn<User, String> roleUserColumn;
    @FXML
    private TreeTableColumn<User, String> managerUserColumn;

    @FXML
    private ComboBox<String> RoleComboFilter;

    @FXML
    private TextField Role_field;
    @FXML
    private Button ExporterExcelButton;
    @FXML
    public ListView<User> userListView;
    @FXML
    public TextField User_field;
    @FXML
    public TextField ID_A;
    @FXML
    public TextField MDP_A;
    @FXML
    public ImageView PDPimageHolder;
    @FXML
    public TextField Prenom_A;

    @FXML
    private TextField searchFieldDept;
    @FXML
    private TextField searchFieldRole;
    @FXML
    private TextField searchFieldUser;
    @FXML
    private TextField RechercheBarUser;
    @FXML
    private ComboBox<String> hierarCombo;
    @FXML
    private ListView<Role> roleListView;
    @FXML
    private TextField RoleSearchBar;
    @FXML
    private VBox CongeVbox;
    @FXML
    private Button modifier_user, saveButton;
    @FXML
    private Pane paneSoldeUsers;
    private TreeItem<User> originalRoot;
    private TreeItem<Departement> originalDeptRoot;


    @FXML
    public GridPane UserContainers;
    @FXML
    private Label affectationlabel;
    @FXML
    public Pane UtilisateursPane;
    @FXML
    public ListView<Departement> departListView;
    @FXML
    public TextField email_A;
    @FXML
    public TextField image_A;
    @FXML
    public Label infolabel;
    @FXML
    public TextField nom_A;
    @FXML
    private Pane DepartPane1;
    @FXML
    private Pane RolePane1;
    @FXML
    private Pane UserPane1;

    @FXML
    private ListView<User> interim_listview;

    @FXML
    private ListView<User> manager_listview;
int stateInt =0 ;

    @FXML
    private HBox Hint1;

    @FXML
    private Button Hint1A;

    @FXML
    private Button Hint1M;

    @FXML
    private Button Hint1S;

    @FXML
    private HBox Hint2;

    @FXML
    private Button Hint2A;

    @FXML
    private Button Hint2E;


    @FXML
    public Button removeFilterButton, adduserbtn, toggleButton, toggleButtonR, toggleButtonDep;

    @FXML
    private Tab TabAffectationid;


    public User selectedUser;
    public User selectedManager;
    public User selectedInterim;
    public FilteredList<User> filteredData;
    public FilteredList<Departement> filteredDepartments;
    public FilteredList<Role> filteredRoles;
    public FilteredList<User> filteredManager;
    public FilteredList<User> filteredInterim;
    private final ServiceDepartement depService = new ServiceDepartement();
    private final ServiceUtilisateur userService = new ServiceUtilisateur();
    private final ServiceRole roleService = new ServiceRole();

    private final ServiceSubordinateManager usersubordinateService = new ServiceSubordinateManager(roleService, depService);
    private final ServiceUserSolde serviceUserSolde = new ServiceUserSolde();
    private final ServiceTypeConge serviceTypeConge = new ServiceTypeConge();

    private LeaveBalanceService leaveBalanceService;
    private AttributionSoldeController attributionSoldeController;
    private ObservableList<User> users;

    private ChangeListener<User> userSelectionListener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            handleUserSelection(newValue);
        }
    };
    private ChangeListener<User> userSelection1Listener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            try {
                handleManagerSelection(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    };
    ServiceUtilisateur UserS = new ServiceUtilisateur();
    Connection cnx = MyDataBase.getInstance().getCnx();
    ObservableList<String> HierarchieList = FXCollections.observableArrayList("Utilisateurs", "Départements");
    @FXML
    public Button Annuler;
    @FXML
    public Button Enregistrer;
    @FXML
    public HBox Hfirst;
    @FXML
    public Button upload;
    @FXML
    private Button Annuler2, handleremove2, handleedit2, Enregistrer2;
    @FXML
    private HBox Hfirst2, hsecond2;
    private Image TriAZ;
    private Image TriZA;
    @FXML
    private ImageView toggleIcon, toggleIconDep, toggleIconR;
    private boolean isAscending1 = true;
    private boolean isAscending2 = true;
    private boolean isAscending3 = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetInt();
        reset2();
        loadUsers();
        loadUsers1();
        loadUsers3();
        loadDepartments1();
        setupSearch();
        setupSearch1();
        loadManagers();
        loadInterims();
        Platform.runLater(() -> {
            Stage stage = (Stage) UtilisateursPane.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(UtilisateursPane, stage.getWidth(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) -> FontResizer.resizeFonts(UtilisateursPane, stage.getWidth(), stage.getHeight()));
            FontResizer.resizeFonts(UtilisateursPane, stage.getWidth(), stage.getHeight());
        });
        TabAffectationid.setOnSelectionChanged(event -> {
            if (TabAffectationid.isSelected()) {
                resetAffectationTab();
            }
        });
        TriAZ = new Image(getClass().getResourceAsStream("/assets/imgs/AZ.png"));
        TriZA = new Image(getClass().getResourceAsStream("/assets/imgs/ZA.png"));
        toggleIcon.setImage(TriAZ);
        toggleIconDep.setImage(TriAZ);
        toggleIconR.setImage(TriAZ);
        toggleButton.setOnAction(event -> {
            if (isAscending3) {
                loadTri(userService.TriUsersDESC());
                toggleIcon.setImage(TriZA);
            } else {
                loadTri(userService.TriUsersASC());
                toggleIcon.setImage(TriAZ);
            }
            isAscending3 = !isAscending3;
        });
        toggleButtonDep.setOnAction(event -> {
            if (isAscending2) {
                loadTri(userService.TriUserDepDESC());
                toggleIconDep.setImage(TriZA);
            } else {
                loadTri(userService.TriUserDepASC());
                toggleIconDep.setImage(TriAZ);
            }
            isAscending2 = !isAscending2;
        });
        toggleButtonR.setOnAction(event -> {
            if (isAscending1) {
                loadTri(userService.TriUserRolesDESC());
                toggleIconR.setImage(TriZA);
            } else {
                loadTri(userService.TriUserRolesASC());
                toggleIconR.setImage(TriAZ);
            }
            isAscending1 = !isAscending1;
        });

        loadRolesIntoComboBox();
        setupRemoveFilterButton();
        setupRoleSearchBar();

        if (SessionManager.getInstance().getUserRoleName().equals("AdminIT")) {
            adduserbtn.setDisable(true);
        }

        setupRoleComboBoxListener();
        loadDeparts3();
        loadRole1s();
        loadRoles3();
        hierarCombo.setValue("Selectioner type");
        hierarCombo.setItems(HierarchieList);

        userListView.getSelectionModel().selectedItemProperty().addListener(userSelectionListener);
        roleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleRoleSelection(newValue);
            }
        });
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleUserSelection(newValue);
            }
        });
        manager_listview.getSelectionModel().selectedItemProperty().addListener(userSelection1Listener);
        manager_listview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                   if (newValue != null) {
                       try {
                           handleManagerSelection(newValue);
                       } catch (SQLException e) {
                           throw new RuntimeException(e);
                       }
                   }
       });
       interim_listview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                  if (newValue != null) {
        handleInterimSelection(newValue);
    }
       });

        idUserColumn.setCellFactory(column -> new ColoredTreeCell());
        prenomUserColumn.setCellFactory(column -> new ColoredTreeCell());
        nomUserColumn.setCellFactory(column -> new ColoredTreeCell());
        roleUserColumn.setCellFactory(column -> new ColoredTreeCell());
        departUserColumn.setCellFactory(column -> new ColoredTreeCell());
        managerUserColumn.setCellFactory(column -> new ColoredTreeCell());
        userTable.setRowFactory(tv -> new ColoredTreeRow());
        originalRoot = userTable.getRoot();

        searchFieldUser.textProperty().addListener((observable, oldValue, newValue) -> filterTree(newValue));
        searchFieldDept.textProperty().addListener((observable, oldValue, newValue) -> filterDeptTree(newValue));
        deptTable.setRowFactory(tv -> new ColoredTreeRowDepartment()); // Apply department highlighting
        clearSoldeFields();
        CongeVbox.setPadding(new Insets(10, 0, 10, 0));
        CongeVbox.setSpacing(10);

        // Schedule the leave balance increment task
        LeaveBalanceService leaveBalanceService = new LeaveBalanceService(this);
        leaveBalanceService.start();
    }
    private void LOADERS() {
        loadUsers();
        loadUsers1();
        loadUsers3();
        loadDepartments1();
        loadDeparts3();
        loadRole1s();
        loadRoles3();
        loadManagers();
        loadInterims();
        loadRolesIntoComboBox();
        setupRemoveFilterButton();
        setupRoleSearchBar();
        setupRoleComboBoxListener();
        roleListView.getSelectionModel().select(-1);
        userListView.getSelectionModel().select(-1);
        departListView.getSelectionModel().select(-1);
        manager_listview.getSelectionModel().select(-1);
        interim_listview.getSelectionModel().select(-1);
        reset2();
    }

    private void filterTree(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            userTable.setRoot(originalRoot); // Reset to the original root when the search text is empty
            return;
        }

        TreeItem<User> filteredRoot = filterTreeItem(originalRoot, searchText.toLowerCase());
        userTable.setRoot(filteredRoot);
    }

    private TreeItem<User> filterTreeItem(TreeItem<User> item, String searchText) {
        if (item == null) {
            return null;
        }

        boolean matches = item.getValue().getNom().toLowerCase().contains(searchText) ||
                item.getValue().getPrenom().toLowerCase().contains(searchText) ||
                item.getValue().getEmail().toLowerCase().contains(searchText);

        TreeItem<User> filteredItem = new TreeItem<>(item.getValue());
        for (TreeItem<User> child : item.getChildren()) {
            TreeItem<User> filteredChild = filterTreeItem(child, searchText);
            if (filteredChild != null) {
                filteredItem.getChildren().add(filteredChild);
            }
        }

        if (matches || !filteredItem.getChildren().isEmpty()) {
            return filteredItem;
        } else {
            return null;
        }
    }

    private void handleUserSelection(User selectedUser) {
        this.selectedUser = selectedUser;
        if (selectedUser != null) {
            ID_A.setText(String.valueOf(selectedUser.getIdUser()));
            Prenom_A.setText(selectedUser.getPrenom());
            nom_A.setText(selectedUser.getNom());
            email_A.setText(selectedUser.getEmail());
            MDP_A.setText(selectedUser.getMdp());
            image_A.setText(selectedUser.getImage());
            if (selectedUser.getImage() != null && !selectedUser.getImage().isEmpty()) {
                try {
                    File file = new File(selectedUser.getImage());
                    FileInputStream inputStream = new FileInputStream(file);
                    Image image = new Image(inputStream);
                    PDPimageHolder.setImage(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Departement department = depService.getDepartmentById(selectedUser.getIdDepartement());
            if (department != null) {
                Depart_field.setText(department.getNom());
            } else {
                Depart_field.setText("No department");
            }
            Role role = roleService.getRoleByUserId(selectedUser.getIdUser());
            if (role != null) {
                Role_field.setText(role.getNom());
            } else {
                Role_field.setText("No role");
            }
            populateCongeSolde(selectedUser.getIdUser());
        } else {
        }
    }

    public void populateSoldeFields(User user) {
        List<UserSolde> soldeList = getSoldeCongeByUserId(user.getIdUser());
        CongeVbox.getChildren().clear(); // Clear existing entries
        for (UserSolde solde : soldeList) {
            Label congeTypeLabel = new Label(solde.getDesignation());
            congeTypeLabel.setPrefWidth(100); // Adjust the width as necessary
            congeTypeLabel.getStyleClass().add("label"); // Apply the same style class as other labels
            TextField soldeField = new TextField(String.valueOf(solde.getTotalSolde()));
            soldeField.setEditable(true); // Make the TextField editable
            soldeField.setPrefWidth(200); // Adjust the width as necessary
            soldeField.setMinWidth(200); // Adjust the width as necessary
            soldeField.getStyleClass().add("text-field"); // Apply the same style class as other text fields
            soldeField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double updatedSolde = Double.parseDouble(newValue);
                    solde.setTotalSolde(updatedSolde);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input: " + newValue);
                }
            });
            HBox soldeRow = new HBox(10); // Horizontal box with spacing
            soldeRow.setUserData(solde);
            soldeRow.getChildren().addAll(congeTypeLabel, soldeField);
            CongeVbox.getChildren().add(soldeRow);
        }
    }

    private void updateUserSoldeInDatabase(UserSolde solde) {
        String query = "UPDATE user_solde SET TotalSolde = ? WHERE ID_UserSolde = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setDouble(1, solde.getTotalSolde());
            stm.setInt(2, solde.getUD_UserSolde());
            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateCongeSolde(int userId) {
        List<UserSolde> soldeList = getSoldeCongeByUserId(userId);
        CongeVbox.getChildren().clear(); // Clear existing entries

        Map<Integer, Double> typeCongeLimits = getTypeCongeLimit();

        for (UserSolde solde : soldeList) {
            HBox soldeRow = new HBox(10); // Horizontal box with spacing
            Label congeTypeLabel = new Label(solde.getDesignation());
            TextField soldeField = new TextField(String.valueOf(solde.getTotalSolde()));
            soldeField.setEditable(true); // Make the TextField editable

            soldeField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double newSolde = Double.parseDouble(newValue);
                    double limit = typeCongeLimits.getOrDefault(solde.getID_TypeConge(), Double.MAX_VALUE);
                    if (newSolde > limit) {
                        newSolde = limit; // Ensure the new solde does not exceed the limit
                    }
                    solde.setTotalSolde(newSolde);
                    serviceUserSolde.updateUserSolde(solde); // Update database with UserSolde object
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input for solde: " + newValue); // Debugging
                }
            });

            soldeRow.getChildren().addAll(congeTypeLabel, soldeField);
            CongeVbox.getChildren().add(soldeRow);
        }
    }

    private Map<Integer, Double> getTypeCongeLimit() {
        Map<Integer, Double> limitMap = new HashMap<>();
        String query = "SELECT ID_TypeConge, Limit FROM typeconge";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                limitMap.put(rs.getInt("ID_TypeConge"), rs.getDouble("Limit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return limitMap;
    }

    private List<UserSolde> getSoldeCongeByUserId(int userId) {
        List<UserSolde> soldeCongeList = new ArrayList<>();
        String query = "SELECT us.*, tc.Designation FROM user_solde us " +
                "JOIN typeconge tc ON us.ID_TypeConge = tc.ID_TypeConge " +
                "WHERE us.ID_User = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UserSolde userSolde = new UserSolde(
                        rs.getInt("ID_UserSolde"),
                        rs.getInt("ID_User"),
                        rs.getInt("ID_TypeConge"),
                        rs.getDouble("TotalSolde"),
                        rs.getString("Designation")
                );
                soldeCongeList.add(userSolde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldeCongeList;
    }

    @FXML
    void SelecHierar(ActionEvent event) {
        if (hierarCombo.getValue().equals("Utilisateurs")) {
            UserPane1.setVisible(true);
            RolePane1.setVisible(false);
            DepartPane1.setVisible(false);
            userTable.setRowFactory(tv -> new ColoredTreeRow()); // Apply user highlighting
            searchFieldUser.textProperty().addListener((observable, oldValue, newValue) -> filterTree(newValue)); // Ensure user search is applied

        }
        if (hierarCombo.getValue().equals("Départements")) {
            UserPane1.setVisible(false);
            DepartPane1.setVisible(true);
            RolePane1.setVisible(false);
            deptTable.setRowFactory(tv -> new ColoredTreeRowDepartment()); // Apply department highlighting
        }
    }

    private void filterDeptTree(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            deptTable.setRoot(originalDeptRoot); // Reset to the original root when the search text is empty
            return;
        }

        TreeItem<Departement> filteredRoot = filterDeptTreeItem(originalDeptRoot, searchText.toLowerCase());
        deptTable.setRoot(filteredRoot);
    }

    private TreeItem<Departement> filterDeptTreeItem(TreeItem<Departement> item, String searchText) {
        if (item == null) {
            return null;
        }

        boolean matches = item.getValue().getNom().toLowerCase().contains(searchText) ||
                item.getValue().getDescription().toLowerCase().contains(searchText);

        TreeItem<Departement> filteredItem = new TreeItem<>(item.getValue());
        for (TreeItem<Departement> child : item.getChildren()) {
            TreeItem<Departement> filteredChild = filterDeptTreeItem(child, searchText);
            if (filteredChild != null) {
                filteredItem.getChildren().add(filteredChild);
            }
        }

        if (matches || !filteredItem.getChildren().isEmpty()) {
            return filteredItem;
        } else {
            return null;
        }
    }

    private void loadUsers() {
        UserContainers.getChildren().clear();
        List<User> userList = userService.getAllUsers();
        users = FXCollections.observableArrayList(userList);
        filteredData = new FilteredList<>(users, p -> true);

        int column = 0;
        int row = 0;
        try {
            for (User user : filteredData) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/RH_User_Card.fxml"));
                Pane userBox = fxmlLoader.load();
                CardUserRHController cardController = fxmlLoader.getController();
                Departement department = depService.getDepartmentById(user.getIdDepartement());
                Role role = roleService.getRoleByUserId(user.getIdUser());
                userBox.prefWidthProperty().bind(UserContainers.widthProperty()/*.divide(2).subtract(20)*/);
                String departmentName = department != null ? department.getNom() : "N/A";
                String roleName = role != null ? role.getNom() : "N/A";
                cardController.setData(user, roleName, departmentName);
                if (column == 1) {
                    column = 0;
                    row++;
                }
                UserContainers.add(userBox, column++, row);
                GridPane.setMargin(userBox, new javafx.geometry.Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTri(List<User> userList) {
        UserContainers.getChildren().clear();
        users = FXCollections.observableArrayList(userList);
        filteredData = new FilteredList<>(users, p -> true);
        int column = 0;
        int row = 0;
        try {
            for (User user : filteredData) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/RH_User_Card.fxml"));
                Pane userBox = fxmlLoader.load();
                CardUserRHController cardController = fxmlLoader.getController();
                Departement department = depService.getDepartmentById(user.getIdDepartement());
                Role role = roleService.getRoleByUserId(user.getIdUser());
                userBox.prefWidthProperty().bind(UserContainers.widthProperty());
                String departmentName = department != null ? department.getNom() : "N/A";
                String roleName = role != null ? role.getNom() : "N/A";
                cardController.setData(user, roleName, departmentName);
                if (column == 1) {
                    column = 0;
                    row++;
                }
                UserContainers.add(userBox, column++, row);
                GridPane.setMargin(userBox, new javafx.geometry.Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleRoleSelection(Role selectedRole) {
        try {
            List<Departement> departments;
            if ("Employe".equals(selectedRole.getNom())) {
                departments = depService.getAllDepartments();
            } else {
                departments = getRelatedDepartments(selectedRole.getIdRole());
            }
            ObservableList<Departement> observableList = FXCollections.observableArrayList(departments);
            departListView.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Une erreur s'est produite lors de la récupération des départements : " + e.getMessage());
        }
    }

    private void loadUsers1() {
        List<User> userList = userService.getAllUsers();
        ObservableList<User> users = FXCollections.observableArrayList(userList);
        filteredData = new FilteredList<>(users, p -> true);
        userListView.setItems(filteredData);
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getPrenom() + " " + user.getNom());
                }
            }
        });
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleUserSelection(newValue);
                filteredData.setPredicate(user -> user.equals(newValue));
            } else {
                filteredData.setPredicate(user -> true);
            }
        });
    }

    private void loadDepartments1() {
        List<Departement> departmentList = depService.getAllDepartments();
        ObservableList<Departement> departments = FXCollections.observableArrayList(departmentList);
        filteredDepartments = new FilteredList<>(departments, p -> true);
        departListView.setItems(filteredDepartments);
        departListView.setCellFactory(param -> new ListCell<Departement>() {
            @Override
            protected void updateItem(Departement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });

        departListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    Depart_field.setText(newValue.getNom());
                } else {
                    // Clear selection if newValue is null
                    Depart_field.clear();
                    departListView.getSelectionModel().clearSelection();
                }
            });
        });
    }

    private void loadRole1s() {
        List<Role> roleList = roleService.getAllRoles();
        ObservableList<Role> roles = FXCollections.observableArrayList(roleList);
        filteredRoles = new FilteredList<>(roles, p -> true);
        roleListView.setItems(filteredRoles);
        roleListView.setCellFactory(param -> new ListCell<Role>() {
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

        roleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    Role_field.setText(newValue.getNom());
                } else {
                    Role_field.setText("");
                }
            });
        });
    }

    public void loadUsers3() {
        try {
            List<User> userList = userService.getAllUsers();
            Map<Integer, User> userMap = userList.stream().collect(Collectors.toMap(User::getIdUser, user -> user));
            for (User user : userList) {
                User manager = userMap.get(user.getIdManager());
                if (manager != null) {
                    user.setManagerName(manager.getNom());
                } else {
                    user.setManagerName("Il n'y a pas de manager");
                }
                Departement department = userService.getDepartmentByUserId(user.getIdUser());
                if (department != null) {
                    user.setDepartementNom(department.getNom());
                } else {
                    user.setDepartementNom("sans département");
                }
                Role role = userService.getRoleByUserId(user.getIdUser());
                if (role != null) {
                    user.setRoleNom(role.getNom());
                } else {
                    user.setRoleNom("sans rôle");
                }
            }
            ObservableList<User> users = FXCollections.observableArrayList(userList);
            TreeItem<User> root = new TreeItem<>(new User(0, "", "", "", "", "", null, 0, 0, 0));
            root.setExpanded(true);
            Map<Integer, TreeItem<User>> treeItemMap = new HashMap<>();
            treeItemMap.put(0, root);
            for (User user : users) {
                TreeItem<User> item = new TreeItem<>(user);
                treeItemMap.put(user.getIdUser(), item);
            }
            for (User user : users) {
                TreeItem<User> item = treeItemMap.get(user.getIdUser());
                TreeItem<User> parentItem = treeItemMap.getOrDefault(user.getIdManager(), root);

                if (parentItem != null) {
                    parentItem.getChildren().add(item);
                }
            }
            userTable.setRoot(null);
            userTable.setRoot(root);
            userTable.setShowRoot(false);
            idUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("idUser"));
            prenomUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("prenom"));
            nomUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("nom"));
            roleUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("roleNom"));
            departUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("departementNom"));
            managerUserColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("managerName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoles3() {
        List<Role> roleList = roleService.getAllRoles();
        ObservableList<Role> roles = FXCollections.observableArrayList(roleList);
        TreeItem<Role> root = new TreeItem<>(new Role(0, "Sans role parent", "", 0)); // Adjust constructor as necessary
        root.setExpanded(true);
        Map<Integer, TreeItem<Role>> roleMap = new HashMap<>();
        roleMap.put(0, root);
        for (Role role : roles) {
            TreeItem<Role> item = new TreeItem<>(role);
            roleMap.put(role.getIdRole(), item);
            TreeItem<Role> parentItem = roleMap.getOrDefault(role.getRoleParent(), root);
            parentItem.getChildren().add(item);
        }
        for (Role role : roles) {
            TreeItem<Role> item = roleMap.get(role.getIdRole());
            TreeItem<Role> parentItem = roleMap.get(role.getRoleParent());
            if (parentItem != null && parentItem.getValue() != null) {
                role.setParentRoleName(parentItem.getValue().getNom());
            }
            for (TreeItem<Role> childItem : item.getChildren()) {
                role.setChildRoleName(childItem.getValue().getNom());
            }
        }
        roleTable.setRoot(root);
        roleTable.setShowRoot(false);
        idRoleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("idRole"));
        nomRoleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("nom"));
        DescRoleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        RoleParColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("parentRoleName"));
        RoleFilsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("childRoleName"));
    }

    private void loadDeparts3() {
        List<Departement> departmentList = depService.getAllDepartments();
        ObservableList<Departement> departments = FXCollections.observableArrayList(departmentList);
        TreeItem<Departement> root = new TreeItem<>(new Departement(0, "Sans dep.Parent", "", 0));
        root.setExpanded(true);
        Map<Integer, TreeItem<Departement>> departMap = new HashMap<>();
        departMap.put(0, root);
        for (Departement departement : departments) {
            TreeItem<Departement> item = new TreeItem<>(departement);
            departMap.put(departement.getIdDepartement(), item);
            TreeItem<Departement> parentItem = departMap.getOrDefault(departement.getParentDept(), root);
            parentItem.getChildren().add(item);
        }
        for (Departement departement : departments) {
            TreeItem<Departement> item = departMap.get(departement.getIdDepartement());
            TreeItem<Departement> parentItem = departMap.get(departement.getParentDept());
            if (parentItem != null && parentItem.getValue() != null) {
                departement.setParentDeptName(parentItem.getValue().getNom());
            }
        }
        deptTable.setRoot(root);
        deptTable.setShowRoot(false);
        idDapartementColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("idDepartement"));
        nomDeptColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("nom"));
        DescriptionDeptColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        DeptparColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("parentDeptName"));
    }

    private boolean isCurrentUser(int userId, String email) throws SQLException {
        User user = UserS.getUserById(userId);
        return user != null && user.getEmail().equals(email);
    }

    @FXML
    public void User_Recherche(KeyEvent event) {
        String searchText = User_field.getText().trim();
        filteredData.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getEmail().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    void Depart_Recherche(KeyEvent event) {
        String searchText = Depart_field.getText().trim();
        filteredDepartments.setPredicate(departement -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return (departement.getNom() != null && departement.getNom().toLowerCase().contains(lowerCaseFilter)) ||
                    (departement.getDescription() != null && departement.getDescription().toLowerCase().contains(lowerCaseFilter));
        });
    }

    @FXML
    void Role_Recherche(KeyEvent event) {
        String searchText = Role_field.getText().trim();
        filteredRoles.setPredicate(role -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return role.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    role.getDescription().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    void rechercheUser1(ActionEvent event) {
        String searchText = searchFieldUser.getText().trim();
        filteredData.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getEmail().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    void rechercheDept1(ActionEvent event) {
        String searchText = searchFieldDept.getText().trim();
        filteredDepartments.setPredicate(departement -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return (departement.getNom() != null && departement.getNom().toLowerCase().contains(lowerCaseFilter)) ||
                    (departement.getDescription() != null && departement.getDescription().toLowerCase().contains(lowerCaseFilter));
        });
    }

    @FXML
    void rechercheRole1(ActionEvent event) {
        String searchText = searchFieldRole.getText().trim();
        filteredRoles.setPredicate(role -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return role.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    role.getDescription().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    private void handleEditUser() {
        userListView.setDisable(false);
        roleListView.setDisable(false);
        departListView.setDisable(false);
        User_field.setDisable(false);
        Role_field.setDisable(false);
        Depart_field.setDisable(false);
        hsecond2.setVisible(false);
        hsecond2.setDisable(true);
        handleedit2.setDisable(true);
        handleremove2.setDisable(true);
        Hfirst2.setVisible(true);
        Hfirst2.setDisable(false);

    }


    @FXML
    private void handleRemoveUserAssignment() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Êtes vous sûrs?");
        alert.setHeaderText("Êtes-vous certain de vouloir supprimer ce role/département ?");
        ButtonType Oui = new ButtonType("Oui");
        ButtonType Non = new ButtonType("Non");
        alert.getButtonTypes().setAll(Oui, Non);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == Oui) {
            Integer userId = selectedUser.getIdUser();
            if (userId != null) {
                try {
                    usersubordinateService.removeRoleAndDepartment(userId);
                    affectationlabel.setText("Rôle et département supprimés.");
                    loadUsers3();
                    selectedUser.setIdDepartement(0);
                    resetAffectationTab(); // Reset the tab after deletion
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Une erreur s'est produite : " + e.getMessage());
                }
            } else {
                showError("Veuillez sélectionner un utilisateur pour supprimer l'affectation.");
            }
        }
        reset2();
    }

    @FXML
    void Enregistrer_user2(ActionEvent event) {
        if (selectedUser != null) {
            Departement selectedDepartement = departListView.getSelectionModel().getSelectedItem();
            Role selectedRole = roleListView.getSelectionModel().getSelectedItem();
            boolean isUpdated = false;
            try {
                if (selectedRole != null && selectedDepartement != null) {
                    usersubordinateService.assignRoleAndDepartment(selectedUser.getIdUser(), selectedRole.getIdRole(), selectedDepartement.getIdDepartement());
                    isUpdated = true;
                } else if (selectedRole != null) {
                    usersubordinateService.assignRoleAndDepartment(selectedUser.getIdUser(), selectedRole.getIdRole(), selectedUser.getIdDepartement());
                    isUpdated = true;
                } else if (selectedDepartement != null) {
                    usersubordinateService.assignRoleAndDepartment(selectedUser.getIdUser(), selectedUser.getIdRole(), selectedDepartement.getIdDepartement());
                    isUpdated = true;
                }
                if (isUpdated) {
                    loadUsers3();
                    affectationlabel.setText("Modification effectuée");
                    resetAffectationTab();
                } else {
                    showError("Veuillez sélectionner un rôle et/ou un département à attribuer.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Une erreur s'est produite : " + e.getMessage());
            }
        } else {
            showError("Veuillez sélectionner un utilisateur à modifier.");
        }
        reset2();
        LOADERS();
    }


    @FXML
    void Annuler_user2(ActionEvent event) {
        reset2();

    }

    void reset2() {
        userListView.setDisable(false);
        roleListView.setDisable(false);
        departListView.setDisable(false);
        userListView.getSelectionModel().clearSelection();
        roleListView.getSelectionModel().clearSelection();
        departListView.getSelectionModel().clearSelection();
        User_field.setDisable(false);
        User_field.setText("");
        Role_field.setDisable(false);
        Role_field.setText("");
        Depart_field.setDisable(false);
        Depart_field.setText("");
        hsecond2.setVisible(true);
        hsecond2.setDisable(false);
        handleedit2.setDisable(false);
        handleremove2.setDisable(false);
        Hfirst2.setVisible(false);
        Hfirst2.setDisable(true);
    }


    public Integer getSelectedUserId() {
        return selectedUser != null ? selectedUser.getIdUser() : null;
    }


    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void upload_image(ActionEvent event) {
        String imagePath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        Stage stage = (Stage) nom_A.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                Path destinationFolder = Paths.get("src/main/resources/assets/imgs");
                if (!Files.exists(destinationFolder)) {
                    Files.createDirectories(destinationFolder);
                }
                String fileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
                Path destinationPath = destinationFolder.resolve(fileName);
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = destinationPath.toString();
                image_A.setText("src/main/resources/assets/imgs/" + fileName);
                if (imagePath != null) {
                    try {
                        File file = new File(imagePath);
                        FileInputStream inputStream = new FileInputStream(file);
                        Image image = new Image(inputStream);
                        PDPimageHolder.setImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean emailExists(String email) throws SQLException {
        String query = "SELECT * FROM `user` WHERE Email=?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean emailExistss(String email, int excludeUserId) throws SQLException {
        String query = "SELECT * FROM `user` WHERE Email=?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @FXML
    public void RechercheBarUser(ActionEvent actionEvent) {
        String searchText = RechercheBarUser.getText().trim();
        filteredData.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                    user.getEmail().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void setupSearch() {
        RechercheBarUser.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
            refreshUserContainers();
        });
    }

    private void setupSearch1() {
        User_field.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                        user.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
            refreshUserContainers();
        });
    }

    private void refreshUserContainers() {
        UserContainers.getChildren().clear();
        int column = 0;
        int row = 0;
        try {
            for (User user : filteredData) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/RH_User_Card.fxml"));
                Pane userBox = fxmlLoader.load();
                CardUserRHController cardController = fxmlLoader.getController();
                Departement department = depService.getDepartmentById(user.getIdDepartement());
                Role role = new ServiceRole().getRoleByUserId(user.getIdUser());
                String departmentName = department != null ? department.getNom() : "N/A";
                String roleName = role != null ? role.getNom() : "N/A";
                cardController.setData(user, roleName, departmentName);
                if (column == 1) {
                    column = 0;
                    row++;
                }
                UserContainers.add(userBox, column++, row);
                GridPane.setMargin(userBox, new javafx.geometry.Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRolesIntoComboBox() {
        List<Role> roles = roleService.getAllRoles();
        ObservableList<String> roleNames = FXCollections.observableArrayList();
        for (Role role : roles) {
            roleNames.add(role.getNom());
        }
        RoleComboFilter.setItems(roleNames);
        resetRoleComboBoxItems();

    }

    @FXML
    public void filterByRoleCB(ActionEvent actionEvent) {
        String selectedRole = RoleComboFilter.getValue();
        if (selectedRole != null) {
            filteredData.setPredicate(user -> {
                Role userRole = roleService.getRoleById(user.getIdRole());
                return userRole != null && userRole.getNom().equals(selectedRole);
            });
            refreshUserContainers();
        }
    }

    private void setupRoleComboBoxListener() {
        RoleComboFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                Role userRole = roleService.getRoleByUserId(user.getIdUser());
                return userRole != null && userRole.getNom().equals(newValue);
            });
            loadFilteredUsers(); // Call method to refresh the displayed users
        });
    }

    private void loadFilteredUsers() {
        UserContainers.getChildren().clear();
        int column = 0;
        int row = 0;
        try {
            for (User user : filteredData) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/RH_User_Card.fxml"));
                Pane userBox = fxmlLoader.load();
                CardUserRHController cardController = fxmlLoader.getController();
                Departement department = depService.getDepartmentById(user.getIdDepartement());
                Role role = roleService.getRoleByUserId(user.getIdUser());
                String departmentName = department != null ? department.getNom() : "N/A";
                String roleName = role != null ? role.getNom() : "N/A";
                cardController.setData(user, roleName, departmentName);
                if (column == 1) {
                    column = 0;
                    row++;
                }
                UserContainers.add(userBox, column++, row);
                GridPane.setMargin(userBox, new javafx.geometry.Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SayebSelection(MouseEvent event) {
        LOADERS();
    }

    @FXML
    void removeFilters(ActionEvent event) {

    }

    private void setupRemoveFilterButton() {
        removeFilterButton.setOnAction(event -> {
            RoleComboFilter.getSelectionModel().clearSelection();
            filteredData.setPredicate(user -> true);
            loadFilteredUsers();
        });
    }

    private void setupRoleSearchBar() {
        RoleSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                RoleComboFilter.hide();
                resetRoleComboBoxItems();
                return;
            }

            RoleComboFilter.show();

            ObservableList<String> filteredRoles = FXCollections.observableArrayList();
            for (String role : RoleComboFilter.getItems()) {
                if (role.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredRoles.add(role);
                }
            }

            RoleComboFilter.setItems(filteredRoles);
            if (!filteredRoles.isEmpty()) {
                RoleComboFilter.show();
            }
        });
    }

    private void resetRoleComboBoxItems() {
        List<Role> roles = roleService.getAllRoles();
        ObservableList<String> roleNames = FXCollections.observableArrayList();
        for (Role role : roles) {
            roleNames.add(role.getNom());
        }
        RoleComboFilter.setItems(roleNames);
    }

    public void TabGestion(Event event) {
        reset();
    }

    public void clearuserselection(ActionEvent actionEvent) {
        Depart_field.setText("");
        User_field.setText("");
        Role_field.setText("");
        User_field.clear();
        userListView.getSelectionModel().clearSelection();
        Role_field.clear();
        roleListView.getSelectionModel().clearSelection();
        Depart_field.clear();
        departListView.getSelectionModel().clearSelection();
        filteredData.setPredicate(user -> true);
        filteredDepartments.setPredicate(departement -> true);
        filteredRoles.setPredicate(role -> true);

        // Refresh the list views
        userListView.setItems(filteredData);
        departListView.setItems(filteredDepartments);
        roleListView.setItems(filteredRoles);
    }

    public void clearroleselection(ActionEvent actionEvent) {
        Role_field.setText("");
        Role_field.clear();
        roleListView.getSelectionModel().clearSelection();
        filteredRoles.setPredicate(role -> true);
        roleListView.setItems(filteredRoles);
    }

    public void cleardepartselection(ActionEvent actionEvent) {
        Depart_field.setText("");
        Depart_field.clear();
        departListView.getSelectionModel().clearSelection();
        filteredDepartments.setPredicate(departement -> true);
        departListView.setItems(filteredDepartments);

    }

    @FXML
    private void TabAffectation(Event event) {
        if (TabAffectationid.isSelected()) {
            resetAffectationTab();
        }
    }

    private void resetAffectationTab() {
        affectationlabel.setText("");
        // Clear selection and fields
        User_field.clear();
        Depart_field.clear();
        Role_field.clear();
        userListView.getSelectionModel().clearSelection();
        departListView.getSelectionModel().clearSelection();
        roleListView.getSelectionModel().clearSelection();

        // Reset the filters to show all items
        filteredData.setPredicate(user -> true);
        filteredDepartments.setPredicate(departement -> true);
        filteredRoles.setPredicate(role -> true);

        // Refresh the list views
        userListView.setItems(filteredData);
        departListView.setItems(filteredDepartments);
        roleListView.setItems(filteredRoles);
    }

    @FXML
    public void ExporterExcel(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        Stage stage = (Stage) ExporterExcelButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            List<User> users = userService.getAllUsers(); // Fetch all users
            exportToExcel(users, String.valueOf(file));
        }
    }

    private void exportToExcel(List<User> users, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Utilisateurs");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Matricule");
        headerRow.createCell(1).setCellValue("Nom");
        headerRow.createCell(2).setCellValue("Prénom");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Mot de Passe");
        // headerRow.createCell(5).setCellValue("Image");
        headerRow.createCell(5).setCellValue("Département");
        headerRow.createCell(6).setCellValue("Rôle");
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getIdUser());
            row.createCell(1).setCellValue(user.getNom());
            row.createCell(2).setCellValue(user.getPrenom());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getMdp());

            // Fetch department and role names
            Departement department = depService.getDepartmentById(user.getIdDepartement());
            String departmentName = department != null ? department.getNom() : "N/A";
            row.createCell(5).setCellValue(departmentName);

            Role role = roleService.getRoleByUserId(user.getIdUser());
            String roleName = role != null ? role.getNom() : "N/A";
            row.createCell(6).setCellValue(roleName);
        }

        // Auto-size columns
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Departement> getRelatedDepartments(int roleId) throws SQLException {
        String query = "SELECT d.* FROM departement d JOIN role_departement rd ON d.ID_Departement = rd.ID_Departement WHERE rd.ID_Role = ?";
        List<Departement> departments = new ArrayList<>();
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            PreparedStatement stmt = cnx.prepareStatement(query);
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID_Departement");
                String name = rs.getString("nom");
                String description = rs.getString("description");
                int parentDeptId = rs.getInt("Parent_Dept");
                int level = rs.getInt("Level");
                Departement dept = new Departement(id, name, description, parentDeptId, level);
                departments.add(dept);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return departments;
    }

    @FXML
    void ajouter_user(ActionEvent actionEvent) {
        state = 1;
        ID_A.setDisable(false);
        email_A.setDisable(false);
        nom_A.setDisable(false);
        Prenom_A.setDisable(false);
        PDPimageHolder.setDisable(false);
        image_A.setDisable(true);
        MDP_A.setDisable(false);
        upload.setDisable(false);
        adduserbtn.setVisible(false);
        adduserbtn.setDisable(true);
        Hfirst.setVisible(true);
        Hfirst.setDisable(false);

    }

    @FXML
    void Enregistrer_user(ActionEvent event) {
        if (state == 1) {
            int idUser = Integer.parseInt(ID_A.getText());
            String nom = nom_A.getText();
            String prenom = Prenom_A.getText();
            String email = email_A.getText();
            String mdp = MDP_A.getText();
            String image = image_A.getText();

            try {
                // Encrypt the password
                String encryptedPassword = EncryptionUtil.encrypt(mdp);

                if (!emailExists(email)) {
                    User newUser = new User(idUser, nom, prenom, email, encryptedPassword, image, LocalDate.now());
                    UserS.AddUser_RH(newUser);
                    //int newUserId = UserS.getLastInsertedUserId();
                    List<TypeConge> typeConges = serviceTypeConge.getAllTypeConge();
                    for (TypeConge typeConge : typeConges) {
                        serviceUserSolde.addUserSolde(idUser, typeConge.getIdTypeConge(), 0.0);
                    }
                    infolabel.setText("Ajout Effectué");
                } else {
                    infolabel.setText("Email déjà existe");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            reset();

        } else if (state == 2) {
            int idUser = Integer.parseInt(ID_A.getText());

            String Nom = nom_A.getText();
            String Prenom = Prenom_A.getText();
            String Email = email_A.getText();
            String Mdp = MDP_A.getText();
            String Image = image_A.getText();
            int IdUser = Integer.parseInt(ID_A.getText());

            try {
                // Encrypt the password
                String encryptedPassword = EncryptionUtil.encrypt(Mdp);

                if (!emailExistss(Email, IdUser) || isCurrentUser(IdUser, Email)) {
                    User user = new User(IdUser, Nom, Prenom, Email, encryptedPassword, Image);
                    UserS.Update(user);
                    infolabel.setText("Modification Effectuée");
                } else {
                    infolabel.setText("Email déjà existe");
                }
            } catch (Exception e) {
                infolabel.setText("Erreur de base de données: " + e.getMessage());
                e.printStackTrace();
            }

            CongeVbox.getChildren().forEach(node -> {
                if (node instanceof HBox) {
                    HBox soldeRow = (HBox) node;
                    if (soldeRow.getChildren().size() == 2 && soldeRow.getChildren().get(1) instanceof TextField) {
                        TextField soldeField = (TextField) soldeRow.getChildren().get(1);
                        try {
                            double updatedSolde = Double.parseDouble(soldeField.getText());
                            UserSolde solde = (UserSolde) soldeRow.getUserData(); // Retrieve UserSolde from user data
                            if (solde != null) {
                                solde.setTotalSolde(updatedSolde);
                                updateUserSoldeInDatabase(solde); // Update in the database
                            } else {
                                System.err.println("Solde object is null for HBox: " + soldeRow);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid input: " + soldeField.getText());
                        }
                    }
                }
            });

            clearSoldeFields();
            reset();
        }
    }



    @FXML
    void Annuler_user(ActionEvent event) {
        clearSoldeFields();
        reset();
    }

    @FXML
    void unselect(MouseEvent event) {
        clearSoldeFields();
        reset();

    }

    void reset() {
        ID_A.setText("");
        email_A.setText("");
        nom_A.setText("");
        Prenom_A.setText("");
        PDPimageHolder.setImage(null);

        image_A.setText("");
        MDP_A.setText("");
        ID_A.setDisable(true);
        email_A.setDisable(true);
        nom_A.setDisable(true);
        Prenom_A.setDisable(true);
        PDPimageHolder.setDisable(true);
        image_A.setDisable(true);
        MDP_A.setDisable(true);
        upload.setDisable(true);
        adduserbtn.setVisible(true);
        adduserbtn.setDisable(false);
        Hfirst.setVisible(false);
        Hfirst.setDisable(true);
    }

    @FXML
    public void Listerefresh(Event event) {
        loadUsers();
    }

    private void clearSoldeFields() {
        CongeVbox.getChildren().clear();
    }

    @FXML
    public void RoleTrier(ActionEvent actionEvent) {
        if (isAscending1) {
            loadTri(userService.TriUserRolesDESC());
            toggleIconR.setImage(TriZA);
        } else {
            loadTri(userService.TriUserRolesASC());
            toggleIconR.setImage(TriAZ);
        }
        isAscending1 = !isAscending1;
    }

    @FXML
    void annulerInterim(ActionEvent event) {
        resetInt();
    }
    @FXML
    void affecterInterim(ActionEvent event) {
        stateInt=1;
        Hint1.setDisable(true);
        Hint1.setVisible(false);
        Hint2.setVisible(true);
        Hint2.setDisable(false);
        Manager_field.setDisable(false);
        Interim_field.setDisable(true);
        manager_listview.setDisable(false);
        interim_listview.setDisable(true);
        Hint2E.setText("Enregistrer");

    }
    @FXML
    void supprimerInterim(ActionEvent event) {
        stateInt=3;
        Hint1.setDisable(true);
        Hint1.setVisible(false);
        Hint2.setVisible(true);
        Hint2.setDisable(false);
        Manager_field.setDisable(false);
        Interim_field.setDisable(true);
        manager_listview.setDisable(false);
        interim_listview.setDisable(true);
        Hint2E.setText("Supprimer");

    }
    @FXML
    void modifierInterim(ActionEvent event) {
        stateInt=2;
        Hint1.setDisable(true);
        Hint1.setVisible(false);
        Hint2.setVisible(true);
        Hint2.setDisable(false);
        Manager_field.setDisable(false);
        Interim_field.setDisable(true);
        manager_listview.setDisable(false);
        interim_listview.setDisable(true);
        Hint2E.setText("Enregistrer");
    }
    @FXML
    void enregistrerInterim(ActionEvent event) {
        if (stateInt==1){
            if (selectedManager != null) {
                boolean isAffected = false;
                try {
                    if (selectedInterim != null) {
                        userService.assignInterimManager(selectedManager.getIdUser(), selectedInterim.getIdUser());
                        isAffected = true;
                    if (isAffected) {
                        InterimLabel.setText("Affectation effectuée");
                        resetInt();
                    } else {
                        showError("Veuillez sélectionner un Intérim à affecter.");
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Une erreur s'est produite : " + e.getMessage());
                }
            }else {
                showError("Veuillez sélectionner un Intérim à affecter.");
            }
        } else if (stateInt==2) {
            if (selectedManager != null) {
                User selectedInterim = interim_listview.getSelectionModel().getSelectedItem();
                boolean isUpdated = false;
                try {
                    if (selectedInterim != null) {
                        userService.updateInterimManager(selectedManager.getIdUser(), selectedInterim.getIdUser());
                        isUpdated = true;
                        if (isUpdated) {
                            InterimLabel.setText("Modification effectuée");
                            resetInt();
                        } else {
                            showError("Veuillez sélectionner un Intérim à modifier.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Une erreur s'est produite : " + e.getMessage());
                }
            }else {
                showError("Veuillez sélectionner un Intérim à modifier.");
            }
        }else if (stateInt == 3) {
            if (selectedManager != null) {
                boolean isDeleted = false;
                try {
                    // Show confirmation dialog
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText(null);
                    confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'intérim pour ce manager?");

                    // Capture the user's response
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        userService.deleteInterimManager(selectedManager.getIdUser());
                        isDeleted = true;
                        if (isDeleted) {
                            InterimLabel.setText("Intérim supprimé");
                            resetInt();
                        } else {
                            showError("Une erreur s'est produite lors de la suppression de l'intérim.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Une erreur s'est produite : " + e.getMessage());
                }
            } else {
                showError("Veuillez sélectionner un manager pour supprimer l'intérim.");
            }
        }

    }
    void resetInt(){
        Manager_field.setText("");
        Manager_field.setDisable(true);
        Interim_field.setDisable(true);
        Interim_field.setText("");

        manager_listview.setDisable(true);
        interim_listview.setDisable(true);
        manager_listview.getSelectionModel().clearSelection();
        interim_listview.getSelectionModel().clearSelection();
        Hint1.setDisable(false);
        Hint1.setVisible(true);
        Hint1A.setDisable(false);
        Hint1M.setDisable(false);
        Hint1S.setDisable(false);
        Hint2.setDisable(true);
        Hint2.setVisible(false);
    }
    private void handleInterimSelection(User selectedInterim) {
        this.selectedInterim = selectedInterim;
        if(selectedInterim!= null){

        }else{

        }

    }


    private void handleManagerSelection(User selectedManager) throws SQLException {
        this.selectedManager = selectedManager;
        if(selectedManager!= null && stateInt!=3) {
            manager_listview.setDisable(false);
            interim_listview.setDisable(false);
            manager_listview.getSelectionModel().clearSelection();
            interim_listview.getSelectionModel().clearSelection();
            Interim_field.setDisable(false);
            Manager_field.setDisable(false);
            User user =userService.getInterimByUserId(selectedManager.getIdUser());
            User user1=userService.getUserById(selectedManager.getIdUser());
            Manager_field.setText(user1.getNom()+" "+user1.getPrenom());
            if (user != null) {
                Interim_field.setText(user.getNom()+" "+user.getPrenom());
            } else {
                Interim_field.setText("No Intérim");
            }


        }else{

        }
    }

    @FXML
    void clearmanagerselection(ActionEvent event) {
        Manager_field.setText("");
        Interim_field.setText("");
        Manager_field.clear();
        manager_listview.getSelectionModel().clearSelection();
        Interim_field.clear();
        interim_listview.getSelectionModel().clearSelection();
        filteredManager.setPredicate(user -> true);
        filteredInterim.setPredicate(user -> true);

        // Refresh the list views
        manager_listview.setItems(filteredManager);
        interim_listview.setItems(filteredInterim);
    }
    @FXML
    void clearinterimselection(ActionEvent event) {
        Interim_field.setText("");
        Interim_field.clear();
        interim_listview.getSelectionModel().clearSelection();
    }
    private void loadManagers() {
        List<User> managerList = userService.getAllManagers();
        ObservableList<User> users = FXCollections.observableArrayList(managerList);
        filteredManager = new FilteredList<>(users, p -> true);
        manager_listview.setItems(filteredManager);
        manager_listview.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getPrenom() + " " + user.getNom());
                }
            }
        });
        manager_listview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    handleManagerSelection(newValue);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                filteredManager.setPredicate(user -> user.equals(newValue));
            } else {
                filteredManager.setPredicate(user -> true);
            }
        });
    }
    private void loadInterims() {
        List<User> interimList = userService.getAllManagers();
        ObservableList<User> users = FXCollections.observableArrayList(interimList);
        filteredInterim = new FilteredList<>(users, p -> true);
        interim_listview.setItems(filteredInterim);
        interim_listview.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getPrenom() + " " + user.getNom());
                }
            }
        });
        interim_listview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleInterimSelection(newValue);
                filteredInterim.setPredicate(user -> user.equals(newValue));
            } else {
                filteredInterim.setPredicate(user -> true);
            }
        });
    }


}
