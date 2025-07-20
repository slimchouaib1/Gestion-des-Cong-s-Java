package tn.bfpme.utils;

import tn.bfpme.models.Departement;
import tn.bfpme.models.Role;
import tn.bfpme.models.TypeConge;
import tn.bfpme.models.User;
import tn.bfpme.services.ServiceDepartement;
import tn.bfpme.services.ServiceRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private User user;

    private SessionManager(User user) {
        this.user = user;
    }

    public static SessionManager getInstance(User user) {
        if (instance == null) {
            instance = new SessionManager(user);
        }
        return instance;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager is not initialized. Call getInstance(User) first.");
        }
        return instance;
    }

    public void updateSession(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public static void cleanUserSession() {
        instance = null;
    }

    public Departement getUserDepartment() {
        return ServiceDepartement.getDepartmentById(user.getIdDepartement());
    }

    public String getUserDepartmentName() {
        Departement departement = getUserDepartment();
        return departement != null ? departement.getNom() : null;
    }

    public Departement getParentDepartment() {
        return ServiceDepartement.getParentDepartment(user.getIdDepartement());
    }

    public Role getUserRole() {
        return ServiceRole.getRoleByUserId(user.getIdUser());
    }

    public String getUserRoleName() {
        Role role = getUserRole();
        return role != null ? role.getNom() : null;
    }

    public List<Role> getParentRoles() {
        Role role = getUserRole();
        return ServiceRole.getParentRoles(role.getIdRole());
    }

    public List<Role> getChildRoles() {
        Role role = getUserRole();
        return ServiceRole.getChildRoles(role.getIdRole());
    }

    public User getUserChef() {
        Role userRole = getUserRole();
        List<Integer> parentRoleIds = ServiceRole.getParentRoleIds(userRole.getIdRole());
        if (parentRoleIds.isEmpty()) {
            return null;
        }

        String sql = "SELECT u.*, ur.ID_Role FROM user u JOIN user_role ur ON u.ID_User = ur.ID_User WHERE u.ID_Departement = ? AND ur.ID_Role IN (";
        for (int i = 0; i < parentRoleIds.size(); i++) {
            sql += parentRoleIds.get(i);
            if (i < parentRoleIds.size() - 1) {
                sql += ", ";
            }
        }
        sql += ") LIMIT 1";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, user.getIdDepartement());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User chef = new User(
                        rs.getInt("ID_User"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Email"),
                        rs.getString("MDP"),
                        rs.getString("Image"),
                        getUserSolde(rs.getInt("ID_User")),  // updated to fetch solde information
                        rs.getInt("ID_Departement"),
                        rs.getInt("ID_Role")
                );
                return chef;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserChefByDeptAndRole(String deptName, String roleName) {
        Departement departement = ServiceDepartement.getDepartmentByName(deptName);
        Role role = ServiceRole.getRoleByName(roleName);
        if (departement == null || role == null) {
            return null;
        }

        List<Integer> parentRoleIds = ServiceRole.getParentRoleIds(role.getIdRole());
        if (parentRoleIds.isEmpty()) {
            return null;
        }

        String sql = "SELECT u.*, ur.ID_Role FROM user u JOIN user_role ur ON u.ID_User = ur.ID_User WHERE u.ID_Departement = ? AND ur.ID_Role IN (";
        for (int i = 0; i < parentRoleIds.size(); i++) {
            sql += parentRoleIds.get(i);
            if (i < parentRoleIds.size() - 1) {
                sql += ", ";
            }
        }
        sql += ") LIMIT 1";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, departement.getIdDepartement());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User chef = new User(
                        rs.getInt("ID_User"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Email"),
                        rs.getString("MDP"),
                        rs.getString("Image"),
                        getUserSolde(rs.getInt("ID_User")),  // updated to fetch solde information
                        rs.getInt("ID_Departement"),
                        rs.getInt("ID_Role")
                );
                return chef;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TypeConge getUserSolde(int userId) {
        TypeConge typeconge = new TypeConge();
        String query = "SELECT ID_TypeConge, TotalSolde FROM user_solde WHERE ID_User = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int typeCongeId = rs.getInt("ID_TypeConge");
                int totalSolde = rs.getInt("TotalSolde");
                // Assuming SoldeConge has a method to set solde based on type
                typeconge.setIdTypeConge(typeCongeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeconge;
    }
}
