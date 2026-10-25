package com.kikonde.common_ground;

public record EventResponse(
        long id,
        String title,
        String location,
        int capacity
) {

}
