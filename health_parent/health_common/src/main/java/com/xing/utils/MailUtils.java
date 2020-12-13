package com.xing.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * send email utils
 */
public class MailUtils {
    private String USER; // sender email address
    private String PASSWORD;

    public MailUtils(String username, String password) {
        this.USER = username;
        this.PASSWORD = password;
    }

    /**
     *
     * @param to receiver address
     * @param text content of the email
     * @param title title of the email
     */
    public boolean sendMail(String to, String text, String title){
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port" , "465");
            props.put("mail.smtp.ssl.enable" , "true");
            props.put("mail.user", USER);
            props.put("mail.password", PASSWORD);

            // construct authenticator used to verify SMTP identity
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // username , password
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };

            Session mailSession = Session.getInstance(props, authenticator);
            MimeMessage message = new MimeMessage(mailSession);

            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            // setup receiver
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // setup title
            message.setSubject(title);

            // setup content
            message.setContent(text, "text/html;charset=UTF-8");
            // send email
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        MailUtils utils = new MailUtils("rexleetest2020@gmail.com" , "idonotknow");
        utils.sendMail("xingxingli2017@gmail.com" , "test mail util" , "test mail util");
    }
}