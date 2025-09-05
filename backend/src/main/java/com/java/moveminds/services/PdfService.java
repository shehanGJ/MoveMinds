package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.models.dto.response.ActivityResponse;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@Service
public interface PdfService {
    ByteArrayInputStream generateActivityReport(Principal principal, List<ActivityResponse> activities);
}
