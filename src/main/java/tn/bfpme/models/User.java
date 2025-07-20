package tn.bfpme.models;

import java.time.LocalDate;
import java.util.*;

public class User {
    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String image;
    private String face_data1, face_data2, face_data3, face_data4;
    private LocalDate creationDate;
    private int idManager;
    private int idDepartement;
    private int idRole;
    private int idSolde;
    private int idInterim;

    private String managerName;
    private String departementNom;
    private String roleNom;
    private int ID_UserSolde; // Add this field
    private Map<Integer, Double> soldeMap = new HashMap<>();
    private Map<Integer, String> typeCongeMap = new HashMap<>();

    private double TotalSolde;


    private List<TypeConge> typeConges; // New field for TypeConge objects

    public User() {
        this.typeConges = new ArrayList<>();
    }


    public User(int idUser, String nom, String prenom, String email, String mdp, String image, LocalDate creationDate, int idManager, int idDepartement, int idRole, int ID_UserSolde, String departementNom, String roleNom, String managerName) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.creationDate = creationDate;
        this.idManager = idManager;
        this.idDepartement = idDepartement;
        this.idRole = idRole;
        this.ID_UserSolde = ID_UserSolde;
        this.departementNom = departementNom;
        this.roleNom = roleNom;
        this.managerName = managerName;
    }
    public User(int idUser, String nom, String prenom, String email, String mdp, String image, LocalDate creationDate) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image, TypeConge idUser1, int idDepartement, int idRole) {

    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image, int idDepartement, int idManager, LocalDate creationDate) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.idDepartement = idDepartement;
        this.idManager = idManager;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image,int idManager, int idDepartement, int idRole, LocalDate creationDate,int ID_UserSolde) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.idManager = idManager;
        this.idDepartement = idDepartement;
        this.idRole = idRole;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.ID_UserSolde = ID_UserSolde;
    }
    public User(int idUser, String nom, String prenom, String email, String mdp, String image,int idManager, int idDepartement, int idRole, LocalDate creationDate,int ID_UserSolde,int idInterim) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.idManager = idManager;
        this.idDepartement = idDepartement;
        this.idRole = idRole;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.ID_UserSolde = ID_UserSolde;
        this.idInterim=idInterim;
    }


    public User(int idUser, String nom, String prenom, String email, String mdp, String image) { //Const modif
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image, LocalDate creationDate, int idDepartement, int idManager, int ID_UserSolde) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.idDepartement = idDepartement;
        this.idManager = idManager;
        this.ID_UserSolde = ID_UserSolde;
    }
    public User(int idUser, String nom, String prenom, String email, String mdp, String image,  int idManager,int idDepartement, int idRole) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.idDepartement = idDepartement;
        this.idManager = idManager;
        this.idRole = idRole;
    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image, LocalDate creationDate, int idDepartement, int idManager, int ID_UserSolde, int idRole) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.idDepartement = idDepartement;
        this.idManager = idManager;
        this.idRole = idRole;
        this.ID_UserSolde = ID_UserSolde;

    }
    public User(int idUser, String nom, String prenom, String email, String mdp, String image, LocalDate creationDate, int idDepartement, int idManager, int ID_UserSolde, int idRole, int idInterim) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
        this.idDepartement = idDepartement;
        this.idManager = idManager;
        this.idRole = idRole;
        this.ID_UserSolde = ID_UserSolde;
        this.idInterim=idInterim;

    }

    public User(int idUser, int idDepartement, int idRole) {
        this.idUser = idUser;
        this.idDepartement = idDepartement;
        this.idRole = idRole;

    }

    public User(int idUser, String nom, String prenom, String email, String mdp, String image, int idManager, int idDepartement, int idRole, String faceData1, String faceData2, String faceData3, String faceData4) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.image = image;
        this.idManager = idManager;
        this.idDepartement = idDepartement;
        this.idRole = idRole;
        this.face_data1 = faceData1;
        this.face_data2 = faceData2;
        this.face_data3 = faceData3;
        this.face_data4 = faceData4;

    }

    public int getID_UserSolde() {
        return ID_UserSolde;
    }

    public void setID_UserSolde(int ID_UserSolde) {
        this.ID_UserSolde = ID_UserSolde;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getIdManager() {
        return idManager;
    }

    public void setIdManager(int idManager) {
        this.idManager = idManager;
    }

    public int getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getDepartementNom() {
        return departementNom;
    }

    public void setDepartementNom(String departementNom) {
        this.departementNom = departementNom;
    }

    public String getRoleNom() {
        return roleNom;
    }

    public void setRoleNom(String roleNom) {
        this.roleNom = roleNom;
    }

    public String getFace_data1() {
        return face_data1;
    }

    public void setFace_data1(String face_data1) {
        this.face_data1 = face_data1;
    }

    public String getFace_data2() {
        return face_data2;
    }

    public void setFace_data2(String face_data2) {
        this.face_data2 = face_data2;
    }

    public String getFace_data3() {
        return face_data3;
    }

    public void setFace_data3(String face_data3) {
        this.face_data3 = face_data3;
    }

    public String getFace_data4() {
        return face_data4;
    }

    public void setFace_data4(String face_data4) {
        this.face_data4 = face_data4;
    }

    public String getManagerName() {
        return managerName == null || managerName.isEmpty() ? "Il n'y a pas de manager" : managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }


    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    public void setSoldeByType(int typeCongeId, double totalSolde, String typeConge) {
        this.soldeMap.put(typeCongeId, totalSolde);
        this.typeCongeMap.put(typeCongeId, typeConge);
    }

    public double getSoldeByType(int typeCongeId) {
        return this.soldeMap.getOrDefault(typeCongeId, 0.0);
    }

    public String getTypeConge(int typeCongeId) {
        return this.typeCongeMap.get(typeCongeId);
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", image='" + image + '\'' +
                ", creationDate=" + creationDate +
                ", idManager=" + idManager +
                ", idDepartement=" + idDepartement +
                ", idRole=" + idRole +
                ", ID_UserSolde=" + ID_UserSolde +
                ", departementNom='" + departementNom + '\'' +
                ", roleNom='" + roleNom + '\'' +
                ", managerName='" + managerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return idUser == user.idUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }

    public void addTypeConge(TypeConge typeConge) {
        if (this.typeConges == null) {
            this.typeConges = new ArrayList<>();
        }
        this.typeConges.add(typeConge);
    }


    // Getter for typeConges
    public List<TypeConge> getTypeConges() {
        return typeConges;
    }

    public int getIdSolde() {
        return idSolde;
    }

    public void setIdSolde(int idSolde) {
        this.idSolde = idSolde;
    }


}
