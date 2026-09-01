package com.java.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
	 MALE, FEMALE, OTHER;
	
	@JsonCreator
    public static Gender fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // Triggers @NotNull validation
        }
        return Gender.valueOf(value.toUpperCase());
    }
}
