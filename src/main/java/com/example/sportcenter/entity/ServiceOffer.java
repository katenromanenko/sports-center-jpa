package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "service_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceOffer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;
}

