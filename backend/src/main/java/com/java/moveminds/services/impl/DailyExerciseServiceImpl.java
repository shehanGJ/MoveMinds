package com.java.moveminds.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.ExerciseFetchException;
import com.java.moveminds.models.dto.ExerciseDTO;
import com.java.moveminds.services.DailyExerciseService;
import com.java.moveminds.services.LogService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class DailyExerciseServiceImpl implements DailyExerciseService {
    @Value("${apininja.api.url}")
    private String apiUrl;
    @Value("${apininja.api.key}")
    private String apiKey;

    private final LogService logService;

    public DailyExerciseServiceImpl(LogService logService) {
        this.logService = logService;
    }

    @Override
    public ExerciseDTO[] getDailyExercises() throws IOException {
        // this is the recommended way to handle the connection to api-ninjas... for whatever reason
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("X-Api-Key", apiKey);

        logService.log(null, "Presentation of daily exercises");

        try (InputStream responseStream = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseStream, ExerciseDTO[].class);
        } catch (Exception e) {
            throw new ExerciseFetchException("API communication error: " + e.getMessage(), e);
        }
    }
}
