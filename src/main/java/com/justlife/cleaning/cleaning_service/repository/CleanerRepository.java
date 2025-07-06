package com.justlife.cleaning.cleaning_service.repository;

import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import com.justlife.cleaning.cleaning_service.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {

    @Query("""
        SELECT c FROM Cleaner c
        WHERE NOT EXISTS (
            SELECT b FROM Booking b
            WHERE c MEMBER OF b.cleaners
            AND b.startTime < :endTime
            AND b.endTime > :startTime
        )
        """)
    List<Cleaner> findAvailableCleaners(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

}
