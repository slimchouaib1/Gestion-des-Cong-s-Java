package tn.bfpme.models;

public class UserRole {
    private int idUser;
    private int idRole;

    public UserRole(int idUser, int idRole) {
        this.idUser = idUser;
        this.idRole = idRole;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public User getUser() {
        return null;
    }

    public Role getRole() {
        return null;
    }

    public void setUser(User user) {
        if (user != null) {
            this.idUser = user.getIdUser();
        } else {
            this.idUser = 0;
        }
    }

    public void setRole(Role role) {
        if (role != null) {
            this.idRole = role.getIdRole();
        }
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "idUser=" + idUser +
                ", idRole=" + idRole +
                '}';
    }
}