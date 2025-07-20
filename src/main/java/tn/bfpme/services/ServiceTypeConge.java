package tn.bfpme.services;

import tn.bfpme.models.*;

import tn.bfpme.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTypeConge {
    private Connection cnx;

    public ServiceTypeConge() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public List<TypeConge> getAllTypeConge() {
        List<TypeConge> typeconges = new ArrayList<>();
        String query = "SELECT `ID_TypeConge`, `Designation`, `Pas`, `File`, `Limite`, `Periode` FROM `typeconge`";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement stmt = cnx.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TypeConge type = new TypeConge(
                        rs.getInt("ID_TypeConge"),
                        rs.getString("Designation"),
                        rs.getDouble("Pas"),
                        rs.getBoolean("File"),
                        rs.getDouble("Limite"),
                        rs.getString("Periode")
                );
                typeconges.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeconges;
    }


    public void AddTypeConge(String designation, double pas, boolean file, double limite, String periode) {
        String query = "INSERT INTO typeconge (Designation, Pas, File,Limite, Periode) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = MyDataBase.getInstance().getCnx();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, designation);
            preparedStatement.setDouble(2, pas);
            preparedStatement.setBoolean(3, file);
            preparedStatement.setDouble(4, limite);
            preparedStatement.setString(5, periode);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getPasBySoldeId(int idSolde) {
        String query = "SELECT Pas FROM typeconge WHERE ID_TypeConge = ?";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSolde);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Pas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoldeCongeIdByDesignation(String designation) {
        String query = "SELECT ID_TypeConge FROM typeconge WHERE Designation = ?";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, designation);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_TypeConge");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateTypeConge(int idSolde, String designation, double pas, boolean file, double limite, String periode) {
        String query = "UPDATE typeconge SET Designation = ?, Pas = ?, File = ?, Limite = ?, Periode = ? WHERE ID_TypeConge = ?";

        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, designation);
            stmt.setDouble(2, pas);
            stmt.setBoolean(3, file);
            stmt.setDouble(4, limite);
            stmt.setString(5, periode);
            stmt.setInt(6, idSolde);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No rows were updated. Verify that the ID_TypeConge exists.");
            } else {
                System.out.println("Update successful.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTypeConge(int idSolde) {
        String query = "DELETE FROM typeconge WHERE ID_TypeConge = ?";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSolde);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsByDesignation(String designation) {
        String query = "SELECT COUNT(*) FROM typeconge WHERE Designation = ?";
        try (Connection connection = MyDataBase.getInstance().getCnx();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, designation);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
