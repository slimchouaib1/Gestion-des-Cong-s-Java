package tn.bfpme.models;

import java.util.Objects;

public class Role {
    private int idRole;
    private int RoleParent;
    private String nom;
    private String description;
    private int level ;

    private String parentRoleName; // New field for parent role name
    private String childRoleName; // New field for child role name

    public Role(int idRole, String nom, String description) {
        this.idRole = idRole;
        this.nom = nom;
        this.description = description;
    }


    public Role(int idRole, String nom, String description, int RoleParent) {
        this.idRole = idRole;
        this.nom = nom;
        this.description = description;
        this.RoleParent = RoleParent;
    }
    public Role(int idRole, String nom, String description, int RoleParent, int level) {
        this.idRole = idRole;
        this.nom = nom;
        this.description = description;
        this.RoleParent = RoleParent;
        this.level = level;
    }



    public Role() {

    }

    // Getters and Setters
    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRoleParent() {
        return RoleParent;
    }

    public void setRoleParent(int RoleParent) {
        this.RoleParent = RoleParent;
    }

    public String getParentRoleName() {
        return parentRoleName == null || parentRoleName.isEmpty() ? "Il n'y a pas de rôle parent" : parentRoleName;
    }

    public String getChildRoleName() {
        return childRoleName == null || childRoleName.isEmpty() ? "Il n'y a pas de rôle fils" : childRoleName;
    }
    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public void setChildRoleName(String childRoleName) {
        this.childRoleName = childRoleName;
    }

    @Override
    public String toString() {
        return nom + " - " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return idRole == role.idRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRole);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
