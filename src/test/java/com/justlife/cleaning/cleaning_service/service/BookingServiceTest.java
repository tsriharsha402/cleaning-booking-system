package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.domain.*;
import com.justlife.cleaning.cleaning_service.dto.BookingCreateRequest;
import com.justlife.cleaning.cleaning_service.dto.BookingResponse;
import com.justlife.cleaning.cleaning_service.exception.BookingValidationException;
import com.justlife.cleaning.cleaning_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepo;
    @Mock CleanerRepository cleanerRepo;

    @InjectMocks BookingService bookingService;

    Cleaner cleaner1;
    Cleaner cleaner2;

    @BeforeEach
    void setUp() {
        Vehicle v1 = Vehicle.builder().id(1L).label("Van-A").build();
        cleaner1 = Cleaner.builder().id(101L).name("Alice").vehicle(v1).build();
        cleaner2 = Cleaner.builder().id(102L).name("Bob").vehicle(v1).build();
    }

    @Test
    void shouldCreateBookingWithTwoCleaners() {
        BookingCreateRequest req = new BookingCreateRequest(
                LocalDate.of(2025, 7, 7),
                LocalTime.of(10, 0),
                2,
                "John Doe",
                List.of(101L, 102L)
        );
        when(cleanerRepo.findAllById(List.of(101L, 102L)))
                .thenReturn(List.of(cleaner1, cleaner2));
        when(bookingRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var resp = bookingService.create(req);

        assertEquals(2, resp.cleanerIds().size());
        assertEquals(LocalTime.of(10,0), resp.startTime().toLocalTime());

        verify(bookingRepo).save(any());
    }

    @Test
    void shouldRejectBookingOnFriday() {
        BookingCreateRequest req = new BookingCreateRequest(
                LocalDate.of(2025, 7, 4),
                LocalTime.of(10, 0),
                2,
                "Test",
                List.of(101L)
        );
        assertThrows(BookingValidationException.class,
                () -> bookingService.create(req));
    }

    @Test
    void shouldRejectBookingOutsideWorkingHours() {
        BookingCreateRequest request = new BookingCreateRequest(
                LocalDate.of(2025, 7, 6),
                LocalTime.of(7, 0),
                2,
                "Test Customer",
                List.of(1L)
        );

        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> bookingService.create(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Booking must be within 08:00-22:00");

        verifyNoInteractions(cleanerRepo, bookingRepo);
    }

    @Test
    void shouldRejectBookingWithInvalidDuration() {
        BookingCreateRequest request = new BookingCreateRequest(
                LocalDate.of(2025, 7, 6),
                LocalTime.of(10, 0),
                3,
                "Duration Test",
                List.of(1L)
        );

        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> bookingService.create(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Duration must be 2 or 4 hours");

        verifyNoInteractions(cleanerRepo, bookingRepo);
    }

    @Test
    void shouldRejectBookingWithTooManyCleaners() {
        List<Long> cleanerIds = List.of(1L, 2L, 3L, 4L);

        BookingCreateRequest request = new BookingCreateRequest(
                LocalDate.of(2025, 7, 6),
                LocalTime.of(10, 0),
                2,
                "Too many cleaners",
                cleanerIds
        );

        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> bookingService.create(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Must assign 1-3 cleaners");

        verifyNoInteractions(cleanerRepo, bookingRepo);
    }

    @Test
    void shouldRejectBookingWhenCleanerHasOverlap() {
        BookingCreateRequest request = new BookingCreateRequest(
                LocalDate.of(2025, 7, 6),
                LocalTime.of(10, 0),
                2,
                "Cleaner overlap test",
                List.of(1L)
        );

        Vehicle van = Vehicle.builder().id(1L).label("Van-1").build();
        Cleaner cleaner = Cleaner.builder().id(1L).name("Cleaner1").vehicle(van).build();

        when(cleanerRepo.findAllById(List.of(1L))).thenReturn(List.of(cleaner));
        when(bookingRepo.hasOverlap(eq(cleaner), any(), any(), eq(null))).thenReturn(true);

        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> bookingService.create(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Cleaner 1 is busy or break too short");

        verify(cleanerRepo).findAllById(List.of(1L));
        verify(bookingRepo).hasOverlap(eq(cleaner), any(), any(), eq(null));
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingCreateRequest request = new BookingCreateRequest(
                LocalDate.of(2025, 7, 7),
                LocalTime.of(9, 0),
                2,
                "Happy Customer",
                List.of(1L)
        );

        Vehicle vehicle = Vehicle.builder().id(1L).label("Van-1").build();
        Cleaner cleaner = Cleaner.builder().id(1L).name("Cleaner1").vehicle(vehicle).build();

        when(cleanerRepo.findAllById(List.of(1L))).thenReturn(List.of(cleaner));
        when(bookingRepo.hasOverlap(eq(cleaner), any(), any(), eq(null))).thenReturn(false);

        Booking savedBooking = Booking.builder()
                .id(100L)
                .startTime(LocalDateTime.of(2025, 7, 7, 9, 0))
                .endTime(LocalDateTime.of(2025, 7, 7, 11, 0))
                .durationH(2)
                .customer("Happy Customer")
                .vehicle(vehicle)
                .cleaners(List.of(cleaner))
                .build();

        when(bookingRepo.save(any())).thenReturn(savedBooking);

        BookingResponse response = bookingService.create(request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.customer()).isEqualTo("Happy Customer");
        assertThat(response.durationH()).isEqualTo(2);
        assertThat(response.cleanerIds()).containsExactlyInAnyOrder(1L);
        assertThat(response.vehicleId()).isEqualTo(1L);

        verify(bookingRepo).save(any());
    }

    @Test
    void shouldDeleteBookingSuccessfully() {
        Long bookingId = 1L;

        bookingService.delete(bookingId);

        verify(bookingRepo).deleteById(bookingId);
    }

}