package tn.bfpme.models;

public class Notification {
    private int idNotif;
    private int idUser;
    private String notification;
    private int statut;
    private String notifcontent;

    public Notification() {}

    public Notification(int idUser, int idNotif, String notification, int statut, String notifcontent) {
        this.idUser = idUser;
        this.idNotif = idNotif;
        this.notification = notification;
        this.statut = statut;
        this.notifcontent = notifcontent;
    }

    public Notification(int idUser, int idNotif, String notification, String notifcontent) {
        this.idUser = idUser;
        this.idNotif = idNotif;
        this.notification = notification;
        this.notifcontent = notifcontent;

    }

    public Notification(int idUser, String notification) {
        this.idUser = idUser;
        this.notification = notification;
    }

    public Notification(int idUser, String notification, int statut) {
        this.idUser = idUser;
        this.notification = notification;
        this.statut = statut;
    }

    public int getIdNotif() {
        return idNotif;
    }

    public void setIdNotif(int idNotif) {
        this.idNotif = idNotif;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getNotifcontent() {
        return notifcontent;
    }

    public void setNotifcontent(String notifcontent) {
        this.notifcontent = notifcontent;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "idNotif=" + idNotif +
                ", idUser=" + idUser +
                ", notification='" + notification + '\'' +
                ", statut=" + statut +
                '}';
    }
}
