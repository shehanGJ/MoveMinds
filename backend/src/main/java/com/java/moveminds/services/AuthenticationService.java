package com.java.moveminds.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.java.moveminds.dto.response.JwtAuthenticationResponse;
import com.java.moveminds.dto.requests.LoginRequest;
import com.java.moveminds.dto.requests.SignUpRequest;

@Service
public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);
    ResponseEntity<String> resendEmail(String email, String token);
    JwtAuthenticationResponse login(LoginRequest request);
    boolean activateAccount(String token);
    boolean checkUsername(String username);
}