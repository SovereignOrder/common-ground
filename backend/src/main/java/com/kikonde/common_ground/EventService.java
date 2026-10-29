package com.kikonde.common_ground;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getEvents() {
        return eventRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<EventResponse> getEventById(long id) {
        return eventRepository.findById(id)
                .map(this::toResponse);
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getCapacity()
        );
    }

    @Transactional(readOnly = false)
    public EventResponse createEvent(CreateEventRequest request) {
        Event event = new Event(
                request.title().strip(),
                request.location().strip(),
                request.capacity()
        );

        Event savedEvent = eventRepository.save(event);

        return toResponse(savedEvent);
    }
}