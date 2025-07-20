package tn.bfpme.services;

import tn.bfpme.interfaces.INotification;
import tn.bfpme.models.Notification;
import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceNotification implements INotification {
    private Connection cnx;

    public ServiceNotification() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public List<Notification> AfficherNotifUser() {
        List<Notification> notifs = new ArrayList<>();
        String query = "SELECT * FROM `notification` WHERE `ID_User` = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, SessionManager.getInstance().getUser().getIdUser());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notif = new Notification(
                        rs.getInt("ID_User"),
                        rs.getInt("ID_Notif"),
                        rs.getString("NotfiMessage"),
                        rs.getInt("Statut"),
                        rs.getString("NotifContent")
                );
                notifs.add(notif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifs;
    }

    public void NewNotification(int idUser, String NotifMsg, int Statut, String NotifContent) {
        String query = "INSERT INTO `notification`(`ID_User`, `NotfiMessage`, `Statut`, `NotifContent`) VALUES (?,?,?,?)";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, idUser);
            pstmt.setString(2, NotifMsg);
            pstmt.setInt(3, Statut);
            pstmt.setString(4, NotifContent);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteAllUserNotif() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            String qry = "DELETE FROM `notification` WHERE `ID_User`= ?";
            PreparedStatement stm = cnx.prepareStatement(qry);
            stm.setInt(1, SessionManager.getInstance().getUser().getIdUser());
            stm.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void DeleteNotif(int id) {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            String qry = "DELETE FROM `notification` WHERE `ID_Notif`= ?";
            PreparedStatement stm = cnx.prepareStatement(qry);
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
