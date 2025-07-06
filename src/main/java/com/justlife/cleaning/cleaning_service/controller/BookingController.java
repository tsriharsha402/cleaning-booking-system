package com.justlife.cleaning.cleaning_service.controller;

import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.service.AvailabilityService;
import com.justlife.cleaning.cleaning_service.service.BookingService;
import com.justlife.cleaning.cleaning_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final AvailabilityService availabilityService;

    // Daily availability (date & duration)
    @GetMapping("/availability")
    public List<AvailabilitySlot> getDailyAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationHours) {

        Map<Long, List<LocalTime>> transformed = availabilityService
                .getAvailableSlots(date, durationHours)
                .entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        e -> e.getKey().getId(),
                        Map.Entry::getValue
                ));

        return transformed.entrySet().stream()
                .map(e -> new AvailabilitySlot(e.getKey(), e.getValue()))
                .toList();
    }

    // Slot-specific availability (date + time + duration)
    @GetMapping("/availability/slot")
    public List<Long> getAvailableCleanersForSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam Integer durationHours) {

        return availabilityService
                .getAvailableCleanersForSlot(date, startTime, durationHours)
                .stream()
                .map(Cleaner::getId)
                .toList();
    }

    // Booking Endpoints

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestBody BookingCreateRequest req) {
        return bookingService.create(req);
    }

    @PatchMapping("/bookings/{id}")
    public BookingResponse updateBooking(@PathVariable Long id,
                                         @RequestBody BookingUpdateRequest req) {
        return bookingService.update(id, req);
    }

    @GetMapping("/bookings/{id}")
    public BookingResponse getBooking(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.delete(id);
    }

}