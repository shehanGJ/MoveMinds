package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.dto.ExerciseDTO;

import java.io.IOException;

@Service
public interface DailyExerciseService {
    ExerciseDTO[] getDailyExercises() throws IOException;
}
