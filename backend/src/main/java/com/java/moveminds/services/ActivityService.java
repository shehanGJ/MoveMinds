package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.models.dto.requests.ActivityRequest;
import com.java.moveminds.models.dto.response.ActivityResponse;

import java.security.Principal;
import java.util.List;

@Service
public interface ActivityService {
    List<ActivityResponse> getAllActivitiesByUser(Principal principal);
    ActivityResponse addActivity(Principal principal, ActivityRequest activityRequest);
}
