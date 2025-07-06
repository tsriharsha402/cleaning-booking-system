package com.justlife.cleaning.cleaning_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long                 id,
        LocalDateTime        startTime,
        LocalDateTime        endTime,
        Integer              durationH,
        String               customer,
        Long                 vehicleId,
        List<Long>           cleanerIds
) {}
