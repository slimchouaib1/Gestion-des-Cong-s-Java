package tn.bfpme.services;

import tn.bfpme.models.Role;
import tn.bfpme.models.RoleHierarchie;
import tn.bfpme.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRole {

    private static Connection cnx = MyDataBase.getInstance().getCnx();

    public static Role getRoleByUserId(int idUser) {
        Role role = null;
        String query = "SELECT r.* FROM role r JOIN user_role ur ON r.ID_Role = ur.ID_Role WHERE ur.ID_User = ?";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public Role getRoleById(int roleId) {
        Role role = null;
        String query = "SELECT * FROM role WHERE ID_Role = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement statement = cnx.prepareStatement(query);
            statement.setInt(1, roleId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                role = new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public static List<Role> getChildRoles(int parentRoleId) {
        List<Role> childRoles = new ArrayList<>();
        String query = "SELECT * FROM role r JOIN rolehierarchie rh ON r.ID_Role = rh.ID_RoleC WHERE rh.ID_RoleP = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement statement = cnx.prepareStatement(query);
            statement.setInt(1, parentRoleId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
                childRoles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return childRoles;
    }

    /*public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM role";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement statement = cnx.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }*/
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM role";
        try (Connection connection = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Role role = new Role();
                role.setIdRole(resultSet.getInt("ID_Role"));
                role.setNom(resultSet.getString("nom"));
                role.setDescription(resultSet.getString("description"));
                role.setLevel(resultSet.getInt("Level"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public static List<Role> getParentRoles(int idRole) {
        List<Role> parentRoles = new ArrayList<>();
        String query = "SELECT r.* FROM rolehierarchie rh JOIN role r ON rh.ID_RoleP = r.ID_Role WHERE rh.ID_RoleC = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idRole);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                parentRoles.add(new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parentRoles;
    }

    public static List<Integer> getParentRoleIds(int idRole) {
        List<Integer> parentRoleIds = new ArrayList<>();
        String query = "SELECT ID_RoleP FROM rolehierarchie WHERE ID_RoleC = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idRole);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                parentRoleIds.add(rs.getInt("ID_RoleP"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parentRoleIds;
    }

    public static Role getRoleByName(String name) {
        Role role = null;
        String query = "SELECT * FROM role WHERE nom = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                role = new Role(
                        rs.getInt("ID_Role"),
                        rs.getString("nom"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public Role addRole2(String nom, String description, int level) {
        String insertQuery = "INSERT INTO role (nom, description,level) VALUES (?, ?,?)";
        String selectQuery = "SELECT * FROM role WHERE nom = ? AND description = ? AND level = ? ORDER BY ID_Role  DESC LIMIT 1"; // Assuming idRole is the primary key
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(insertQuery)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, description);
            pstmt.setInt(3, level);

            pstmt.executeUpdate();
            try (PreparedStatement selectStmt = cnx.prepareStatement(selectQuery)) {
                selectStmt.setString(1, nom);
                selectStmt.setString(2, description);
                selectStmt.setInt(3, level);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int idRole = rs.getInt("ID_Role");
                        String roleName = rs.getString("nom");
                        String roleDescription = rs.getString("description");
                        int levelRole = rs.getInt("level");
                        return new Role(idRole, roleName, roleDescription, levelRole);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRole(int idRole, String nom, String description) {
        String query = "UPDATE role SET nom = ?, description = ? WHERE ID_Role = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, description);
            pstmt.setInt(3, idRole);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Role updateRole1(int idRole, int level) {
        String query = "UPDATE role SET level = ? WHERE ID_Role = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, level);
            pstmt.setInt(2, idRole);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteRole(int idRole) {
        Connection cnx = null;
        try {
            cnx = MyDataBase.getInstance().getCnx();
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            // Begin transaction
            cnx.setAutoCommit(false);

            // Delete from rolehierarchie where ID_RoleP or ID_RoleC
            String deleteRoleHierarchyQuery = "DELETE FROM rolehierarchie WHERE ID_RoleP = ? OR ID_RoleC = ?";
            try (PreparedStatement deleteRoleHierarchyStmt = cnx.prepareStatement(deleteRoleHierarchyQuery)) {
                deleteRoleHierarchyStmt.setInt(1, idRole);
                deleteRoleHierarchyStmt.setInt(2, idRole);
                deleteRoleHierarchyStmt.executeUpdate();
            }

            // Delete user roles associated with the role
            String deleteUserRoleQuery = "DELETE FROM user_role WHERE ID_Role = ?";
            try (PreparedStatement deleteUserRoleStmt = cnx.prepareStatement(deleteUserRoleQuery)) {
                deleteUserRoleStmt.setInt(1, idRole);
                deleteUserRoleStmt.executeUpdate();
            }

            // Delete the role
            String deleteRoleQuery = "DELETE FROM role WHERE ID_Role = ?";
            try (PreparedStatement deleteStmt = cnx.prepareStatement(deleteRoleQuery)) {
                deleteStmt.setInt(1, idRole);
                deleteStmt.executeUpdate();
            }

            // Commit transaction
            cnx.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (cnx != null) {
                    cnx.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (cnx != null) {
                    cnx.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<Role> getParentRoles2(int roleId) {
        Connection cnx = MyDataBase.getInstance().getCnx();
        List<Role> roles = new ArrayList<>();
        String query = "SELECT r.ID_Role, r.nom, r.Level, r.description " +
                "FROM role r " +
                "JOIN rolehierarchie rh ON r.ID_Role = rh.ID_RoleP " +
                "WHERE rh.ID_RoleC = ?";
        try {
            PreparedStatement statement = cnx.prepareStatement(query);
            statement.setInt(1, roleId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Role role = new Role();
                role.setIdRole(resultSet.getInt("ID_Role"));
                role.setNom(resultSet.getString("nom"));
                role.setDescription(resultSet.getString("description"));
                role.setLevel(resultSet.getInt("Level"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public void addRoleHierarchy(int parentRoleId, int childRoleId) {
        String query = "INSERT INTO rolehierarchie (ID_RoleP, ID_RoleC) VALUES (?, ?)";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = cnx.prepareStatement(query)) {

            statement.setInt(1, parentRoleId);
            statement.setInt(2, childRoleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRoleHierarchy(int parentRoleId, int childRoleId) {
        String query = "DELETE FROM rolehierarchie WHERE ID_RoleP = ? AND ID_RoleC = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, parentRoleId);
            statement.setInt(2, childRoleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public List<Role> getRoleParents2(int childRoleId) {
        List<Role> parents = new ArrayList<>();
        String query = "SELECT rp.ID_Role, rp.nom, rp.description FROM rolehierarchie rh " +
                "JOIN role rp ON rh.ID_RoleP = rp.ID_Role WHERE rh.ID_RoleC = ?";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {

            pstmt.setInt(1, childRoleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idRole = rs.getInt("ID_Role");
                    String roleName = rs.getString("nom");
                    String roleDescription = rs.getString("description");
                    parents.add(new Role(idRole, roleName, roleDescription));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parents;
    }
}