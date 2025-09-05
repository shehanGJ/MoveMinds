package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.models.dto.response.FitnessProgramListResponse;
import com.java.moveminds.models.dto.response.UserProgramResponse;
import com.java.moveminds.services.UserProgramService;

import java.security.Principal;

@RestController
@RequestMapping("/user-programs")
@RequiredArgsConstructor
public class UserProgramController {

    private final UserProgramService userProgramService;

    // Endpoint for getting user programs
    @GetMapping
    public ResponseEntity<Page<FitnessProgramListResponse>> getUserPrograms(
            Principal principal,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FitnessProgramListResponse> userPrograms = userProgramService.getUserPrograms(principal, pageable);
        return ResponseEntity.ok(userPrograms);
    }

    // Endpoint for creating user program
    @PostMapping("/{programId}")
    public ResponseEntity<UserProgramResponse> createUserProgram(
            Principal principal,
            @PathVariable Integer programId) {
        if (programId == null || programId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            UserProgramResponse userProgram = userProgramService.createUserProgram(principal, programId);
            return ResponseEntity.ok(userProgram);
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalStateException ex) {
            // Duplicate enrollment
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).build();
        }
    }

    // Endpoint for deleting user program
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchasedProgram(@PathVariable("id") Integer programId, Principal principal) {
        if (programId == null || programId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        userProgramService.deleteUserProgram(principal, programId);
        return ResponseEntity.noContent().build(); // Status 204 No Content
    }
}
