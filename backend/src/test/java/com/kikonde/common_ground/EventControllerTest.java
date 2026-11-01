package com.kikonde.common_ground;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.verifyNoInteractions;

class EventControllerTest {

    private MockMvc mockMvc;
    private EventService service;

    @BeforeEach
    void setUp() {
        service = mock(EventService.class);

        EventResponse requestedEvent = new EventResponse(
                2L,
                "Chess and Coffee",
                "Oakville",
                8
        );

        when(service.getEvents()).thenReturn(List.of(
                new EventResponse(
                        1L,
                        "Beach Volleyball",
                        "Burlington",
                        20
                ),
                requestedEvent,
                new EventResponse(
                        3L,
                        "Group Walk",
                        "Burlington",
                        10
                )
        ));

        when(service.getEventById(2L))
                .thenReturn(Optional.of(requestedEvent));

        when(service.getEventById(999L))
                .thenReturn(Optional.empty());

        EventController controller = new EventController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void returnsEventList() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void returnsRequestedEvent() throws Exception {
        mockMvc.perform(get("/api/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Chess and Coffee"));
    }

    @Test
    void returns404ForMissingEvent() throws Exception {
        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void rejectsZeroCapacity() throws Exception {
        String requestBody = """
        {
          "title": "Rejected Capacity Check",
          "location": "Burlington",
          "capacity": 0
        }
        """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsMissingCapacity() throws Exception {
        String requestBody = """
        {
          "title": "Missing Capacity Check",
          "location": "Burlington"
        }
        """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}