package tn.bfpme.models;

public class RoleHierarchie {
    private int idRoleH;
    private int idRoleP;
    private int idRoleC;
    private int idDepartement;
    private String parentRoleName;
    private String childRoleName;

    public RoleHierarchie(int idRoleH, int idRoleP, int idRoleC, int idDepartement) {
        this.idRoleH = idRoleH;
        this.idRoleP = idRoleP;
        this.idRoleC = idRoleC;
        this.idDepartement = idDepartement;
    }

    public RoleHierarchie(int idRoleH, int idRoleP, int idRoleC, String parentRoleName, String childRoleName, int idDepartement) {
        this.idRoleH = idRoleH;
        this.idRoleP = idRoleP;
        this.idRoleC = idRoleC;
        this.parentRoleName = parentRoleName;
        this.childRoleName = childRoleName;
        this.idDepartement = idDepartement;

    }

    public int getIdRoleH() {
        return idRoleH;
    }

    public void setIdRoleH(int idRoleH) {
        this.idRoleH = idRoleH;
    }

    public int getIdRoleP() {
        return idRoleP;
    }

    public void setIdRoleP(int idRoleP) {
        this.idRoleP = idRoleP;
    }

    public int getIdRoleC() {
        return idRoleC;
    }

    public void setIdRoleC(int idRoleC) {
        this.idRoleC = idRoleC;
    }

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public String getChildRoleName() {
        return childRoleName;
    }

    public void setChildRoleName(String childRoleName) {
        this.childRoleName = childRoleName;
    }

    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }

    @Override
    public String toString() {
        return parentRoleName + " - " + childRoleName;
    }
}
