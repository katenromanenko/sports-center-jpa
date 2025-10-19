package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Employee extends Client {

    @Column(name = "hired_date")
    private LocalDate hiredDate;

    @Column(name = "fired_date")
    private LocalDate firedDate;

    @Column(name = "position", length = 80)
    private String position;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;
}
