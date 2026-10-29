package com.kikonde.common_ground;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateEventRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be greater than zero")
        Integer capacity
) {
}