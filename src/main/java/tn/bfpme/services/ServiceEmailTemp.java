package tn.bfpme.services;

import tn.bfpme.models.EmailsTemplates;
import tn.bfpme.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEmailTemp {
    private Connection cnx;

    public ServiceEmailTemp() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public List<EmailsTemplates> getAllEmailsTemplates() {
        List<EmailsTemplates> emailstemps = new ArrayList<>();
        String query = "SELECT * FROM `email_templates`";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                EmailsTemplates EmailTemp = new EmailsTemplates(
                        rs.getInt("id_Email"),
                        rs.getString("object"),
                        rs.getString("message")
                );
                emailstemps.add(EmailTemp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emailstemps;
    }

    public void AddEmailTemp(String obj, String msg) {
        String query = "INSERT INTO `email_templates`(`object`, `message`) VALUES (?, ?)";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, obj);
            pstmt.setString(2, msg);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateEmailTemp(int id, String object, String message) {
        String query = "UPDATE `email_templates` SET `object`=? ,`message`=? WHERE  `id_Email`=?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, object);
            pstmt.setString(2, message);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteEmailTelp(int id) {
        String query = "DELETE FROM `email_templates` WHERE `id_Email`= ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
