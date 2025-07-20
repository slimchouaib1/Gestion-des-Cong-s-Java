package tn.bfpme.utils;

import javafx.scene.control.Alert;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mails {

    public static String generateAlternativeProposee(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous écris concernant votre demande de congé pour la période du %s au %s. Malheureusement, en raison des exigences actuelles de notre département, je ne suis pas en mesure d'approuver votre demande de congé pour les dates spécifiées.\n" +
                        "\n" +
                        "Cependant, je vous propose d'envisager de prendre vos congés à une autre date ultérieure. Je suis à votre disposition pour discuter de nouvelles dates qui conviendraient à la fois à vos besoins et aux exigences de notre département.\n" +
                        "\n" +
                        "Je vous remercie de votre compréhension et reste à votre disposition pour toute question.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }

    public static String generateEquiteEtEquilibre(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous informe que votre demande de congé pour la période du %s au %s ne peut pas être approuvée à ce moment. Plusieurs de vos collègues ont également demandé des congés pendant cette période, et afin de maintenir un équilibre et une équité au sein de l'équipe, je ne peux malheureusement pas autoriser votre demande.\n" +
                        "\n" +
                        "Je vous encourage à soumettre une nouvelle demande pour une période différente. N'hésitez pas à me consulter pour trouver un moment qui conviendrait à la fois à vos besoins et à ceux de l'équipe.\n" +
                        "\n" +
                        "Merci de votre compréhension.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }

    public static String generatePolitiqueDeRotationDesConges(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Votre demande de congé pour la période du %s au %s ne peut être approuvée cette fois-ci. Conformément à notre politique de rotation des congés, nous devons nous assurer que chaque membre de l'équipe a la possibilité de prendre des congés pendant les périodes de vacances populaires.\n" +
                        "\n" +
                        "Je vous propose de replanifier vos congés à une autre période. Vous pouvez consulter le calendrier des congés disponibles et soumettre une nouvelle demande.\n" +
                        "\n" +
                        "Merci de votre compréhension et de votre coopération.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }

    public static String generateCongesCumulesNonAutorises(String employeeName, String startDate, String endDate, String managerName, String managerRole, int maxDays) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous écris pour vous informer que votre demande de congé pour la période du %s au %s ne peut pas être approuvée. Selon la politique de notre entreprise, les congés cumulés au-delà de %d jours ne sont pas autorisés.\n" +
                        "\n" +
                        "Je vous invite à soumettre une nouvelle demande respectant cette limite. Si vous avez des questions concernant notre politique de congés, je suis disponible pour en discuter.\n" +
                        "\n" +
                        "Merci de votre compréhension.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, maxDays, managerName, managerRole
        );
    }

    public static String generateRemplacementNonDisponible(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je dois malheureusement refuser votre demande de congé pour la période du %s au %s. À l'heure actuelle, il n'y a pas de remplaçant disponible pour couvrir vos tâches pendant votre absence.\n" +
                        "\n" +
                        "Je vous encourage à proposer une autre période pour vos congés, et nous ferons de notre mieux pour organiser un remplacement adéquat.\n" +
                        "\n" +
                        "Merci de votre compréhension et de votre coopération.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }

    public static String generateEvaluationDePerformanceOuAudit(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous écris pour vous informer que votre demande de congé pour la période du %s au %s ne peut pas être approuvée. Pendant cette période, nous avons planifié une évaluation de performance/audit important(e) pour notre département, et votre présence est nécessaire.\n" +
                        "\n" +
                        "Je vous invite à soumettre une nouvelle demande de congé pour une période ultérieure. Je reste à votre disposition pour toute question ou pour discuter des dates alternatives possibles.\n" +
                        "\n" +
                        "Merci de votre compréhension.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }
    public static String generateRefusDemande(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous écris pour vous informer que votre demande de congé pour la période du %s au %s ne peut pas être approuvée pour des raisons opérationnelles et organisationnelles.\n" +
                        "\n" +
                        "Nous comprenons l'importance de vos besoins personnels et nous regrettons de ne pouvoir accéder à votre demande à ce moment. Nous vous encourageons à soumettre une nouvelle demande de congé pour une période ultérieure qui pourrait mieux convenir aux besoins opérationnels de notre département.\n" +
                        "\n" +
                        "Nous restons à votre disposition pour toute question ou pour discuter des dates alternatives possibles.\n" +
                        "\n" +
                        "Merci de votre compréhension.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }
    public static String generateApprobationDemande(String employeeName, String startDate, String endDate, String managerName, String managerRole) {
        return String.format(
                "Cher/Chère %s,\n" +
                        "\n" +
                        "Je vous écris pour vous informer que votre demande de congé pour la période du %s au %s a été approuvée.\n" +
                        "\n" +
                        "Nous vous souhaitons une période de congé agréable et reposante. Si vous avez besoin de quoi que ce soit avant votre départ, n'hésitez pas à me contacter.\n" +
                        "\n" +
                        "Merci de votre travail acharné et de votre dévouement.\n" +
                        "\n" +
                        "Cordialement,\n" +
                        "\n" +
                        "%s\n" +
                        "%s",
                employeeName, startDate, endDate, managerName, managerRole
        );
    }


    public static void sendEmail(String to ,String subject,String messageText) {
    String from = "waves.esprit@gmail.com"; // change accordingly
    final String username = "waves.esprit@gmail.com"; // change accordingly
    final String password = "tgao tbqg wudl aluo"; // change accordingly

    // Assuming you are sending email through Gmail
    String host = "smtp.gmail.com";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");

    // Get the Session object.
    Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(username, password);
                }
            });

    try {
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        message.setText(messageText);

        // Send message
        Transport.send(message);

        System.out.println("Message envoyé....");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Message envoyé!");
        alert.showAndWait();

    } catch (
            MessagingException e) {
        throw new RuntimeException(e);
    }
}

}
