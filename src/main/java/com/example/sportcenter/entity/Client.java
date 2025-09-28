package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "phone", nullable = false, length = 32, unique = true)
    private String phone;

    @Column(name = "last_visit")
    private LocalDate lastVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ClientStatus status;

    @Column(name = "total_spent", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSpent;
}
