package com.java.moveminds.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.EmailSendException;
import com.java.moveminds.services.EmailService;
import com.java.moveminds.services.LogService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final LogService logService;
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    @Value("${app.email.from:}")
    private String fromAddress;

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        if (!emailEnabled) {
            logService.log(null, "Email sending disabled - skipping activation email to: " + to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress);
            }

            helper.setTo(to);
            helper.setSubject("Welcome to MoveMinds - Activate Your Account");
            helper.setText(buildActivationEmail(activationLink), true);

            logService.log(null, "Sending an email to activate your account");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Error sending email", e);
        }
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            logService.log(null, "Email sending disabled - skipping email to: " + to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress);
            }

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            logService.log(null, "Sending email");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("Error sending email", e);
        }
    }

    private String buildActivationEmail(String activationLink) {
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;\">" +
                "<div style=\"background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                "<div style=\"text-align: center; margin-bottom: 30px;\">" +
                "<h1 style=\"color: #2563eb; margin: 0; font-size: 28px;\">Welcome to MoveMinds!</h1>" +
                "<p style=\"color: #6b7280; margin: 10px 0 0 0;\">Your Fitness Journey Starts Here</p>" +
                "</div>" +
                "<p style=\"color: #374151; line-height: 1.6; margin-bottom: 20px;\">" +
                "Thank you for registering with MoveMinds! We're excited to help you achieve your fitness goals." +
                "</p>" +
                "<p style=\"color: #374151; line-height: 1.6; margin-bottom: 30px;\">" +
                "To complete your account setup and start your fitness journey, please click the button below to activate your account:" +
                "</p>" +
                "<div style=\"text-align: center; margin: 30px 0;\">" +
                "<a href=\"" + activationLink + "\" style=\"background-color: #2563eb; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; display: inline-block; font-weight: bold; font-size: 16px;\">" +
                "Activate My Account" +
                "</a>" +
                "</div>" +
                "<p style=\"color: #6b7280; font-size: 14px; line-height: 1.6; margin-top: 30px;\">" +
                "If you didn't create an account with MoveMinds, please ignore this email. This activation link will expire in 24 hours." +
                "</p>" +
                "<hr style=\"border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;\">" +
                "<p style=\"color: #6b7280; font-size: 14px; margin: 0;\">" +
                "Thanks,<br>" +
                "<strong>The MoveMinds Team</strong>" +
                "</p>" +
                "</div>" +
                "</div>";
    }
}
