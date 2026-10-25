package com.kikonde.common_ground;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/api/events")
    public List<EventResponse> getEvents() {
        return eventService.getEvents();
    }

    @GetMapping("/api/events/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable("id") long id
    ) {
        Optional<EventResponse> result =
                eventService.getEventById(id);

        return result
                .map(ResponseEntity::ok)
                .orElseGet( () -> ResponseEntity.notFound().build() );

    }
}
