package com.java.moveminds.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.java.moveminds.models.dto.response.ErrorResponse;
import com.java.moveminds.services.JwtService;
import com.java.moveminds.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService; // Service for working with JWT tokens
    private final UserService userService; // Service for working with user details
    private final ObjectMapper objectMapper; // Mapper for converting objects to/from JSON

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            // Retrieves the Authorization header from the request
            String jwt = null; // Variable to store the JWT token
            String userEmail = null; // Variable to store the user email

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                // Checks if the Authorization header is not empty and starts with "Bearer "
                jwt = authHeader.substring(7); // Extracts the JWT token from the header
                userEmail = jwtService.extractUserName(jwt); // Extracts the username (email) from the JWT token
                logger.info("JWT token found for user: {}", userEmail); // Logs the extracted user email
            }

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Checks if the user email is not null and the user is not already authenticated
                UserDetails userDetails = userService.loadUserByUsername(userEmail);
                // Loads the user details using the email
                logger.info("User details loaded for user: {}", userEmail); // Logs that user details are loaded

                if (jwtService.isTokenValid(jwt, userDetails)) { // Checks if the JWT token is valid
                    Claims claims = jwtService.extractAllClaims(jwt); // Extracts all claims from the JWT token
                    @SuppressWarnings("unchecked")
                    List<String> roles = claims.get("roles", List.class); // Retrieves the roles from the JWT claims
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new) // Converts roles to GrantedAuthority objects
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    // Creates an authentication token
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Sets the authentication details from the request
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Sets the authentication token in the security context
                    logger.info("User {} authenticated with roles: {}", userEmail, roles); // Logs successful authentication
                } else {
                    logger.warn("Invalid JWT token for user: {}", userEmail); // Logs invalid JWT token
                }
            }
        } catch (JwtException e) { // Handles JWT exceptions
            logger.error("JWT exception occurred: {}", e.getMessage()); // Logs the JWT exception
            handleException(request, response); // Handles the exception and sets a standardized error response
            return; // Skips further filter processing
        }

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                "JWT_EXPIRED",
                "Your session has expired. Please log in again.",
                request.getRequestURI()
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Sets the HTTP status to 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Sets the response content type to JSON
        response.setCharacterEncoding("UTF-8"); // Sets the response character encoding to UTF-8
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // Writes the error response as JSON to the response
    }
}
