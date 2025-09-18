package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.java.moveminds.dto.ExerciseDTO;
import com.java.moveminds.services.DailyExerciseService;

import java.io.IOException;

@RestController
@RequestMapping("/daily-exercises")
@RequiredArgsConstructor
public class DailyExercisesController {
    private final DailyExerciseService dailyExerciseService;

    // Endpoint to get daily exercises
    @GetMapping
    public ExerciseDTO[] getDailyExercises() throws IOException {
        return this.dailyExerciseService.getDailyExercises();
    }
}
