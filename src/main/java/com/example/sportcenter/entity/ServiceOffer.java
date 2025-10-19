package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "serviceOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Facility> facilities = new ArrayList<>();
}

