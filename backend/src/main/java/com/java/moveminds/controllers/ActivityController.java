package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.requests.ActivityRequest;
import com.java.moveminds.dto.response.ActivityResponse;
import com.java.moveminds.services.ActivityService;
import com.java.moveminds.services.PdfService;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final PdfService pdfService; 

    // Endpoint for getting all activities by user
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivitiesByUser(Principal principal) {
        return ResponseEntity.ok(activityService.getAllActivitiesByUser(principal));
    }

    // Endpoint for adding a new activity
    @PostMapping
    public ResponseEntity<ActivityResponse> addActivity(Principal principal, @RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.addActivity(principal, activityRequest));
    }

    // Endpoint for downloading a PDF report of all activities
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadActivityReport(Principal principal) {
        List<ActivityResponse> activities = activityService.getAllActivitiesByUser(principal);
        ByteArrayInputStream pdfStream = pdfService.generateActivityReport(principal, activities);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=activity_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }
}
