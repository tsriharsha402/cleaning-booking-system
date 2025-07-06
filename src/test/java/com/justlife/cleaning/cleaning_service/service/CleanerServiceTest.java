package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.domain.Vehicle;
import com.justlife.cleaning.cleaning_service.dto.CleanerResponse;
import com.justlife.cleaning.cleaning_service.repository.CleanerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CleanerServiceTest {

    @Mock
    private CleanerRepository cleanerRepository;

    @InjectMocks
    private CleanerService cleanerService;

    private Cleaner alice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Vehicle vanA = Vehicle.builder().id(1L).label("Van-A").build();
        alice = Cleaner.builder().id(101L).name("Alice").vehicle(vanA).build();
    }

    @Test
    void findAvailableCleaners_returnsCleanerDtos() {
        LocalDateTime from = LocalDateTime.of(2025, 7, 8, 10, 0);
        LocalDateTime to   = from.plusHours(2);

        when(cleanerRepository.findAvailableCleaners(from, to))
                .thenReturn(List.of(alice));

        List<CleanerResponse> result = cleanerService.findAvailableCleaners(from, to);

        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(r -> {
                    assertThat(r.id()).isEqualTo(101L);
                    assertThat(r.name()).isEqualTo("Alice");
                    assertThat(r.vehicleType()).isEqualTo("Van-A");
                });

        verify(cleanerRepository).findAvailableCleaners(from, to);
    }

    @Test
    void findAvailableCleaners_returnsEmptyWhenNoneAvailable() {
        LocalDateTime from = LocalDateTime.of(2025, 7, 8, 10, 0);
        LocalDateTime to   = from.plusHours(2);

        when(cleanerRepository.findAvailableCleaners(from, to))
                .thenReturn(Collections.emptyList());

        List<CleanerResponse> result = cleanerService.findAvailableCleaners(from, to);

        assertThat(result).isEmpty();
        verify(cleanerRepository).findAvailableCleaners(from, to);
    }

}