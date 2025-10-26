package com.example.sportcenter.entity.view;

import com.example.sportcenter.entity.Address;
import com.example.sportcenter.entity.ClientStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "clients")
@Where(clause = "status = 'PREMIUM'")
@Getter @Setter
@NoArgsConstructor
public class PremiumClient {
    @Id
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    private Integer age;

    @Column(length = 32)
    private String phone;

    @Column(name = "last_visit")
    private LocalDate lastVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ClientStatus status;

    @Column(name = "total_spent", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSpent;

    @Embedded
    private Address address;
}

