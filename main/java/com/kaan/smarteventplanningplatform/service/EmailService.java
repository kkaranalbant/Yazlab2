/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author kaan
 */
@Service
public class EmailService {

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(EmailService.class);

    }

    @Value("${spring.mail.username}")
    private String companyEmail;

    @Value("${company}")
    private String companyName;

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String fullName, String url) throws MessagingException, UnsupportedEncodingException {
        mailSender.send(createEmailVerificationMessage(fullName, url, email));
    }

    public void sendResetMail(String email, String fullName, String url) throws MessagingException, UnsupportedEncodingException {
        mailSender.send(createPasswordResetMessage(fullName, url, email));
    }

    private MimeMessage createEmailVerificationMessage(String fullName, String url, String email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Please Verify Your Email");
        helper.setFrom(companyEmail, companyName);
        helper.setTo(email);
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Smart Event Planning Platform";
        content = content.replace("[[name]]", fullName);
        content = content.replace("[[URL]]", url);
        helper.setText(content, true);
        return mimeMessage;
    }

    private MimeMessage createPasswordResetMessage(String fullName, String url, String email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Please Reset Your Password");
        helper.setFrom(companyEmail, companyName);
        helper.setTo(email);
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your password :<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Reset Password</a></h3>"
                + "Thank you,<br>"
                + "Smart Event Planning Platform";
        content = content.replace("[[name]]", fullName);
        content = content.replace("[[URL]]", url);
        helper.setText(content, true);
        return mimeMessage;
    }

}
