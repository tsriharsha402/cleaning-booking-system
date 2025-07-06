package com.justlife.cleaning.cleaning_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.dto.*;
import com.justlife.cleaning.cleaning_service.service.AvailabilityService;
import com.justlife.cleaning.cleaning_service.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookingController.class)
@Import(BookingControllerTest.MockBeans.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AvailabilityService availabilityService;

    static class MockBeans {
        @Bean BookingService bookingService() {
            return Mockito.mock(BookingService.class);
        }
        @Bean AvailabilityService availabilityService() {
            return Mockito.mock(AvailabilityService.class);
        }
    }

    @Test
    void createBooking_returns201() throws Exception {
        BookingCreateRequest req = new BookingCreateRequest(
                LocalDate.of(2025, 7, 10),
                LocalTime.of(10, 0),
                2,
                "John Doe",
                List.of(101L, 102L)
        );

        BookingResponse resp = new BookingResponse(
                1L,
                LocalDateTime.of(2025, 7, 10, 10, 0),
                LocalDateTime.of(2025, 7, 10, 12, 0),
                2,
                "John Doe",
                10L,
                List.of(101L, 102L)
        );

        Mockito.when(bookingService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.durationH").value(2))
                .andExpect(jsonPath("$.cleanerIds[0]").value(101));
    }

    @Test
    void getDailyAvailability_returnsSlots() throws Exception {
        LocalDate date = LocalDate.of(2025, 7, 10);
        int duration = 2;

        Cleaner cleaner = Cleaner.builder().id(1L).build();
        Map<Cleaner, List<LocalTime>> mockAvailability = Map.of(
                cleaner, List.of(LocalTime.of(9, 0), LocalTime.of(14, 0))
        );

        Mockito.when(availabilityService.getAvailableSlots(date, duration))
                .thenReturn(mockAvailability);

        mockMvc.perform(get("/api/v1/availability")
                        .param("date", date.toString())
                        .param("durationHours", String.valueOf(duration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cleanerId").value(1L));
    }

    @Test
    void getAvailableCleanersForSlot_returnsCleanerIds() throws Exception {
        LocalDate date = LocalDate.of(2025, 7, 10);
        LocalTime time = LocalTime.of(10, 0);
        int duration = 2;

        Cleaner cleaner = Cleaner.builder().id(101L).build();
        Mockito.when(availabilityService.getAvailableCleanersForSlot(date, time, duration))
                .thenReturn(List.of(cleaner));

        mockMvc.perform(get("/api/v1/availability/slot")
                        .param("date", date.toString())
                        .param("startTime", time.toString())
                        .param("durationHours", String.valueOf(duration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(101L));
    }

    @Test
    void updateBooking_returnsUpdatedBooking() throws Exception {
        BookingUpdateRequest req = new BookingUpdateRequest(
                LocalDate.of(2025, 7, 10),
                LocalTime.of(14, 0),
                2,
                List.of(1L, 2L)
        );

        BookingResponse resp = new BookingResponse(
                1L,
                LocalDateTime.of(2025, 7, 10, 14, 0),
                LocalDateTime.of(2025, 7, 10, 16, 0),
                2,
                "Jane",
                20L,
                List.of(1L, 2L)
        );

        Mockito.when(bookingService.update(eq(1L), any())).thenReturn(resp);

        mockMvc.perform(patch("/api/v1/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.durationH").value(2));
    }

    @Test
    void getBooking_returnsBooking() throws Exception {
        BookingResponse resp = new BookingResponse(
                1L,
                LocalDateTime.of(2025, 7, 10, 10, 0),
                LocalDateTime.of(2025, 7, 10, 12, 0),
                2,
                "John Doe",
                15L,
                List.of(101L)
        );

        Mockito.when(bookingService.get(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteBooking_returns204() throws Exception {
        Mockito.doNothing().when(bookingService).delete(1L);

        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isNoContent());
    }

}