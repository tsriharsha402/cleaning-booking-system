package com.justlife.cleaning.cleaning_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cleaner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cleaner {

    @Id
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}