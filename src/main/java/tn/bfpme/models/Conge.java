package tn.bfpme.models;

import tn.bfpme.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Conge {
    private int idConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int idTypeConge;
    private String TypeName;
    private Statut statut;
    private int idUser;
    private String file;
    private String description;
    private String message;
    private String designation;
    private TypeConge typeConge; // Changed to store a TypeConge object

    public Conge() {}

    public Conge(int idConge, LocalDate dateDebut, LocalDate dateFin, int idTypeConge, Statut statut, int idUser, String file, String description,String message) {
        this.idConge = idConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idTypeConge = idTypeConge;
        this.statut = statut;
        this.idUser = idUser;
        this.file = file;
        this.description = description;
        this.message = message;
    }
    public Conge(int idConge, LocalDate dateDebut, LocalDate dateFin, int idTypeConge, Statut statut, int idUser, String file, String description) {
        this.idConge = idConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idTypeConge = idTypeConge;
        this.statut = statut;
        this.idUser = idUser;
        this.file = file;
        this.description = description;
    }
    public Conge(int idConge, LocalDate dateDebut, LocalDate dateFin, String designation, Statut statut, int idUser, String file, String description) {
        this.idConge = idConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idTypeConge = idTypeConge;
        this.statut = statut;
        this.idUser = idUser;
        this.file = file;
        this.description = description;
        this.designation = designation;
    }
    public Conge(LocalDate dateDebut, LocalDate dateFin, int idTypeConge) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idTypeConge = idTypeConge;
    }
    public Conge(LocalDate dateDebut, LocalDate dateFin, String designation) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.designation = designation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getIdConge() {
        return idConge;
    }

    public void setIdConge(int idConge) {
        this.idConge = idConge;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public int getIdTypeConge() {
        return idTypeConge;
    }

    public void setTypeConge(int idTypeConge) {
        this.idTypeConge = idTypeConge;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDescription() {return description;}
    public void setDescription(String description) {
        this.description = description;
    }
    public String getMessage() {return message;}
    public void setMessage(String notification) {
        this.message = notification;
    }
    public TypeConge getTypeConge2() {
        return typeConge;
    }

    public void setTypeConge2(TypeConge typeConge) {
        this.typeConge = typeConge;
    }

    @Override
    public String toString() {
        return "Conge{" +
                "idConge=" + idConge +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", idTypeConge=" + idTypeConge +
                ", statut=" + statut +
                ", idUser=" + idUser +
                ", file='" + file +
                ", description='" + description +
                ", message='" + message +
                '}';
    }

    public double getCongeDays() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }
        return 0;
    }


    public TypeConge getTypeConge() {
        TypeConge typeConge = null;
        String query = "SELECT tc.* FROM typeconge tc JOIN conge c ON tc.ID_TypeConge = c.TypeConge WHERE c.ID_Conge = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, this.idConge);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    typeConge = new TypeConge();
                    typeConge.setIdTypeConge(rs.getInt("ID_TypeConge"));
                    typeConge.setDesignation(rs.getString("Designation"));
                    typeConge.setPas(rs.getDouble("Pas"));
                    typeConge.setPeriode(rs.getString("Periode"));
                    typeConge.setFile(rs.getBoolean("File"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeConge;
    }
}
