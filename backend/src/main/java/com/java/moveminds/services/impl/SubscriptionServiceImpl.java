package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.java.moveminds.entities.CategoryEntity;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.SubscriptionEntity;
import com.java.moveminds.repositories.CategoryEntityRepository;
import com.java.moveminds.repositories.FitnessProgramEntityRepository;
import com.java.moveminds.repositories.SubscriptionEntityRepository;
import com.java.moveminds.services.EmailService;
import com.java.moveminds.services.LogService;
import com.java.moveminds.services.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final SubscriptionEntityRepository subscriptionRepository;
    private final EmailService emailService;
    private final CategoryEntityRepository categoryRepository;
    private final LogService logService;


    @Scheduled(cron = "0 0 6 * * ?")
    public void sendDailySubscriptionEmails() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<CategoryEntity> categories = categoryRepository.findAll();
        for (CategoryEntity category : categories) {
            List<FitnessProgramEntity> newPrograms = fitnessProgramRepository.findAllByCategoryAndCreatedAtAfter(category, yesterday);
            if (!newPrograms.isEmpty()) {
                List<SubscriptionEntity> subscriptions = subscriptionRepository.findAllByCategory(category);
                for (SubscriptionEntity subscription : subscriptions) {
                    String emailContent = createEmailContent(newPrograms);
                    emailService.sendEmail(subscription.getUser().getEmail(), "New programs for the category: " + category.getName(), emailContent);
                }
            }
        }
        logService.log(null, "Sending emails about new programs");

    }

    private String createEmailContent(List<FitnessProgramEntity> newPrograms) {
        StringBuilder content = new StringBuilder();
        content.append("<div style=\"font-family: Arial, sans-serif; font-size: 16px;\">");
        content.append("<h2>New programs created in the last 24 hours</h2>");

        for (FitnessProgramEntity program : newPrograms) {
            content.append("<div style=\"margin-bottom: 20px; padding: 10px; border-bottom: 1px solid #ccc;\">")
                    .append("<h3 style=\"color: #333;\">Naziv: ").append(program.getName()).append("</h3>")
                    .append("<p><strong>Opis:</strong> ").append(program.getDescription()).append("</p>")
                    .append("<p><strong>Cijena:</strong> ").append(program.getPrice()).append(" €</p>")
                    .append("<p><strong>Težina:</strong> ").append(program.getDifficultyLevel()).append("</p>")
                    .append("</div>");
        }

        content.append("<p>Thank you for using our platform!</p>");
        content.append("</div>");

        return content.toString();
    }

}
