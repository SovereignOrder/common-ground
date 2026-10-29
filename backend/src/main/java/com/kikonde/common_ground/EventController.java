package com.kikonde.common_ground;

import java.util.List;
import java.util.Optional;
import java.net.URI;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/api/events")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        EventResponse createdEvent =
                eventService.createEvent(request);

        URI location = URI.create("/api/events/" + createdEvent.id() );
        return ResponseEntity.created(location).body(createdEvent);
    }
}
