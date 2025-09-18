package com.java.moveminds.services.impl;

import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.dto.CustomUserDetails;
import com.java.moveminds.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.AccountActivationException;
import com.java.moveminds.exceptions.UserAlreadyExistsException;
import com.java.moveminds.dto.CustomUserDetails;
import com.java.moveminds.dto.response.JwtAuthenticationResponse;
import com.java.moveminds.dto.requests.LoginRequest;
import com.java.moveminds.dto.requests.SignUpRequest;
import com.java.moveminds.entities.CityEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.repositories.UserEntityRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final CityService cityService;
    private final AuthenticationManager authenticationManager;
    private final LogService logService;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this username already exists.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        CityEntity city = cityService.getCityById(request.getCityId());
        user.setCity(city);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Roles.USER);
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getAuthorities()
        );
        var jwt = jwtService.generateToken(userDetails); // Generate a JWT token for the user
        String activationLink = frontendUrl + "/auth/activate?token=" + jwt; // Generate an activation link
        emailService.sendActivationEmail(user.getEmail(), activationLink); // Send an activation email to the user

        logService.log(null, "User registration " + user.getUsername() ); // Log the user registration

        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .role(user.getRole().name())
                .username(user.getUsername())
                .email(user.getEmail())
                .build(); // Return the JWT token with user details
    }

    @Override
    public ResponseEntity<String> resendEmail(String email, String token) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserAlreadyExistsException("The user with this email address does not exist."));

        String activationLink = frontendUrl + "/auth/activate?token=" + token;
        emailService.sendActivationEmail(user.getEmail(), activationLink);

        logService.log(null, "Resending activation email to user " + user.getUsername() );

        return ResponseEntity.ok("The email has been resent.");
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmailOrUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
        UserDetails userDetails = userService.loadUserByUsername(request.getEmailOrUsername());
        var jwt = jwtService.generateToken(userDetails);
        
        // Extract user details from CustomUserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        logService.log(null, "User login " + request.getEmailOrUsername() );

        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .role(role)
                .username(customUserDetails.getUsername())
                .email(customUserDetails.getEmail())
                .build();
    }

    @Override
    public boolean activateAccount(String token) {
        // Extract email from the token
        String email = jwtService.extractUserEmail(token);

        // Find user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccountActivationException("Invalid token!"));

        // Validate the token
        if (jwtService.isTokenValid(token, user)) {
            // Activate the user account
            user.setActivated(true);
            userRepository.saveAndFlush(user);
            return true;
        }

        // If the token is invalid or expired
        throw new AccountActivationException("The token is invalid or has expired.");
    }

    @Override
    public boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
