package com.kikonde.common_ground;

import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    public List<EventResponse> getEvents() {
        return List.of(
                new EventResponse(
                        1L,
                        "Beach Volleyball",
                        "Burlington",
                        20
                ),
                new EventResponse(
                        2L,
                        "Chess and Coffee",
                        "Oakville",
                        8
                ),
                new EventResponse(
                        3L,
                        "Group Walk",
                        "Burlington",
                        10
                )
        );
    }
    public Optional<EventResponse> getEventById(long id) {
        for (EventResponse event : getEvents()) {
            if (event.id() == id) {
                return Optional.of(event);
            }
        }

        return Optional.empty();
    }
}