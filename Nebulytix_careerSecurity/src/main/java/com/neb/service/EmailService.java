package com.neb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Generic mail sender using MimeMessage
    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            System.out.println("Mail sent successfully!");

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendApplicationMail(String to, String subject, String text) {
        sendMail(to, subject, text);
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Job Application OTP Verification";
        String text = "Dear Candidate,\n\nYour OTP for verification is: " + otp
                + "\n\nThank you,\nNeb HR Team";
        sendMail(to, subject, text);
    }

    public void sendConfirmationEmail(String to, String fullName, String jobTitle) {
        String subject = "Job Application Submitted Successfully";
        String body = 
                "Dear " + fullName + ",\n\n" +
                "Thank you for applying for the role of " + jobTitle + ".\n\n" +
                "Your application has been received.\n\n" +
                "Regards,\nNeb HR Team";

        sendMail(to, subject, body);
    }

    public void sendEmail(String email, String subject, String message) {
        sendMail(email, subject, message);
    }
}
