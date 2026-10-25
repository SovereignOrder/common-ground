package com.kikonde.common_ground;

// Data Transfer Object
public record EventResponse(
        long id,
        String title,
        String location,
        int capacity
) {
}
