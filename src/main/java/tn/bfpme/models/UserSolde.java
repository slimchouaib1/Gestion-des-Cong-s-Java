package tn.bfpme.models;

public class UserSolde {

    private int UD_UserSolde;
    private int ID_User;
    private int ID_TypeConge;
    private double TotalSolde;
    private String Designation;


    public UserSolde() {
    }

    public UserSolde(int UD_UserSolde, int ID_User, int ID_TypeConge, double totalSolde) {
        this.UD_UserSolde = UD_UserSolde;
        this.ID_User = ID_User;
        this.ID_TypeConge = ID_TypeConge;
        this.TotalSolde = totalSolde;
    }
    public UserSolde(int UD_UserSolde, int ID_User, int ID_TypeConge, double totalSolde,String Designation) {
        this.UD_UserSolde = UD_UserSolde;
        this.ID_User = ID_User;
        this.ID_TypeConge = ID_TypeConge;
        this.TotalSolde = totalSolde;
        this.Designation = Designation;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getUD_UserSolde() {
        return UD_UserSolde;
    }

    public void setUD_UserSolde(int UD_UserSolde) {
        this.UD_UserSolde = UD_UserSolde;
    }

    public int getID_User() {
        return ID_User;
    }

    public void setID_User(int ID_User) {
        this.ID_User = ID_User;
    }

    public int getID_TypeConge() {
        return ID_TypeConge;
    }

    public void setID_TypeConge(int ID_TypeConge) {
        this.ID_TypeConge = ID_TypeConge;
    }

    public double getTotalSolde() {
        return TotalSolde;
    }

    public void setTotalSolde(double totalSolde) {
        TotalSolde = totalSolde;
    }


    @Override
    public String toString() {
        return "UserSolde{" +
                "UD_UserSolde=" + UD_UserSolde +
                ", ID_User=" + ID_User +
                ", ID_TypeConge=" + ID_TypeConge +
                ", TotalSolde=" + TotalSolde +
                '}';
    }
}
