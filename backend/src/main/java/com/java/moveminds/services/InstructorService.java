package com.java.moveminds.services;

import com.java.moveminds.dto.requests.FitnessProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.dto.response.InstructorStatsResponse;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import com.java.moveminds.dto.response.ProgramStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface InstructorService {
    InstructorStatsResponse getInstructorStats(Principal principal);
    FitnessProgramResponse createProgram(Principal principal, FitnessProgramRequest programRequest, List<MultipartFile> files) throws IOException;
    FitnessProgramResponse updateProgram(Principal principal, Integer programId, FitnessProgramRequest programRequest, List<MultipartFile> files, List<String> removedImages) throws IOException;
    void deleteProgram(Principal principal, Integer programId) throws IOException;
    Page<FitnessProgramListResponse> getMyPrograms(Principal principal, Pageable pageable, String sort);
    FitnessProgramResponse getProgramDetails(Principal principal, Integer programId);
    List<ProgramEnrollmentResponse> getProgramEnrollments(Principal principal, Integer programId);
    Page<ProgramEnrollmentResponse> getAllEnrollments(Principal principal, Pageable pageable);
    ProgramEnrollmentResponse updateEnrollmentStatus(Principal principal, Integer enrollmentId, String status);
    Page<ProgramEnrollmentResponse> getStudents(Principal principal, Pageable pageable);
    ProgramStatsResponse getProgramStats(Principal principal, Integer programId);
}
