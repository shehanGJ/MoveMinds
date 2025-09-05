package com.java.moveminds.services;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendActivationEmail(String to, String activationLink);
    void sendEmail(String to, String subject, String text);
}
