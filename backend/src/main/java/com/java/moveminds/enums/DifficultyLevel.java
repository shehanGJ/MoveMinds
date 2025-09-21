package com.java.moveminds.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DifficultyLevel {
    BEGINNER(1, "Beginner"),
    INTERMEDIATE(2, "Intermediate"),
    ADVANCED(3, "Advanced");

    private final int order;
    private final String displayName;

    DifficultyLevel(int order, String displayName) {
        this.order = order;
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static DifficultyLevel fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // Try to match by enum name first (e.g., "BEGINNER")
        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        
        // Try to match by display name (e.g., "Beginner")
        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level.getDisplayName().equalsIgnoreCase(value)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("Unknown difficulty level: " + value);
    }
}
