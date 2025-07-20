package tn.bfpme.models;

import tn.bfpme.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeConge {
    private int idTypeConge;
    private String Designation;
    private double Pas;
    private String Periode;
    private double Limite;
    private boolean File;

    public TypeConge() {
    }
    public TypeConge(int idTypeConge, String designation,double Limite) {
        this.idTypeConge = idTypeConge;
        this.Designation = designation;
        this.Limite = Limite;
    }
    public TypeConge(int idTypeConge, String Designation, double pas, boolean file, double Limite, String Periode) {
        this.idTypeConge = idTypeConge;
        this.Designation = Designation;
        this.Pas = pas;
        this.File = file;
        this.Limite = Limite;
        this.Periode = Periode;

    }

    public TypeConge(int typeCongeID, String designation) {
        this.idTypeConge = idTypeConge;
        this.Designation = designation;
    }

    public static int valueOf(String typeConge) {
        Connection cnx = MyDataBase.getInstance().getCnx();
        TypeConge type = new TypeConge();
        String query = "SELECT * FROM typeconge WHERE Designation = ?";
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = MyDataBase.getInstance().getCnx();
            }
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setString(1, typeConge);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // type.setIdTypeConge(rs.getInt("ID_TypeConge"), totalsolde);
                type.setDesignation(rs.getString("Type"));
                type.setPas(rs.getDouble("Pas"));
                type.setFile(rs.getBoolean("File"));
                type.setPeriode(rs.getString("Periode"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public int getIdTypeConge() {
        return idTypeConge;
    }

    public void setIdTypeConge(int idTypeConge) {
        this.idTypeConge = idTypeConge;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String Designation) {
        Designation = Designation;
    }

    public double getPas() {
        return Pas;
    }

    public void setPas(double pas) {
        Pas = pas;
    }

    public String getPeriode() {return Periode;}

    public void setPeriode(String periode) {
        Periode = periode;
    }

    public boolean isFile() {
        return File;
    }

    public void setFile(boolean file) {
        File = file;
    }

    public double getLimite() {
        return Limite;
    }

    public void setLimite(double limite) {
        Limite = limite;
    }

    @Override
    public String toString() {
        return "TypeConge{" +
                "idTypeConge=" + idTypeConge +
                ", Designation=" + Designation +
                ", Pas=" + Pas +
                ", Periode=" + Periode +
                ", File=" + File +
                '}';
    }


    public int getID_TypeConge() {
        return idTypeConge;
    }
}
