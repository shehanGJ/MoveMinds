package com.java.moveminds.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.ProgramNotFoundException;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.models.dto.response.FitnessProgramListResponse;
import com.java.moveminds.models.dto.response.UserProgramResponse;
import com.java.moveminds.models.entities.FitnessProgramEntity;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.models.entities.UserProgramEntity;
import com.java.moveminds.models.enums.Status;
import com.java.moveminds.repositories.FitnessProgramEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.repositories.UserProgramEntityRepository;
import com.java.moveminds.services.LogService;
import com.java.moveminds.services.UserProgramService;

import java.security.Principal;
import java.time.LocalDate;

@Service
public class UserProgramServiceImpl implements UserProgramService {

    @Value("${fitness.program.duration}")
    private Integer programDuration;

    private final UserEntityRepository userRepository;
    private final UserProgramEntityRepository userProgramRepository;
    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final LogService logService;

    public UserProgramServiceImpl(UserEntityRepository userRepository, UserProgramEntityRepository userProgramRepository, FitnessProgramEntityRepository fitnessProgramRepository, LogService logService) {
        this.userRepository = userRepository;
        this.userProgramRepository = userProgramRepository;
        this.fitnessProgramRepository = fitnessProgramRepository;
        this.logService = logService;
    }

    @Override
    public Page<FitnessProgramListResponse> getUserPrograms(Principal principal, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Page<UserProgramEntity> userProgramsPage = userProgramRepository.findAllByUserByUserId(user, pageable);

        LocalDate now = LocalDate.now();

        userProgramsPage.forEach(userProgram -> {
            if (now.isAfter(userProgram.getEndDate().toLocalDate())) {
                if (userProgram.getStatus() == Status.ACTIVE) {
                    userProgram.setStatus(Status.INACTIVE);
                    userProgramRepository.saveAndFlush(userProgram);
                }
            }
        });

        logService.log(principal, "Display of all user programs");

        return userProgramsPage.map(this::mapToFitnessProgramListResponse);
    }

    @Override
    public UserProgramResponse createUserProgram(Principal principal, Integer programId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        FitnessProgramEntity fitnessProgram = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program not found"));

        UserProgramEntity userProgram = new UserProgramEntity();
        userProgram.setUserByUserId(user);
        userProgram.setFitnessProgramByProgramId(fitnessProgram);

        userProgram.setStartDate(new java.sql.Date(System.currentTimeMillis()));

        LocalDate endDate = LocalDate.now().plusDays(this.programDuration);
        userProgram.setEndDate(java.sql.Date.valueOf(endDate));
        userProgram.setStatus(Status.ACTIVE);

        UserProgramEntity savedUser = userProgramRepository.saveAndFlush(userProgram);

        logService.log(principal, "Program creation");

        return mapToUserProgramResponse(savedUser);
    }

    @Override
    public void deleteUserProgram(Principal principal, Integer programId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserProgramEntity userProgram = userProgramRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException("Program not found"));

        if (!userProgram.getUserByUserId().getId().equals(user.getId())) {
            throw new ProgramNotFoundException("Program not found");
        }

        logService.log(principal, "Program deletion");

        userProgramRepository.delete(userProgram);
    }


    private UserProgramResponse mapToUserProgramResponse(UserProgramEntity userProgram) {
        UserProgramResponse response = new UserProgramResponse();
        response.setId(userProgram.getId());
        response.setProgramName(userProgram.getFitnessProgramByProgramId().getName());
        response.setStartDate(userProgram.getStartDate());
        response.setEndDate(userProgram.getEndDate());
        response.setStatus(userProgram.getStatus().name());

        return response;
    }

    private FitnessProgramListResponse mapToFitnessProgramListResponse(UserProgramEntity userProgram) {
        FitnessProgramEntity program = userProgram.getFitnessProgramByProgramId();
        FitnessProgramListResponse response = new FitnessProgramListResponse();

        response.setId(program.getId());
        response.setName(program.getName());
        response.setDescription(program.getDescription());
        response.setDuration(program.getDuration());
        response.setPrice(program.getPrice());
        response.setDifficultyLevel(program.getDifficultyLevel());
        response.setYoutubeUrl(program.getYoutubeUrl());
        response.setPurchaseId(userProgram.getId());

        if (program.getLocation() != null) {
            response.setLocationName(program.getLocation().getName());
        }

        if (program.getUser() != null) {
            response.setInstructorName(this.generateInstructorName(program.getUser()));
            response.setInstructorId(program.getUser().getId());
        }

        response.setStartDate(userProgram.getStartDate());
        response.setEndDate(userProgram.getEndDate());
        response.setStatus(userProgram.getStatus().toString());

        return response;
    }

    private String generateInstructorName(UserEntity user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String username = user.getUsername();

        String instructorName;

        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            instructorName = firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            instructorName = firstName;
        } else if (lastName != null && !lastName.isEmpty()) {
            instructorName = lastName;
        } else {
            instructorName = username;
        }

        return instructorName;
    }

}
