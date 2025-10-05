package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "facilities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Facility {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable=false, length=100)
    private String name;

    @Column(name="identity_number", nullable=false, length=50, unique=true)
    private String identityNumber;

    @Column(name="max_capacity", nullable=false)
    private Integer maxCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=16)
    private FacilityStatus status;

    @Column(name="hour_rate", nullable=false, precision=12, scale=2)
    private BigDecimal hourRate;
}
