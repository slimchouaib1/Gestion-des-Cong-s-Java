package tn.bfpme.services;

import tn.bfpme.interfaces.IUtilisateur;

import tn.bfpme.models.*;

import tn.bfpme.utils.MyDataBase;
import tn.bfpme.utils.SessionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServiceUtilisateur implements IUtilisateur {

    private Connection cnx;

    public ServiceUtilisateur() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public List<User> show() throws SQLException {
        List<User> users = new ArrayList<>();
        String qry = "SELECT * FROM user";
        try (PreparedStatement pstm = cnx.prepareStatement(qry); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setRoleNom(rs.getString("Role"));
                user.setDepartementNom(rs.getString("Departement"));
                user.setManagerName(rs.getString("Manager")); // Fetch and set the contrat_id
                // user.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public UserConge AfficherEnAttente() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            String query = "WITH RECURSIVE Subordinates AS ("
                    + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                    + "FROM `user` "
                    + "WHERE ID_User = ? "
                    + "UNION ALL "
                    + "SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                    + "FROM `user` u "
                    + "INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                    + "SELECT user.ID_User, user.ID_Manager, user.Nom, user.Prenom, user.Email, user.Image, user.ID_Departement, "
                    + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file, "
                    + "typeconge.Designation, typeconge.Pas, typeconge.Periode, typeconge.File AS tcF FROM `user` "
                    + "JOIN conge ON user.ID_User = conge.ID_User "
                    + "JOIN typeconge ON conge.TypeConge = typeconge.ID_TypeConge "
                    + "WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ?";
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conge.setDesignation(rs.getString("Designation"));
                TypeConge typeConge = new TypeConge();
                typeConge.setIdTypeConge(rs.getInt("TypeConge"));
                typeConge.setDesignation(rs.getString("Designation"));
                typeConge.setPas(rs.getDouble("Pas"));
                typeConge.setPeriode(rs.getString("Periode"));
                typeConge.setFile(rs.getBoolean("tcF"));
                conge.setTypeConge2(typeConge); // Set the TypeConge object
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge TriType() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM user "
                + "WHERE ID_User = ? "
                + "UNION ALL "
                + "SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User "
                + "WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ? ORDER BY conge.TypeConge";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge TriNom() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement FROM user "
                + "WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User "
                + "WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ? ORDER BY user.Nom ";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    public UserConge RechercheUserCongeEnAtt(String searchCriteria) {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM user WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) "
                + "AND conge.Statut = ? ";

        // Append search criteria to the query
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            query += "AND (user.Nom LIKE ? OR user.Prenom LIKE ? OR user.Email LIKE ? " + "OR conge.TypeConge LIKE ? OR conge.DateDebut = ? OR conge.DateFin = ?) ";
        }

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));

            // Set search criteria parameters if provided
            if (searchCriteria != null && !searchCriteria.isEmpty()) {
                String likeCriteria = "%" + searchCriteria + "%";
                ps.setString(4, likeCriteria);
                ps.setString(5, likeCriteria);
                ps.setString(6, likeCriteria);
                ps.setString(7, likeCriteria);
                ps.setString(8, searchCriteria); // Assuming searchCriteria is in 'yyyy-MM-dd' format for date comparison
                ps.setString(9, searchCriteria); // Assuming searchCriteria is in 'yyyy-MM-dd' format for date comparison
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge TriPrenom() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM user WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ? ORDER BY user.Prenom";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    public UserConge TriDateDebut() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM user WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User "
                + "WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ? ORDER BY conge.DateDebut";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge TriDateFin() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM user WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM user u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file "
                + "FROM user JOIN conge ON user.ID_User = conge.ID_User WHERE user.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ? ORDER BY conge.DateFin";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.En_Attente));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                if (!users.contains(user)) {
                    users.add(user);
                }
                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                //conge.setTypeConge(TypeConge.valueOf(rs.getString("TypeConge")));
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge AfficherApprove() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM `user` WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom,u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM `user` u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file, "
                + "typeconge.Designation, typeconge.Pas, typeconge.Periode, typeconge.File AS tcF "
                + "FROM `user` JOIN conge ON `user`.ID_User = conge.ID_User JOIN typeconge ON conge.TypeConge = typeconge.ID_TypeConge "
                + "WHERE `user`.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ?";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.Approuvé));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                // Add idSolde field handling if necessary
                if (!users.contains(user)) {
                    users.add(user);
                }

                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conge.setDesignation(rs.getString("Designation"));
                TypeConge typeConge = new TypeConge();
                typeConge.setIdTypeConge(rs.getInt("TypeConge"));
                typeConge.setDesignation(rs.getString("Designation"));
                typeConge.setPas(rs.getDouble("Pas"));
                typeConge.setPeriode(rs.getString("Periode"));
                typeConge.setFile(rs.getBoolean("tcF"));
                conge.setTypeConge2(typeConge); // Set the TypeConge object
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    @Override
    public UserConge AfficherReject() {
        List<User> users = new ArrayList<>();
        List<Conge> conges = new ArrayList<>();
        String query = "WITH RECURSIVE Subordinates AS ("
                + "SELECT ID_User, Nom, Prenom, ID_Manager, Email, Image, ID_Departement "
                + "FROM `user` WHERE ID_User = ? UNION ALL SELECT u.ID_User, u.Nom, u.Prenom, u.ID_Manager, u.Email, u.Image, u.ID_Departement "
                + "FROM `user` u INNER JOIN Subordinates s ON u.ID_Manager = s.ID_User ) "
                + "SELECT user.ID_User, user.Nom, user.Prenom, user.ID_Manager, user.Email, user.Image, user.ID_Departement, "
                + "conge.ID_Conge, conge.TypeConge, conge.Statut, conge.DateFin, conge.DateDebut, conge.description, conge.file, "
                + "typeconge.Designation, typeconge.Pas, typeconge.Periode, typeconge.File AS tcF FROM `user` JOIN conge ON `user`.ID_User = conge.ID_User "
                + "JOIN typeconge ON conge.TypeConge = typeconge.ID_TypeConge WHERE `user`.ID_User IN (SELECT ID_User FROM Subordinates WHERE ID_User != ?) AND conge.Statut = ?";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            int currentUserId = SessionManager.getInstance().getUser().getIdUser();
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            ps.setString(3, String.valueOf(Statut.Rejeté));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                // Add idSolde field handling if necessary
                if (!users.contains(user)) {
                    users.add(user);
                }

                Conge conge = new Conge();
                conge.setIdConge(rs.getInt("ID_Conge"));
                conge.setDateDebut(rs.getDate("DateDebut").toLocalDate());
                conge.setDateFin(rs.getDate("DateFin").toLocalDate());
                conge.setStatut(Statut.valueOf(rs.getString("Statut")));
                conge.setDescription(rs.getString("description"));
                conge.setFile(rs.getString("file"));
                conge.setIdUser(rs.getInt("ID_User"));
                conge.setDesignation(rs.getString("Designation"));
                TypeConge typeConge = new TypeConge();
                typeConge.setIdTypeConge(rs.getInt("TypeConge"));
                typeConge.setDesignation(rs.getString("Designation"));
                typeConge.setPas(rs.getDouble("Pas"));
                typeConge.setPeriode(rs.getString("Periode"));
                typeConge.setFile(rs.getBoolean("tcF"));
                conge.setTypeConge2(typeConge); // Set the TypeConge object
                conges.add(conge);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new UserConge(users, conges);
    }

    private List<TypeConge> getTypeCongesByUserId(int userId) {
        List<TypeConge> typeConges = new ArrayList<>();
        String query = "SELECT tc.* FROM typeconge tc " + "JOIN user_typeconge utc ON tc.ID_TypeConge = utc.ID_TypeConge " + "WHERE utc.ID_User = ?";

        try (Connection cnx = MyDataBase.getInstance().getCnx(); PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TypeConge typeConge = new TypeConge();
                //typeConge.setIdTypeConge(rs.getInt("ID_TypeConge"), totalSolde);
                typeConge.setDesignation(rs.getString("Type"));
                typeConge.setPas(rs.getDouble("Pas"));
                typeConge.setPeriode(rs.getString("Periode"));
                typeConge.setFile(rs.getBoolean("File"));
                typeConges.add(typeConge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return typeConges;
    }

    public int getManagerIdByUserId2(int userId) {
        String query = "SELECT ID_Manager FROM user WHERE ID_User = ?";
        int managerId = 0;
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                managerId = rs.getInt("ID_Manager");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managerId;
    }

    public int getManagerIdByUserId(int userId) {
        int managerId = 0;
        String query = "SELECT ID_Manager FROM user WHERE ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                managerId = rs.getInt("ID_Manager");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managerId;
    }

    public List<User> getUsersByDepartment(String departement) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user.ID_User, user.Nom, user.Prenom, user.Email, user.Image, user.ID_Departement " + "FROM user " + "WHERE user.ID_Departement = ?";

        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, departement);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));

                // Retrieve the TypeConge associated with the user
                List<TypeConge> typeConges = getTypeCongesByUserId(user.getIdUser());
                for (TypeConge typeConge : typeConges) {
                    user.addTypeConge(typeConge);
                }

                users.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return users;
    }

    @Override
    public void Add(User user) {
        String query = "INSERT INTO user (`Nom`, `Prenom`, `Email`, `MDP`, `Image`, `ID_Departement`, `ID_Manager`, `Creation_Date`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getMdp());
            pst.setString(5, user.getImage());
            pst.setInt(6, user.getIdDepartement());
            pst.setInt(7, user.getIdManager());
            pst.setDate(8, Date.valueOf(user.getCreationDate()));
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void AddUser_RH(User user) {
        String query = "INSERT INTO user (`ID_User`,`Nom`, `Prenom`, `Email`, `MDP`, `Image`,`Creation_Date`) VALUES (?,?, ?, ?, ?, ?, ?)";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setInt(1, user.getIdUser()); // Set the ID_User explicitly
            pst.setString(2, user.getNom());
            pst.setString(3, user.getPrenom());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getMdp());
            pst.setString(6, user.getImage());
            pst.setDate(7, Date.valueOf(user.getCreationDate()));
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void Update(User user) {
        String query = "UPDATE user SET Nom=?, Prenom=?, Email=?, MDP=?, Image=? WHERE ID_User=?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getMdp());
            pst.setString(5, user.getImage());
            pst.setInt(6, user.getIdUser());
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void DeleteByID(int id) {
        String query = "DELETE FROM user WHERE ID_User=?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<User> search(String query) {
        return null;
    }

    @Override
    public List<User> ShowUnder() {
        List<User> users = new ArrayList<>();
        int currentUserId = SessionManager.getInstance().getUser().getIdUser(); // Assuming SessionManager manages the current user's session
        String sql = "WITH RECURSIVE Subordinates AS (" + "SELECT u.ID_User, u.Nom, u.Prenom, u.Email, u.MDP, u.Image, u.ID_Departement, u.ID_Manager, ur.ID_Role " + "FROM user u " + "LEFT JOIN user_role ur ON u.ID_User = ur.ID_User " + "WHERE u.ID_Manager = ? " + "UNION ALL " + "SELECT u.ID_User, u.Nom, u.Prenom, u.Email, u.MDP, u.Image, u.ID_Departement, u.ID_Manager, ur.ID_Role " + "FROM user u " + "INNER JOIN Subordinates s ON s.ID_User = u.ID_Manager " + "LEFT JOIN user_role ur ON u.ID_User = ur.ID_User" + ") " + "SELECT s.ID_User, s.Nom, s.Prenom, s.Email, s.MDP, s.Image, s.ID_Departement, s.ID_Manager, s.ID_Role, d.nom AS DepartementNom, r.nom AS RoleNom " + "FROM Subordinates s " + "LEFT JOIN departement d ON s.ID_Departement = d.ID_Departement " + "LEFT JOIN role r ON s.ID_Role = r.ID_Role " + "WHERE s.ID_User != ?";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId); // Exclude current user
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setMdp(rs.getString("MDP"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdRole(rs.getInt("ID_Role"));
                user.setDepartementNom(rs.getString("DepartementNom"));
                user.setRoleNom(rs.getString("RoleNom"));
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return users;
    }

    public int getLastInsertedUserId() throws SQLException {
        String query = "SELECT LAST_INSERT_ID()";
        try (Connection cnx = MyDataBase.getInstance().getCnx(); Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve last inserted user ID");
    }

    public void setManagerForUser(int userId, int managerId) {
        String query = "UPDATE user SET ID_Manager = ? WHERE ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, managerId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> RechercheUnder(String input) {
        List<User> users = new ArrayList<>();
        int currentUserId = SessionManager.getInstance().getUser().getIdUser(); // Assuming SessionManager manages the current user's session
        String sql = "WITH RECURSIVE Subordinates AS (" + "SELECT u.ID_User, u.Nom, u.Prenom, u.Email, u.MDP, u.Image, u.ID_Departement, u.ID_Manager, ur.ID_Role " + "FROM user u " + "LEFT JOIN user_role ur ON u.ID_User = ur.ID_User " + "WHERE u.ID_Manager = ? " + "UNION ALL " + "SELECT u.ID_User, u.Nom, u.Prenom, u.Email, u.MDP, u.Image, u.ID_Departement, u.ID_Manager, ur.ID_Role " + "FROM user u " + "INNER JOIN Subordinates s ON s.ID_User = u.ID_Manager " + "LEFT JOIN user_role ur ON u.ID_User = ur.ID_User" + ") " + "SELECT s.ID_User, s.Nom, s.Prenom, s.Email, s.MDP, s.Image, s.ID_Departement, s.ID_Manager, s.ID_Role, d.nom AS DepartementNom, r.nom AS RoleNom " + "FROM Subordinates s " + "LEFT JOIN departement d ON s.ID_Departement = d.ID_Departement " + "LEFT JOIN role r ON s.ID_Role = r.ID_Role " + "WHERE s.ID_User != ? " + "AND (s.Nom LIKE ? OR s.Prenom LIKE ? OR s.Email LIKE ?)";

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId); // Exclude current user
            String searchInput = "%" + input + "%";
            ps.setString(3, searchInput);
            ps.setString(4, searchInput);
            ps.setString(5, searchInput);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("ID_User"));
                user.setNom(rs.getString("Nom"));
                user.setPrenom(rs.getString("Prenom"));
                user.setEmail(rs.getString("Email"));
                user.setMdp(rs.getString("MDP"));
                user.setImage(rs.getString("Image"));
                user.setIdDepartement(rs.getInt("ID_Departement"));
                user.setIdManager(rs.getInt("ID_Manager"));
                user.setIdRole(rs.getInt("ID_Role"));
                user.setDepartementNom(rs.getString("DepartementNom"));
                user.setRoleNom(rs.getString("RoleNom"));
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return users;
    }

    public String getRoleNameByUserID(int userId) {
        String query = "SELECT r.nom AS RoleName " + "FROM user u " + "JOIN user_role ur ON u.ID_User = ur.ID_User " + "JOIN role r ON ur.ID_Role = r.ID_Role " + "WHERE u.ID_User = ?";
        String RoleName = "";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RoleName = rs.getString("RoleName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return RoleName;
    }

    public String getDepNameByUserID(int userId) {
        String query = "SELECT d.nom AS DepartmentName " + "FROM user u " + "JOIN departement d ON u.ID_Departement = d.ID_Departement " + "WHERE u.ID_User = ?";
        String DepName = "";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DepName = rs.getString("DepartmentName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DepName;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUsersASC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY Nom ASC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUsersDESC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY Nom DESC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUserDepASC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, d.nom AS department_name " + "FROM user u " + "JOIN departement d ON u.ID_Departement = d.ID_Departement " + "ORDER BY d.nom ASC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUserDepDESC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, d.nom AS department_name " + "FROM user u " + "JOIN departement d ON u.ID_Departement = d.ID_Departement " + "ORDER BY d.nom DESC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUserRolesASC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.nom AS role_name " + "FROM user u " + "JOIN user_role ur ON u.ID_User = ur.ID_User " + "JOIN role r ON ur.ID_Role = r.ID_Role " + "ORDER BY r.nom ASC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> TriUserRolesDESC() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.nom AS role_name " + "FROM user u " + "JOIN user_role ur ON u.ID_User = ur.ID_User " + "JOIN role r ON ur.ID_Role = r.ID_Role " + "ORDER BY r.nom DESC";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public Departement getDepartmentByUserId(int userId) {
        String sql = "SELECT d.* FROM departement d JOIN user u ON d.ID_Departement = u.ID_Departement WHERE u.ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Departement(rs.getInt("ID_Departement"), rs.getString("nom"), rs.getString("description"), rs.getInt("Parent_Dept"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Role getRoleByUserId(int userId) {
        String sql = "SELECT r.* FROM role r JOIN user_role ur ON r.ID_Role = ur.ID_Role WHERE ur.ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Role(rs.getInt("ID_Role"), rs.getString("nom"), rs.getString("description"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void ensureConnection() throws SQLException {
        if (cnx == null || cnx.isClosed()) {
            cnx = MyDataBase.getInstance().getCnx();
        }
    }

    public User getUserById(int userId) throws SQLException {
        ensureConnection();
        String query = "SELECT u.*, d.nom AS departementNom, r.nom AS roleNom " + "FROM user u " + "LEFT JOIN departement d ON u.ID_Departement = d.ID_Departement " + "LEFT JOIN user_role ur ON u.ID_User = ur.ID_User " + "LEFT JOIN role r ON ur.ID_Role = r.ID_Role " + "WHERE u.ID_User = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = extractUserFromResultSet(resultSet);
                user.setDepartementNom(resultSet.getString("departementNom"));
                user.setRoleNom(resultSet.getString("roleNom"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user by ID: " + e.getMessage(), e);
        }
        return null;
    }


    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setIdUser(resultSet.getInt("ID_User"));
        user.setNom(resultSet.getString("nom"));
        user.setPrenom(resultSet.getString("prenom"));
        user.setEmail(resultSet.getString("email"));
        user.setMdp(resultSet.getString("MDP"));
        user.setImage(resultSet.getString("image"));
        user.setCreationDate(resultSet.getDate("Creation_Date") != null ? resultSet.getDate("Creation_Date").toLocalDate() : null);
        user.setIdManager(resultSet.getInt("ID_Manager"));
        user.setIdDepartement(resultSet.getInt("ID_Departement"));
        user.setID_UserSolde(resultSet.getInt("idSolde"));
        user.setDepartementNom(resultSet.getString("departementNom"));
        user.setRoleNom(resultSet.getString("roleNom"));
        return user;
    }

    public List<User> getAllManagers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM user u " + "INNER JOIN user u2 ON u.ID_User = u2.ID_Manager";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"), rs.getInt("ID_Interim"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public boolean assignInterimManager(int managerId, int interimManagerId) {
        String sql = "UPDATE user SET ID_Interim = ? WHERE ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, interimManagerId);
            ps.setInt(2, managerId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateInterimManager(int managerId, int newInterimManagerId) {
        String sql = "UPDATE user SET ID_Interim = ? WHERE ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, newInterimManagerId);
            ps.setInt(2, managerId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteInterimManager(int managerId) {
        String sql = "UPDATE user SET ID_Interim = NULL WHERE ID_User = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, managerId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public User getInterimByUserId(int userId) {
        String query = "SELECT u2.* FROM user u1 " + "JOIN user u2 ON u1.ID_Interim = u2.ID_User " + "WHERE u1.ID_User = ?";
        User interimUser = null;
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                interimUser = new User(rs.getInt("ID_User"), rs.getString("Nom"), rs.getString("Prenom"), rs.getString("Email"), rs.getString("MDP"), rs.getString("Image"), rs.getDate("Creation_Date") != null ? rs.getDate("Creation_Date").toLocalDate() : null, rs.getInt("ID_Departement"), rs.getInt("ID_Manager"), rs.getInt("idSolde"), rs.getInt("ID_Interim"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interimUser;
    }

    public User getInterimOfUsersManager(int userId) {
        String managerQuery = "SELECT ID_Manager FROM user WHERE ID_User = ?";
        String interimQuery = "SELECT u2.* FROM user u1 JOIN user u2 ON u1.ID_Interim = u2.ID_User WHERE u1.ID_User = ?";
        User interimUser = null;

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            // Step 1: Get the manager ID of the given user
            PreparedStatement psManager = cnx.prepareStatement(managerQuery);
            psManager.setInt(1, userId);
            ResultSet rsManager = psManager.executeQuery();

            if (rsManager.next()) {
                int managerId = rsManager.getInt("ID_Manager");

                // Step 2: Get the interim details of the manager
                PreparedStatement psInterim = cnx.prepareStatement(interimQuery);
                psInterim.setInt(1, managerId);
                ResultSet rsInterim = psInterim.executeQuery();

                if (rsInterim.next()) {
                    interimUser = new User(rsInterim.getInt("ID_User"), rsInterim.getString("Nom"), rsInterim.getString("Prenom"), rsInterim.getString("Email"), rsInterim.getString("MDP"), rsInterim.getString("Image"), rsInterim.getDate("Creation_Date") != null ? rsInterim.getDate("Creation_Date").toLocalDate() : null, rsInterim.getInt("ID_Departement"), rsInterim.getInt("ID_Manager"), rsInterim.getInt("idSolde"), rsInterim.getInt("ID_Interim"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return interimUser;
    }

    public boolean isUsersManagerOnLeave(int userId) {
        String managerQuery = "SELECT ID_Manager FROM user WHERE ID_User = ?";
        String congeQuery = "SELECT COUNT(*) FROM conge WHERE ID_User = ? AND ? BETWEEN DateDebut AND DateFin AND Statut = 'Approuvé'";
        boolean isOnLeave = false;

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            // Step 1: Get the manager ID of the given user
            PreparedStatement psManager = cnx.prepareStatement(managerQuery);
            psManager.setInt(1, userId);
            ResultSet rsManager = psManager.executeQuery();

            if (rsManager.next()) {
                int managerId = rsManager.getInt("ID_Manager");

                // Step 2: Check if the manager is currently on leave
                PreparedStatement psConge = cnx.prepareStatement(congeQuery);
                psConge.setInt(1, managerId);
                psConge.setDate(2, Date.valueOf(LocalDate.now())); // Set the current date
                ResultSet rsConge = psConge.executeQuery();

                if (rsConge.next()) {
                    int leaveCount = rsConge.getInt(1);
                    isOnLeave = leaveCount > 0; // If leave count is greater than 0, the manager is on leave
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isOnLeave;
    }

    public List<User> getSubordinatesIfManagerOnLeave(int interimUserId) {
        String interimQuery = "SELECT ID_User FROM user WHERE ID_Interim = ?";
        String congeQuery = "SELECT COUNT(*) FROM conge WHERE ID_User = ? AND ? BETWEEN DateDebut AND DateFin AND Statut = 'Approuvé'";
        String subordinatesQuery = "SELECT * FROM user WHERE ID_Manager = ?";
        List<User> subordinates = new ArrayList<>();

        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }

            // Step 1: Check if the user is an interim for any manager
            PreparedStatement psInterim = cnx.prepareStatement(interimQuery);
            psInterim.setInt(1, interimUserId);
            ResultSet rsInterim = psInterim.executeQuery();

            while (rsInterim.next()) {
                int managerId = rsInterim.getInt("ID_User");

                // Step 2: Check if the manager is currently on leave
                PreparedStatement psConge = cnx.prepareStatement(congeQuery);
                psConge.setInt(1, managerId);
                psConge.setDate(2, Date.valueOf(LocalDate.now())); // Set the current date
                ResultSet rsConge = psConge.executeQuery();

                if (rsConge.next() && rsConge.getInt(1) > 0) {
                    // Step 3: Retrieve the subordinates of the manager
                    PreparedStatement psSubordinates = cnx.prepareStatement(subordinatesQuery);
                    psSubordinates.setInt(1, managerId);
                    ResultSet rsSubordinates = psSubordinates.executeQuery();

                    while (rsSubordinates.next()) {
                        User subordinate = new User(rsSubordinates.getInt("ID_User"), rsSubordinates.getString("Nom"), rsSubordinates.getString("Prenom"), rsSubordinates.getString("Email"), rsSubordinates.getString("MDP"), rsSubordinates.getString("Image"), rsSubordinates.getDate("Creation_Date") != null ? rsSubordinates.getDate("Creation_Date").toLocalDate() : null, rsSubordinates.getInt("ID_Departement"), rsSubordinates.getInt("ID_Manager"), rsSubordinates.getInt("idSolde"), rsSubordinates.getInt("ID_Interim"));
                        subordinates.add(subordinate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subordinates;
    }

    public List<User> getAppropriateUsers(int id_manager) {
        List<User> users = new ArrayList<>();
        String managerQuery = "SELECT u.* FROM user u JOIN user u2 ON u.ID_User = u2.ID_Manager WHERE u2.ID_User = ?";
        String subordinatesQuery = "SELECT * FROM user WHERE ID_Manager = ?";
        String sameRoleQuery = "SELECT u.* FROM user u JOIN user_role ur ON u.ID_User = ur.ID_User WHERE ur.ID_Role = (SELECT ur2.ID_Role FROM user_role ur2 WHERE ur2.ID_User = ?) AND u.ID_User != ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement psManager = cnx.prepareStatement(managerQuery);
            psManager.setInt(1, id_manager);
            ResultSet rsManager = psManager.executeQuery();
            if (rsManager.next()) {
                int managerId = rsManager.getInt("ID_User");
                if (!isUser_OFFDUTY(managerId)) {
                    User manager = new User(managerId, rsManager.getString("Nom"), rsManager.getString("Prenom"), rsManager.getString("Email"), rsManager.getString("MDP"), rsManager.getString("Image"), rsManager.getDate("Creation_Date") != null ? rsManager.getDate("Creation_Date").toLocalDate() : null, rsManager.getInt("ID_Departement"), rsManager.getInt("ID_Manager"), rsManager.getInt("idSolde"), rsManager.getInt("ID_Interim"));
                    users.add(manager);
                }
            }
            PreparedStatement psSubordinates = cnx.prepareStatement(subordinatesQuery);
            psSubordinates.setInt(1, id_manager);
            ResultSet rsSubordinates = psSubordinates.executeQuery();
            while (rsSubordinates.next()) {
                int subordinateId = rsSubordinates.getInt("ID_User");
                if (!isUser_OFFDUTY(subordinateId)) {
                    User subordinate = new User(subordinateId, rsSubordinates.getString("Nom"), rsSubordinates.getString("Prenom"), rsSubordinates.getString("Email"), rsSubordinates.getString("MDP"), rsSubordinates.getString("Image"), rsSubordinates.getDate("Creation_Date") != null ? rsSubordinates.getDate("Creation_Date").toLocalDate() : null, rsSubordinates.getInt("ID_Departement"), rsSubordinates.getInt("ID_Manager"), rsSubordinates.getInt("idSolde"), rsSubordinates.getInt("ID_Interim"));
                    users.add(subordinate);
                }
            }
            PreparedStatement psSameRole = cnx.prepareStatement(sameRoleQuery);
            psSameRole.setInt(1, id_manager);
            psSameRole.setInt(2, id_manager);
            ResultSet rsSameRole = psSameRole.executeQuery();
            while (rsSameRole.next()) {
                int sameRoleId = rsSameRole.getInt("ID_User");
                if (!isUser_OFFDUTY(sameRoleId)) {
                    User sameRoleUser = new User(sameRoleId, rsSameRole.getString("Nom"), rsSameRole.getString("Prenom"), rsSameRole.getString("Email"), rsSameRole.getString("MDP"), rsSameRole.getString("Image"), rsSameRole.getDate("Creation_Date") != null ? rsSameRole.getDate("Creation_Date").toLocalDate() : null, rsSameRole.getInt("ID_Departement"), rsSameRole.getInt("ID_Manager"), rsSameRole.getInt("idSolde"), rsSameRole.getInt("ID_Interim"));
                    users.add(sameRoleUser);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    public boolean isUserAnInterim(int userId) {
        String query = "SELECT COUNT(*) FROM user WHERE ID_Interim = ?";
        boolean isInterim = false;
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                isInterim = count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isInterim;
    }

    /*public void resetInterimStatus(User user) {
        if (!isUsersManagerOnLeave(user.getIdUser())) {
            setInterimToNull(user.getIdUser());
        }
    }*/

    public boolean isUser_OFFDUTY(int ID_USER) {
        String query = "SELECT * FROM `conge` WHERE `ID_User`=? AND `Statut`='Approuvé' AND CURDATE() BETWEEN `DateDebut` AND `DateFin`";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, ID_USER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public int getHisInterim(int ID_USER) {
        String Query = "SELECT `ID_Interim` FROM `user` WHERE `ID_User`=?";
        int INTERIM = Integer.parseInt(null);
        try {
            PreparedStatement ps = cnx.prepareStatement(Query);
            ps.setInt(1, ID_USER);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                INTERIM = rs.getInt("ID_Interim");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return INTERIM;
    }
}
