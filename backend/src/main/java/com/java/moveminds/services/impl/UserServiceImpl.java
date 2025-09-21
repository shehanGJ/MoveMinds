package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.InvalidOldPasswordException;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.dto.AdviserDTO;
import com.java.moveminds.dto.CustomUserDetails;
import com.java.moveminds.dto.requests.UpdatePasswordRequest;
import com.java.moveminds.dto.requests.UpdateUserRequest;
import com.java.moveminds.dto.response.NonAdvisersResponse;
import com.java.moveminds.dto.response.UserInfoResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.LogService;
import com.java.moveminds.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userRepository;
    private final CityServiceImpl cityService;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Check for user by username
        Optional<UserEntity> userOptional = userRepository.findByUsername(usernameOrEmail);

        if (userOptional.isEmpty()) {
            // If not found by username, check by email
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        UserEntity user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        logService.log(null, "User acquisition");
        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getEmail(), authorities);
    }

    @Override
    public UserInfoResponse getUserInfo(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        logService.log(null, "Display user information");

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public UserInfoResponse updateUserInfo(String username, UpdateUserRequest updateUserRequest) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setAvatarUrl(updateUserRequest.getAvatarUrl());

        if (updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updateUserRequest.getFirstName());
        }
        if (updateUserRequest.getLastName() != null && !updateUserRequest.getLastName().equals(user.getLastName())) {
            user.setLastName(updateUserRequest.getLastName());
        }
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getBiography() != null && !updateUserRequest.getBiography().equals(user.getBiography())) {
            user.setBiography(updateUserRequest.getBiography());
        }
        if (updateUserRequest.getCityId() != null && !updateUserRequest.getCityId().equals(user.getCity().getId())) {
            user.setCity(cityService.getCityById(updateUserRequest.getCityId()));
        }

        logService.log(null, "Updating user information");
        userRepository.save(user);

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("The old password is incorrect!");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        logService.log(null, "Password update");
        userRepository.saveAndFlush(user);
    }

    @Override
    public String getAvatar(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user.getAvatarUrl();
    }

    @Override
    public Boolean isActive(String username) {
        UserEntity user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user.isVerified();
    }

    @Override
    public Integer getUserId(String username) {
        UserEntity user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("The user with this email address does not exist."));
        return user.getId();
    }

    @Override
    public UserInfoResponse getUserInfoById(Integer id) {

        UserEntity user = userRepository.findById(id).
                orElseThrow(() -> new UsernameNotFoundException("User not found: "));

        return new UserInfoResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBiography(),
                user.getCity().getId()
        );
    }

    @Override
    public List<AdviserDTO> getAllAdvisers() {
        List<UserEntity> advisers = userRepository.findAllByRole(Roles.INSTRUCTOR);
        if (advisers.isEmpty()) {
            throw new UserNotFoundException("There are no advisors in the system.");
        }
        return advisers.stream()
                .map(this::mapToAdviserDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<NonAdvisersResponse> getAllNonAdvisers(Principal principal) {

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        List<UserEntity> users = userRepository.findAllByRoleNotAndUsernameNot(Roles.INSTRUCTOR, user.getUsername());

        if (users.isEmpty()) {
            throw new UserNotFoundException("There are no users in the system.");
        }

        return users.stream()
                .map(this::mapToNonAdviser)
                .collect(Collectors.toList());
    }


    private NonAdvisersResponse mapToNonAdviser(UserEntity userEntity) {
        NonAdvisersResponse response = new NonAdvisersResponse();
        response.setUserId(userEntity.getId());
        response.setName(getDisplayName(userEntity));

        return response;
    }

    private AdviserDTO mapToAdviserDTO(UserEntity adviser) {
        AdviserDTO adviserDTO = new AdviserDTO();
        adviserDTO.setId(adviser.getId());
        adviserDTO.setName(getDisplayName(adviser));
        adviserDTO.setEmail(adviser.getEmail());
        return adviserDTO;
    }

    private static String getDisplayName(UserEntity user) {
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