package com.justlife.cleaning.cleaning_service.repository;

import com.justlife.cleaning.cleaning_service.domain.Booking;
import com.justlife.cleaning.cleaning_service.domain.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT CASE WHEN EXISTS (
            SELECT 1 FROM Booking b JOIN b.cleaners c
            WHERE c = :cleaner
                AND :end > b.startTime
                AND :start < b.endTime
                AND (:excludeId IS NULL OR b.id <> :excludeId)
        ) THEN true ELSE false END
    """)
    boolean hasOverlap(
            @Param("cleaner")    Cleaner        cleaner,
            @Param("start")      LocalDateTime  start,
            @Param("end")        LocalDateTime  end,
            @Param("excludeId")  Long           excludeId
    );
}