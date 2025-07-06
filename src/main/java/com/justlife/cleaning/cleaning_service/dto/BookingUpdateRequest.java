package com.justlife.cleaning.cleaning_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingUpdateRequest(
        LocalDate  newDate,
        LocalTime  newStartTime,
        Integer    newDurationHours,
        List<Long> newCleanerIds
) {}