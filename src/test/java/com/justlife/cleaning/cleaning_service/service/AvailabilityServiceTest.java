package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.repository.BookingRepository;
import com.justlife.cleaning.cleaning_service.repository.CleanerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    @Mock
    private CleanerRepository cleanerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Cleaner sampleCleaner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleCleaner = Cleaner.builder()
                .id(1L)
                .name("Test Cleaner")
                .vehicle(null)
                .build();
    }

    @Test
    void getAvailableSlots_shouldReturnFullDayWhenNoBookings() {
        LocalDate date = LocalDate.now();
        int durationHours = 2;

        when(cleanerRepository.findAll()).thenReturn(List.of(sampleCleaner));
        when(bookingRepository.findByStartTimeBetween(any(), any())).thenReturn(Collections.emptyList());

        Map<Cleaner, List<LocalTime>> result = availabilityService.getAvailableSlots(date, durationHours);

        assertThat(result).containsKey(sampleCleaner);
        assertThat(result.get(sampleCleaner)).isNotEmpty();
    }

    @Test
    void getAvailableCleanersForSlot_shouldReturnCleanerWhenNoOverlap() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);

        when(cleanerRepository.findAll()).thenReturn(List.of(sampleCleaner));
        when(bookingRepository.hasOverlap(eq(sampleCleaner), any(), any(), isNull())).thenReturn(false);

        List<Cleaner> result = availabilityService.getAvailableCleanersForSlot(date, time, 2);

        assertThat(result).containsExactly(sampleCleaner);
    }

}