package tn.esprit.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class Mail {
    // Configuration for the email sending service
    private static final String FROM_EMAIL = "ferjeniameni37@gmail.com";
    private static final String PASSWORD = "oapxcnoghrzryfxi";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    /**
     * Sends an email to the specified address with the given content.
     *
     * @param toEmail the recipient's email address
     * @param subject the subject of the email
     * @param content the content of the email in HTML format
     * @throws MessagingException if an error occurs during the sending process
     */
    public void sendEmail(String toEmail, String subject, String content) throws MessagingException {
        Session session = createSession();
        Message message = new MimeMessage(session);

        // Set message details
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject(subject);

        // Create email content
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        message.setContent(multipart);

        // Send the email
        Transport.send(message);

        System.out.println("Email sent successfully to " + toEmail);
    }

    /**
     * Creates a mail session with the required configuration.
     *
     * @return a configured Session instance
     */
    private Session createSession() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_HOST);

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }
}
