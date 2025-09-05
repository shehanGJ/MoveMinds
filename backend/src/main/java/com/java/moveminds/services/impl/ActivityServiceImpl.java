package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.models.dto.requests.ActivityRequest;
import com.java.moveminds.models.dto.response.ActivityResponse;
import com.java.moveminds.models.entities.ActivityEntity;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.repositories.ActivityEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.ActivityService;
import com.java.moveminds.services.LogService;

import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityEntityRepository activityRepository;
    private final UserEntityRepository userRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;

    @Override
    public List<ActivityResponse> getAllActivitiesByUser(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<ActivityEntity> activityEntities = activityRepository.findAllByUser(user);

        logService.log(principal, "Pregled aktivnosti");

        return activityEntities
                .stream()
                .map(activityEntity -> modelMapper.map(activityEntity, ActivityResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityResponse addActivity(Principal principal, ActivityRequest activityRequest) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ActivityEntity activity = new ActivityEntity();
        activity.setUser(user);
        activity.setActivityType(activityRequest.getActivityType());
        activity.setDuration(activityRequest.getDuration());
        activity.setIntensity(activityRequest.getIntensity());
        activity.setResult(activityRequest.getResult());
        activity.setLogDate(Date.valueOf(activityRequest.getLogDate()));

        ActivityEntity savedActivity = activityRepository.saveAndFlush(activity);

        logService.log(principal, "Dodavanje aktivnosti");

        return modelMapper.map(savedActivity, ActivityResponse.class);
    }
}
