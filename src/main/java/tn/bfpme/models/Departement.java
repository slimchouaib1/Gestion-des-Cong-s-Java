package tn.bfpme.models;

public class Departement {
    private int idDepartement;
    private String nom;
    private String description;
    private int parentDept;
    private int level;
    private String parentDeptName; // New field for parent department name

    public Departement() {}

    public Departement(int idDepartement, String nom, String description, int parentDept) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.description = description;
        this.parentDept = parentDept;
    }
    public Departement(int idDepartement, String nom, String description, int parentDept,int level) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.description = description;
        this.parentDept = parentDept;
        this.level = level;
    }

    // Constructor with new field
    public Departement(int idDepartement, String nom, String description, int parentDept, String parentDeptName) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.description = description;
        this.parentDept = parentDept;
        this.parentDeptName = parentDeptName;
    }

    public Departement(int idDepartement, String nom, String description) {
        this.idDepartement = idDepartement;
        this.nom = nom;
        this.description = description;
    }

    // Getters and setters
    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
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

    public int getParentDept() {
        return parentDept;
    }

    public void setParentDept(int parentDept) {
        this.parentDept = parentDept;
    }

    public String getParentDeptName() {
        return parentDeptName == null || parentDeptName.isEmpty() ? "Il n'y a pas de département parent" : parentDeptName;
    }
    public void setParentDeptName(String parentDeptName) {
        this.parentDeptName = parentDeptName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    @Override
    public String toString() {
        return nom;
    }
}
