package com.java.moveminds.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.UserProgramResponse;

import java.security.Principal;

@Service
public interface UserProgramService {
    Page<FitnessProgramListResponse> getUserPrograms(Principal principal, Pageable pageable);
    UserProgramResponse createUserProgram(Principal principal, Integer programId);
    @Transactional
    void deleteUserProgram(Principal principal, Integer programId);
}
