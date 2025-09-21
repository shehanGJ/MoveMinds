package com.java.moveminds.services.impl;

import com.java.moveminds.dto.response.AdminStatsResponse;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.exceptions.UnauthorizedException;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserEntityRepository userRepository;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminStatsResponse getAdminStats(Principal principal) {
        validateAdminAccess(principal);
        
        long totalUsers = userRepository.count();
        long totalInstructors = userRepository.countByRole(Roles.INSTRUCTOR);
        long totalAdmins = userRepository.countByRole(Roles.ADMIN);
        
        // Additional stats would be calculated from other repositories
        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(userRepository.countByIsVerified(true))
                .totalInstructors(totalInstructors)
                .totalAdmins(totalAdmins)
                .totalPrograms(0L) // TODO: Implement from FitnessProgramRepository
                .activePrograms(0L) // TODO: Implement from FitnessProgramRepository
                .totalEnrollments(0L) // TODO: Implement from UserProgramRepository
                .activeEnrollments(0L) // TODO: Implement from UserProgramRepository
                .totalRevenue(BigDecimal.ZERO) // TODO: Implement revenue calculation
                .monthlyRevenue(BigDecimal.ZERO) // TODO: Implement monthly revenue calculation
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<AdminUserResponse> getAllUsers(Principal principal, Pageable pageable, Roles role, String search) {
        validateAdminAccess(principal);
        
        Page<UserEntity> users;
        if (role != null && search != null && !search.trim().isEmpty()) {
            users = userRepository.findByRoleAndUsernameContainingIgnoreCase(role, search, pageable);
        } else if (role != null) {
            users = userRepository.findByRole(role, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        
        return users.map(this::mapToAdminUserResponse);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse getUserById(Principal principal, Integer userId) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse updateUserRole(Principal principal, Integer userId, Roles role) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setRole(role);
        UserEntity savedUser = userRepository.save(user);
        
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse updateUserStatus(Principal principal, Integer userId, boolean active) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setVerified(active);
        UserEntity savedUser = userRepository.save(user);
        
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(Principal principal, Integer userId) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deleting themselves
        if (user.getUsername().equals(principal.getName())) {
            throw new UnauthorizedException("Cannot delete your own account");
        }
        
        userRepository.delete(user);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminUserResponse> getAllInstructors(Principal principal) {
        validateAdminAccess(principal);
        
        List<UserEntity> instructors = userRepository.findAllByRole(Roles.INSTRUCTOR);
        return instructors.stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse promoteToInstructor(Principal principal, Integer userId) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setRole(Roles.INSTRUCTOR);
        UserEntity savedUser = userRepository.save(user);
        
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse demoteFromInstructor(Principal principal, Integer userId) {
        validateAdminAccess(principal);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setRole(Roles.USER);
        UserEntity savedUser = userRepository.save(user);
        
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<String> getSystemLogs(Principal principal, int limit) {
        validateAdminAccess(principal);
        
        // TODO: Implement actual log retrieval from logging system
        return List.of("System logs functionality to be implemented");
    }

    private void validateAdminAccess(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }
        
        UserEntity admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Admin user not found"));
        
        if (admin.getRole() != Roles.ADMIN) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    private AdminUserResponse mapToAdminUserResponse(UserEntity user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .avatarUrl(user.getAvatarUrl())
                .biography(user.getBiography())
                .cityName(user.getCity() != null ? user.getCity().getName() : null)
                .createdAt(LocalDateTime.now()) // TODO: Add createdAt field to UserEntity
                .lastLoginAt(LocalDateTime.now()) // TODO: Add lastLoginAt field to UserEntity
                .programCount(user.getFitnessPrograms() != null ? (long) user.getFitnessPrograms().size() : 0L)
                .enrollmentCount(user.getUserPrograms() != null ? (long) user.getUserPrograms().size() : 0L)
                .build();
    }
}
