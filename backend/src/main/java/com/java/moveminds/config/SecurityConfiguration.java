package com.java.moveminds.config;

import com.java.moveminds.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.java.moveminds.services.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration // Marks this class as a configuration class
@EnableWebSecurity // Enables Spring Security for the application
@EnableMethodSecurity // Enables method-level config
@RequiredArgsConstructor // Annotation to generate a constructor with final fields
public class SecurityConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class); // Logger for logging information
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT filter for authentication
    private final UserService userService; // User service to manage user details
    private final CorsConfig corsConfig; // CORS configuration bean
    private final PasswordEncoder passwordEncoder; // Password encoder bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Security Filter Chain");
        http
                .cors(cors -> {
                    cors.configurationSource(corsConfig.corsConfigurationSource()); // Sets the CORS configuration source
                    logger.info("CORS configuration applied");
                })
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection because... I don't need it right now :D
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 console frames
                .authorizeHttpRequests(request -> request
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll() // Authentication endpoints
                        .requestMatchers("/upload/**").permitAll() // File upload endpoints
                        .requestMatchers("/uploads/**").permitAll() // Static file access
                        .requestMatchers("/h2-console/**").permitAll() // H2 console
                        .requestMatchers("/api/test/**").permitAll() // Test endpoints
                        .requestMatchers("/cities/**").permitAll() // City data
                        .requestMatchers("/news/**").permitAll() // News endpoints
                        .requestMatchers("/attributes").permitAll() // Attributes data
                        .requestMatchers("/comments").permitAll() // Comments data
                        
                        // Program endpoints - Read access for all, Write access for instructors/admins
                        .requestMatchers(HttpMethod.GET, "/programs").permitAll() // List programs
                        .requestMatchers(HttpMethod.GET, "/programs/{id}").permitAll() // Get program details
                        .requestMatchers(HttpMethod.GET, "/programs/with-attributes").permitAll() // Get programs with attributes
                        .requestMatchers(HttpMethod.POST, "/programs").hasAnyRole("INSTRUCTOR", "ADMIN") // Create programs
                        .requestMatchers(HttpMethod.PUT, "/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN") // Update programs
                        .requestMatchers(HttpMethod.DELETE, "/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN") // Delete programs
                        
                        // User program endpoints - Authenticated users only
                        .requestMatchers("/user-programs/**").authenticated() // User program management
                        
                        // Admin endpoints - Admin only
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin management
                        
                        // Instructor endpoints - Instructors and admins
                        .requestMatchers("/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN") // Instructor management
                        
                        // Enhanced admin user management endpoints
                        .requestMatchers(HttpMethod.GET, "/admin/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/users/**").hasRole("ADMIN")
                        
                        // Enhanced admin system management endpoints
                        .requestMatchers("/admin/system/**").hasRole("ADMIN")
                        
                        // Enhanced instructor program management endpoints
                        .requestMatchers(HttpMethod.GET, "/instructor/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/instructor/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/instructor/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/instructor/programs/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        
                        // Enhanced instructor student management endpoints
                        .requestMatchers("/instructor/students/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        
                        // Enhanced instructor dashboard endpoints
                        .requestMatchers("/instructor/dashboard/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        
                        // User endpoints - Authenticated users
                        .requestMatchers(HttpMethod.GET, "/user/**").permitAll() // Public user info
                        .requestMatchers("/user/**").authenticated() // User management
                        
                        // Message endpoints - Authenticated users
                        .requestMatchers("/message/**").authenticated() // Messaging
                        
                        // Activity endpoints - Authenticated users
                        .requestMatchers("/activities/**").authenticated() // User activities
                        
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // Configures session management to be stateless (jwt)
                .authenticationProvider(authenticationProvider()) // Sets the authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Adds JWT filter before UsernamePasswordAuthenticationFilter
        logger.info("Security Filter Chain configured successfully");
        return http.build(); // Builds the config filter chain
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Configuring DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Sets the user details service
        authProvider.setPasswordEncoder(passwordEncoder); // Sets the password encoder
        logger.info("DaoAuthenticationProvider configured successfully");
        return authProvider; // Returns the authentication provider
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Creating AuthenticationManager bean");
        return config.getAuthenticationManager(); // Retrieves and returns the authentication manager
    }
}
