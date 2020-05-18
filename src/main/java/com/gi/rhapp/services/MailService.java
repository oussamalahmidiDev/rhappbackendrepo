package com.gi.rhapp.services;

import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@Component
public class MailService {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String FROM;

    @Value("${spring.mail.host}")
    private String HOST;

    @Value("${spring.mail.port}")
    private String PORT;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean SMTP_AUTH;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private boolean SMTP_SSL;

    @Value("${spring.mail.username}")
    private String USERNAME;

    @Value("${spring.mail.password}")
    private String PASSWORD;

//    public void sendCompteDetails (Salarie salarie) {
//        String url = getConfirmationURL(salarie) + "&action=compte_details&ccn=" + salarie.getNumSomme();
//        try {
//            MimeMessage message = getMimeMessage(
//                    salarie.getUser().getEmail(),
//                    "Details de votre compte AKINOBANK",
//                    "Bienvenue "  + salarie.getPrenom() + " sur AKINOBANK"
//            );
//            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
//
//            Context context = new Context();
//            context.setVariable("receiver", salarie);
//            context.setVariable("url", url);
//            String content = templateEngine.process("mails/salarie_info", context);
//            messageHelper.setText(content, true);
//            // Send message
//            Transport.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//    }

    public void sendPasswordRecoveryMail (User user) {
        String url = getConfirmationURL(user) + "&action=forgot_password";
        try {
            MimeMessage message = getMimeMessage(
                user.getEmail(),
                "Récuperation du mot de passe",
                "Bienvenue "  + user.getPrenom()
            );
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            Context context = new Context();
            context.setVariable("url", url);
            context.setVariable("receiver", user);
            String content = templateEngine.process("mails/forgot_password", context);
            messageHelper.setText(content, true);
            // Send message
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
    public void sendVerificationMail (User user) {
        String url = getConfirmationURL(user) + "&action=confirm";
        try {
            MimeMessage message = getMimeMessage(
                    user.getEmail(),
                    "Verification d'email",
                    "Bienvenue "  + user.getPrenom() + " sur RH"
            );
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            Context context = new Context();
            context.setVariable("url", url);
            context.setVariable("receiver", user);
            String content = templateEngine.process("mails/salarie_info", context);
            messageHelper.setText(content, true);
            // Send message
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    private MimeMessage getMimeMessage(String to, String subject, String text) throws MessagingException {
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(getMailSession());

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(FROM));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message;
        message.setText(text);
        return message;
    }


    private Session getMailSession() {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.ssl.enable", SMTP_SSL);
        properties.put("mail.smtp.auth", SMTP_AUTH);
        properties.put("mail.mime.charset", "UTF-8");

        // Get the Session objec
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }

        });
        // Used to debug SMTP issues
        session.setDebug(true);
        return session;
    }

    private String getConfirmationURL(User user) {
        String rootURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // generation du lien de confirmation et envoie par mail
        return rootURL + "/confirm?token=" + user.getVerificationToken();
    }

}
