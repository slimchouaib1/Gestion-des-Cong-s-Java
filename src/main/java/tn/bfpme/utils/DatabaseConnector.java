package tn.bfpme.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnector {

    public static int getLeaveCredits(String userId) {
        String query = "SELECT TotalSolde FROM user_solde WHERE ID_User = '" + userId + "'";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("TotalSolde");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean canTakeLeave(String userId, String date, String role, String department) {
        String query = "SELECT COUNT(*) FROM leave_requests WHERE user_id = '" + userId + "' AND date = '" + date + "'";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // No leave requests on that date
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUserRole(String userId) {
        String query = "SELECT role FROM users WHERE id = '" + userId + "'";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserDepartment(String userId) {
        String query = "SELECT department FROM users WHERE id = '" + userId + "'";
        try (Connection conn = MyDataBase.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getString("department");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
