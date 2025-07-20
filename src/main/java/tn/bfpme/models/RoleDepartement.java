package tn.bfpme.models;

public class RoleDepartement {
    private int ID_RoleDepart;
    private int ID_Role;
    private int ID_Depart;



    public RoleDepartement() {
    }

    public RoleDepartement(int ID_RoleDepart, int ID_Role, int ID_Depart) {
        this.ID_RoleDepart = ID_RoleDepart;
        this.ID_Role = ID_Role;
        this.ID_Depart = ID_Depart;
    }

    public int getID_RoleDepart() {
        return ID_RoleDepart;
    }

    public void setID_RoleDepart(int ID_RoleDepart) {
        this.ID_RoleDepart = ID_RoleDepart;
    }

    public int getID_Role() {
        return ID_Role;
    }

    public void setID_Role(int ID_Role) {
        this.ID_Role = ID_Role;
    }

    public int getID_Depart() {
        return ID_Depart;
    }

    public void setID_Depart(int ID_Depart) {
        this.ID_Depart = ID_Depart;
    }

    @Override
    public String toString() {
        return "RoleDepartement{" +
                "ID_RoleDepart=" + ID_RoleDepart +
                ", ID_Role=" + ID_Role +
                ", ID_Depart=" + ID_Depart +
                '}';
    }
}
