// src/main/java/com/example/sportcenter/entity/Visit.java
package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "amount_spent", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountSpent;

    @ManyToOne
    @JoinColumn(name = "visitor_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Visitor visitor;
}

