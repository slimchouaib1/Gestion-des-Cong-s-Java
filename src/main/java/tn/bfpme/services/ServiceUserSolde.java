package tn.bfpme.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.bfpme.utils.MyDataBase;
import tn.bfpme.models.*;

public class ServiceUserSolde {
    private static Connection cnx;

    public ServiceUserSolde() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public void updateUserSolde(UserSolde userSolde) {
        String query = "UPDATE user_solde SET TotalSolde = ? WHERE ID_UserSolde = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setDouble(1, userSolde.getTotalSolde());
            stm.setInt(2, userSolde.getUD_UserSolde());
            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserSolde(int userId, int idtype, long days) {
        UserSolde userSolde = getUserSoldeByUserIdAndTypeCongeId(userId, idtype);
        if (userSolde != null) {
            double newSolde = userSolde.getTotalSolde() - days;
            userSolde.setTotalSolde(newSolde);
            updateUserSolde(userSolde);
        }
    }

    public void addUserSolde(int userId, int typeCongeId, double totalSolde) {
        Connection cnx = MyDataBase.getInstance().getCnx();
        String query = "INSERT INTO user_solde(ID_User, ID_TypeConge, TotalSolde) VALUES (?,?,?)";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, typeCongeId);
            pstmt.setDouble(3, totalSolde);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<UserSolde> getUserSoldes(int userId) {
        List<UserSolde> userSoldes = new ArrayList<>();
        String sql = "SELECT ID_UserSolde, ID_User, ID_TypeConge, TotalSolde FROM user_solde WHERE ID_User = ?";
        Connection cnx = MyDataBase.getInstance().getCnx();
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement stm = cnx.prepareStatement(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UserSolde userSolde = new UserSolde();
                userSolde.setUD_UserSolde(rs.getInt("ID_UserSolde"));
                userSolde.setID_User(rs.getInt("ID_User"));
                userSolde.setID_TypeConge(rs.getInt("ID_TypeConge"));
                userSolde.setTotalSolde(rs.getDouble("TotalSolde"));
                userSoldes.add(userSolde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSoldes;
    }

    public UserSolde getUserSoldeByUserIdAndTypeCongeId(int userId, int typeCongeId) {
        UserSolde userSolde = null;
        String query = "SELECT * FROM user_solde WHERE ID_User = ? AND ID_TypeConge = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, typeCongeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userSolde = new UserSolde(
                        rs.getInt("ID_UserSolde"),
                        rs.getInt("ID_User"),
                        rs.getInt("ID_TypeConge"),
                        rs.getDouble("TotalSolde")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSolde;
    }

    public boolean requestLeave(int userId, int typeCongeId, double daysRequested) {
        UserSolde userSolde = getUserSoldeByUserIdAndTypeCongeId(userId, typeCongeId);
        if (userSolde != null) {
            double currentSolde = userSolde.getTotalSolde();
            double newSolde = currentSolde - daysRequested;
            if (newSolde >= 0) {
                userSolde.setTotalSolde(newSolde);
                updateUserSolde(userSolde);
                System.out.println("Leave requested: " + daysRequested + " days. New solde: " + newSolde);
                return true; // Leave request can be processed
            }
        }
        System.out.println("Leave request failed: Not enough solde.");
        return false; // Not enough solde to process the leave request
    }

    public void refuseLeave(int userId, int typeCongeId, double daysRequested) {
        UserSolde userSolde = getUserSoldeByUserIdAndTypeCongeId(userId, typeCongeId);
        if (userSolde != null) {
            double currentSolde = userSolde.getTotalSolde();
            double newSolde = currentSolde + daysRequested;
            userSolde.setTotalSolde(newSolde);
            updateUserSolde(userSolde);
            System.out.println("Leave request refused: " + daysRequested + " days. Solde re-incremented to: " + newSolde);
        }
    }

    /*public void incrementMonthlyLeaveBalances() {
        List<UserSolde> allUserSoldes = getAllUserSoldes();
        Map<Integer, Double> typeCongeLimits = getTypeCongeLimit();
        Map<Integer, Double> typeCongePas = getTypeCongePas();

        for (UserSolde userSolde : allUserSoldes) {
            int typeCongeId = userSolde.getID_TypeConge();
            double currentSolde = userSolde.getTotalSolde();

            double pas = typeCongePas.getOrDefault(typeCongeId, 0.0);
            double limit = typeCongeLimits.getOrDefault(typeCongeId, Double.MAX_VALUE);
            double newSolde = currentSolde + pas;

            if (newSolde > limit) {
                newSolde = limit;
            }

            userSolde.setTotalSolde(newSolde);
            updateUserSolde(userSolde); // Ensure this method accepts UserSolde object
        }
    }*/

    public Map<Integer, Double> getTypeCongeLimit() {
        Map<Integer, Double> typeCongeLimits = new HashMap<>();
        String query = "SELECT ID_TypeConge, `Limite` FROM typeconge";  // Use backticks for reserved keywords
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                typeCongeLimits.put(rs.getInt("ID_TypeConge"), rs.getDouble("Limite"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeCongeLimits;
    }

    public Map<Integer, Double> getTypeCongePas() {
        Map<Integer, Double> pasMap = new HashMap<>();
        String query = "SELECT ID_TypeConge, Pas FROM typeconge";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                pasMap.put(rs.getInt("ID_TypeConge"), rs.getDouble("Pas"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pasMap;
    }

    public Map<Integer, String> getTypeCongePeriods() {
        Map<Integer, String> typeCongePeriods = new HashMap<>();
        String query = "SELECT ID_TypeConge, Periode FROM typeconge";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int typeCongeId = rs.getInt("ID_TypeConge");
                String periode = rs.getString("Periode");
                typeCongePeriods.put(typeCongeId, periode);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return typeCongePeriods;
    }

    public List<UserSolde> getAllUserSoldes() {
        List<UserSolde> soldeList = new ArrayList<>();
        String query = "SELECT ID_UserSolde, ID_User, ID_TypeConge, TotalSolde FROM user_solde";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stm = cnx.prepareStatement(query);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                UserSolde userSolde = new UserSolde(
                        rs.getInt("ID_UserSolde"),
                        rs.getInt("ID_User"),
                        rs.getInt("ID_TypeConge"),
                        rs.getDouble("TotalSolde")
                        //""  // Designation is not present in the table structure
                );
                soldeList.add(userSolde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldeList;
    }

    public TypeConge getTypeCongeById(int typeCongeId) {
        TypeConge typeConge = null;
        String query = "SELECT `ID_TypeConge`, `Designation`, `Pas`, `File`, `Limite`, `Periode` FROM `typeconge` WHERE `ID_TypeConge` = ?";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setInt(1, typeCongeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idTypeConge = resultSet.getInt("ID_TypeConge");
                String designation = resultSet.getString("Designation");
                double pas = resultSet.getDouble("Pas");
                boolean file = resultSet.getBoolean("File");
                double limite = resultSet.getDouble("Limite");
                String periode = resultSet.getString("Periode");

                typeConge = new TypeConge(idTypeConge, designation, pas, file, limite, periode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeConge;
    }
}
