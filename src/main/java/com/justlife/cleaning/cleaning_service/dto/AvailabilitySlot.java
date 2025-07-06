package com.justlife.cleaning.cleaning_service.dto;

import java.time.LocalTime;
import java.util.List;

public record AvailabilitySlot(
        Long cleanerId,
        List<LocalTime> freeStartTimes
) {}
