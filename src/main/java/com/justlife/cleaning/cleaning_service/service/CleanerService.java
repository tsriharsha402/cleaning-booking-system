package com.justlife.cleaning.cleaning_service.service;

import com.justlife.cleaning.cleaning_service.dto.CleanerResponse;
import com.justlife.cleaning.cleaning_service.repository.CleanerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {

    private final CleanerRepository cleanerRepository;

    public List<CleanerResponse> findAvailableCleaners(LocalDateTime startTime, LocalDateTime endTime) {
        return cleanerRepository.findAvailableCleaners(startTime, endTime)
                .stream()
                .map(c -> new CleanerResponse(c.getId(), c.getName(), c.getVehicle().getLabel()))
                .toList();
    }
}