package com.example.sportcenter.entity.view;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;

@Entity
@Immutable
@Subselect("""
    select
        f.id               as id,
        f.name             as name,
        f.identity_number  as identity_number,
        f.max_capacity     as max_capacity,
        f.hour_rate        as hour_rate
    from facilities f
    where f.max_capacity <= 15
""")
@Synchronize({"facilities"})
@Getter
@NoArgsConstructor
@ToString
public class FacilitiesUnder15 {

    @Id
    private Long id;

    private String name;

    @Column(name = "identity_number")
    private String identityNumber;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "hour_rate")
    private BigDecimal hourRate;
}

