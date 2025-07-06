package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.domain.*;
import com.justlife.cleaning.cleaning_service.exception.BookingValidationException;
import com.justlife.cleaning.cleaning_service.repository.*;
import com.justlife.cleaning.cleaning_service.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository  bookingRepo;
    private final CleanerRepository  cleanerRepo;

    private static final LocalTime START_OF_DAY = LocalTime.of(8, 0);
    private static final LocalTime END_OF_DAY   = LocalTime.of(22, 0);
    private static final List<Integer> ALLOWED_DURATIONS = List.of(2, 4);

    // create
    @Transactional
    public BookingResponse create(BookingCreateRequest req) {
        validateBusinessRules(req.date(), req.startTime(), req.durationHours(), req.cleanerIds());

        List<Cleaner> cleaners   = cleanerRepo.findAllById(req.cleanerIds());
        Vehicle       vehicle    = cleaners.get(0).getVehicle();            // same vehicle validated
        LocalDateTime start      = req.date().atTime(req.startTime());
        LocalDateTime end        = start.plusHours(req.durationHours());

        Booking booking = Booking.builder()
                .startTime(start)
                .endTime(end)
                .durationH(req.durationHours())
                .customer(req.customer())
                .vehicle(vehicle)
                .cleaners(cleaners)
                .build();

        Booking saved = bookingRepo.save(booking);
        return toDto(saved);
    }

    // update
    @Transactional
    public BookingResponse update(Long bookingId, BookingUpdateRequest req) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingValidationException("Booking not found"));

        // if any field is null, keep existing value
        LocalDate   date         = Optional.ofNullable(req.newDate()).orElse(booking.getStartTime().toLocalDate());
        LocalTime   startTime    = Optional.ofNullable(req.newStartTime()).orElse(booking.getStartTime().toLocalTime());
        Integer     durationH    = Optional.ofNullable(req.newDurationHours()).orElse(booking.getDurationH());
        List<Long>  cleanerIds   = Optional.ofNullable(req.newCleanerIds())
                .orElse(booking.getCleaners().stream().map(Cleaner::getId).toList());

        validateBusinessRules(date, startTime, durationH, cleanerIds, bookingId);

        List<Cleaner> cleaners = cleanerRepo.findAllById(cleanerIds);
        Vehicle vehicle        = cleaners.get(0).getVehicle();
        LocalDateTime start    = date.atTime(startTime);
        LocalDateTime end      = start.plusHours(durationH);

        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationH(durationH);
        booking.setVehicle(vehicle);
        booking.setCleaners(cleaners);

        Booking saved = bookingRepo.save(booking);
        return toDto(saved);
    }

    // validation
    private void validateBusinessRules(LocalDate date,
                                       LocalTime startTime,
                                       int durationH,
                                       List<Long> cleanerIds) {
        validateBusinessRules(date, startTime, durationH, cleanerIds, null);
    }

    private void validateBusinessRules(LocalDate date,
                                       LocalTime startTime,
                                       int durationH,
                                       List<Long> cleanerIds,
                                       Long bookingIdToExclude) {

        // Day off rule
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            throw new BookingValidationException("No bookings allowed on Fridays");
        }

        // Working-hours rule
        LocalDateTime start = date.atTime(startTime);
        LocalDateTime end   = start.plusHours(durationH);
        if (startTime.isBefore(START_OF_DAY) || end.toLocalTime().isAfter(END_OF_DAY)) {
            throw new BookingValidationException("Booking must be within 08:00-22:00");
        }

        // Duration
        if (!ALLOWED_DURATIONS.contains(durationH)) {
            throw new BookingValidationException("Duration must be 2 or 4 hours");
        }

        // Cleaner count
        if (cleanerIds.isEmpty() || cleanerIds.size() > 3) {
            throw new BookingValidationException("Must assign 1-3 cleaners");
        }

        // Cleaners exist & same vehicle
        List<Cleaner> cleaners = cleanerRepo.findAllById(cleanerIds);
        if (cleaners.size() != cleanerIds.size()) {
            throw new BookingValidationException("One or more cleaners not found");
        }
        long vehicleId = cleaners.get(0).getVehicle().getId();
        boolean sameVehicle = cleaners.stream()
                .allMatch(c -> c.getVehicle().getId().equals(vehicleId));
        if (!sameVehicle) {
            throw new BookingValidationException("All cleaners must belong to the same vehicle");
        }

        // Availability & 30-min break rule
        for (Cleaner cleaner : cleaners) {
            boolean overlap = bookingRepo.hasOverlap(
                    cleaner,
                    start.minusMinutes(30),
                    end,
                    bookingIdToExclude
            );

            if (overlap) {
                throw new BookingValidationException("Cleaner " + cleaner.getId() + " is busy or break too short");
            }
        }
    }

    // Mapper
    private BookingResponse toDto(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getStartTime(),
                b.getEndTime(),
                b.getDurationH(),
                b.getCustomer(),
                b.getVehicle().getId(),
                b.getCleaners().stream().map(Cleaner::getId).toList()
        );
    }

    public BookingResponse get(Long id) { return bookingRepo.findById(id).map(this::toDto)
            .orElseThrow(() -> new BookingValidationException("Booking not found")); }

    public void delete(Long id) { bookingRepo.deleteById(id); }

}