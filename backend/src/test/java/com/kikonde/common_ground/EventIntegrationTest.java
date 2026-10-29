package com.kikonde.common_ground;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfiguration.class)
class EventIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        String databaseName = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        assertEquals("common-ground_test", databaseName);

        jdbcTemplate.update("DELETE FROM events");

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Test
    void returnsEmptyListWhenNoEventsExist() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsStoredEventsInIdOrder() throws Exception {
        insertEvent(20L, "Chess and Coffee", "Oakville", 8);
        insertEvent(10L, "Beach Volleyball", "Burlington", 20);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title")
                        .value("Beach Volleyball"))
                .andExpect(jsonPath("$[1].id").value(20));
    }

    @Test
    void returnsRequestedStoredEvent() throws Exception {
        insertEvent(1L, "Beach Volleyball", "Burlington", 20);
        insertEvent(2L, "Chess and Coffee", "Oakville", 8);

        mockMvc.perform(get("/api/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title")
                        .value("Chess and Coffee"))
                .andExpect(jsonPath("$.location").value("Oakville"))
                .andExpect(jsonPath("$.capacity").value(8));
    }

    @Test
    void returns404ForMissingStoredEvent() throws Exception {
        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    private void insertEvent(
            long id,
            String title,
            String location,
            int capacity
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO events (id, title, location, capacity)
                VALUES (?, ?, ?, ?)
                """,
                id,
                title,
                location,
                capacity
        );
    }
}
