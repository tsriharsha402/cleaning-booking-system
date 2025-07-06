package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.domain.Booking;
import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.repository.BookingRepository;
import com.justlife.cleaning.cleaning_service.repository.CleanerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final CleanerRepository cleanerRepository;
    private final BookingRepository bookingRepository;

    private static final LocalTime START_OF_DAY = LocalTime.of(8, 0);
    private static final LocalTime END_OF_DAY = LocalTime.of(22, 0);
    private static final int BREAK_MINUTES = 30;

    public Map<Cleaner, List<LocalTime>> getAvailableSlots(LocalDate date, int durationHours) {
        List<Cleaner> allCleaners = cleanerRepository.findAll();
        Map<Cleaner, List<LocalTime>> result = new HashMap<>();

        for (Cleaner cleaner : allCleaners) {
            List<Booking> bookings = getBookingsForCleanerOnDate(cleaner, date);
            List<LocalTime> availableSlots = calculateAvailableSlots(bookings, durationHours, date);
            if (!availableSlots.isEmpty()) {
                result.put(cleaner, availableSlots);
            }
        }

        return result;
    }

    private List<Booking> getBookingsForCleanerOnDate(Cleaner cleaner, LocalDate date) {
        LocalDateTime from = date.atTime(START_OF_DAY);
        LocalDateTime to = date.atTime(END_OF_DAY);
        return bookingRepository.findByStartTimeBetween(from, to)
                .stream()
                .filter(b -> b.getCleaners().contains(cleaner))
                .toList();
    }

    private List<LocalTime> calculateAvailableSlots(List<Booking> bookings, int durationHours, LocalDate date) {
        List<Booking> bookingsCopy = new ArrayList<>(bookings);
        bookingsCopy.sort(Comparator.comparing(Booking::getStartTime));

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime cursor = START_OF_DAY;
        int durationMinutes = durationHours * 60;

        for (Booking booking : bookings) {
            LocalTime bookingStart = booking.getStartTime().toLocalTime();
            long minutesFree = java.time.Duration.between(cursor, bookingStart).toMinutes();

            if (minutesFree >= durationMinutes) {
                availableSlots.add(cursor);
            }
            cursor = booking.getEndTime().toLocalTime().plusMinutes(BREAK_MINUTES);
        }

        // Check slot after last booking
        if (cursor.plusMinutes(durationMinutes).isBefore(END_OF_DAY.plusSeconds(1))) {
            availableSlots.add(cursor);
        }

        return availableSlots;
    }

    public List<Cleaner> getAvailableCleanersForSlot(LocalDate date, LocalTime startTime, int durationHours) {
        LocalDateTime start = date.atTime(startTime);
        LocalDateTime end = start.plusHours(durationHours);
        List<Cleaner> allCleaners = cleanerRepository.findAll();

        List<Cleaner> available = new ArrayList<>();
        for (Cleaner cleaner : allCleaners) {
            boolean hasOverlap = bookingRepository.hasOverlap(
                    cleaner,
                    start,
                    end,
                    null
            );

            if (!hasOverlap) {
                available.add(cleaner);
            }
        }

        return available;
    }

}