package com.justlife.cleaning.cleaning_service.controller;

import com.justlife.cleaning.cleaning_service.dto.CleanerResponse;
import com.justlife.cleaning.cleaning_service.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cleaners")
@RequiredArgsConstructor
public class CleanerController {

    private final CleanerService cleanerService;

    @GetMapping("/available")
    public List<CleanerResponse> getAvailableCleaners(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {

        return cleanerService.findAvailableCleaners(startTime, endTime);
    }

}