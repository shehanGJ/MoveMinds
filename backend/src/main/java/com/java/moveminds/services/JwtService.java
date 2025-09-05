package com.java.moveminds.services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    String extractUserName(String token);
    String extractUserEmail(String token);
    String generateToken(UserDetails userDetails);

    Claims extractAllClaims(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}