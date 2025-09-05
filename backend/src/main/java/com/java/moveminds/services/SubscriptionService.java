package com.java.moveminds.services;

import org.springframework.stereotype.Service;

@Service
public interface SubscriptionService {
    void sendDailySubscriptionEmails();
}
