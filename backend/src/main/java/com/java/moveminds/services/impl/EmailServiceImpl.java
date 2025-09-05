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
            helper.setSubject("Task activation");
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
        // This could be a template, but just for this example I will hardcode it
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px;\">" +
                "<h2>Task activation</h2>" +
                "<p>Thank you for registering. Click on the link below to activate your account:</p>" +
                "<a href=\"" + activationLink + "\">Activate the task</a>" +
                "<p>We are glad to be hanging out.</p>" +
                "<p>Thanks,</p>" +
                "<p>Your team</p>" +
                "</div>";
    }
}
