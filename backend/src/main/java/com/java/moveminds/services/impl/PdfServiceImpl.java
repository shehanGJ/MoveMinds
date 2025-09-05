package com.java.moveminds.services.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.models.dto.response.ActivityResponse;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.PdfService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final UserEntityRepository userRepository;

    @Override
    public ByteArrayInputStream generateActivityReport(Principal principal, List<ActivityResponse> activities) {

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

        try {
            int index = 1;
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("----------------------------------------"));
            document.add(new Paragraph("Log of user activity"));
            document.add(new Paragraph("----------------------------------------"));
            document.add(new Paragraph("Username: " + this.getUsername(user)));
            document.add(new Paragraph("----------------------------------------"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("----------------------------------------"));
            for (ActivityResponse activity : activities) {
                document.add(new Paragraph("(" + index + ")"));
                document.add(new Paragraph(
                        "Type of activity: " + activity.getActivityType() +
                                "\nDatum: " + dateFormat.format(activity.getLogDate()) +
                                "\nDuration: " + activity.getDuration() + " minute" +
                                "\nIntensity: " + activity.getIntensity() +
                                "\nWeight: " + String.format("%.2f", activity.getResult()) + " kg\n"
                ));
                document.add(new Paragraph("----------------------------------------"));
                index++;
            }

            document.add(new Paragraph("----------------------------------------"));
            document.add(new Paragraph("Thank you for using our application."));
            document.add(new Paragraph("----------------------------------------"));

            document.close();
        } catch (DocumentException ignored) {
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String getUsername(UserEntity user) {
        if (user == null) {
            return "";
        }
        String displayName;
        if (user.getFirstName() != null && user.getLastName() != null) {
            displayName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            displayName = user.getFirstName();
        } else if (user.getLastName() != null) {
            displayName = user.getLastName();
        } else {
            displayName = user.getUsername();
        }

        return displayName;
    }
}
