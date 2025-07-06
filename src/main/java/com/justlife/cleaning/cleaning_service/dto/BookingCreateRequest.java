package com.justlife.cleaning.cleaning_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingCreateRequest(
        LocalDate    date,
        LocalTime    startTime,
        Integer      durationHours,
        String       customer,
        List<Long>   cleanerIds
) {}