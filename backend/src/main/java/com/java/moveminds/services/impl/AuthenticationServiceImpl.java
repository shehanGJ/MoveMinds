package com.java.moveminds.services.impl;

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
import com.java.moveminds.models.dto.CustomUserDetails;
import com.java.moveminds.models.dto.response.JwtAuthenticationResponse;
import com.java.moveminds.models.dto.requests.LoginRequest;
import com.java.moveminds.models.dto.requests.SignUpRequest;
import com.java.moveminds.models.entities.CityEntity;
import com.java.moveminds.models.enums.Roles;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.*;

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

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        CityEntity city = cityService.getCityById(request.getCityId());
        user.setCity(city);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Roles.USER);
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

        logService.log(null, "Registracija korisnika " + user.getUsername() ); // Log the user registration

        return JwtAuthenticationResponse.builder().token(jwt).build(); // Return the JWT token
    }

    @Override
    public ResponseEntity<String> resendEmail(String email, String token) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserAlreadyExistsException("Korisnik sa ovim emailom ne postoji."));

        String activationLink = frontendUrl + "/auth/activate?token=" + token;
        emailService.sendActivationEmail(user.getEmail(), activationLink);

        logService.log(null, "Ponovno slanje emaila za aktivaciju korisniku " + user.getUsername() );

        return ResponseEntity.ok("Email je ponovo poslat.");
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

        logService.log(null, "User login " + request.getEmailOrUsername() );

        return JwtAuthenticationResponse.builder().token(jwt).build();
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
