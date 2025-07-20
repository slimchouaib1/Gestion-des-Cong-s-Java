package tn.bfpme.models;

public class EmailsTemplates {
    private int id_Email;
    private String object;
    private String message;

    public EmailsTemplates() {
    }

    public EmailsTemplates(int id_Email, String object, String message) {
        this.id_Email = id_Email;
        this.object = object;
        this.message = message;
    }

    public EmailsTemplates(String object, String message) {
        this.object = object;
        this.message = message;
    }

    public int getId_Email() {
        return id_Email;
    }

    public void setId_Email(int id_Email) {
        this.id_Email = id_Email;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EmailsTemplates{" +
                "id_Email=" + id_Email +
                ", object='" + object + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
