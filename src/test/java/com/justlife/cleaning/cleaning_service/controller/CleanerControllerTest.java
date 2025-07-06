package com.justlife.cleaning.cleaning_service.controller;

import com.justlife.cleaning.cleaning_service.dto.CleanerResponse;
import com.justlife.cleaning.cleaning_service.service.CleanerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CleanerController.class)
@Import(CleanerControllerTest.MockBeans.class)
class CleanerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CleanerService cleanerService;

    static class MockBeans {
        @Bean CleanerService cleanerService() {
            return Mockito.mock(CleanerService.class);
        }
    }

    @Test
    void getAvailableCleaners_returnsCleanerDtos() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 7, 8, 10, 0);
        LocalDateTime end   = start.plusHours(2);

        CleanerResponse resp = new CleanerResponse(101L, "Alice", "Van-A");
        Mockito.when(cleanerService.findAvailableCleaners(start, end))
                .thenReturn(List.of(resp));

        mockMvc.perform(get("/api/v1/cleaners/available")
                        .param("startTime", start.toString())
                        .param("endTime",   end.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101L))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].vehicleType").value("Van-A"));
    }

    @Test
    void getAvailableCleaners_returnsEmptyListWhenNoneAvailable() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 7, 8, 16, 0);
        LocalDateTime end   = start.plusHours(2);

        Mockito.when(cleanerService.findAvailableCleaners(start, end))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cleaners/available")
                        .param("startTime", start.toString())
                        .param("endTime",   end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}