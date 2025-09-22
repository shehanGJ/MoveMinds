package com.java.moveminds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}") // Injects the value of 'upload.path' from application properties
    private String uploadPath;

    @Value("${spring.mvc.static-path-pattern}") // Injects the value of 'spring.mvc.static-path-pattern' from application properties
    private String pathPattern;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(pathPattern) // Adds a resource handler for the specified path pattern
                .addResourceLocations("file:" + uploadPath); // Sets the location of the resources to the upload path
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/files/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition", "Content-Type", "Content-Length")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
