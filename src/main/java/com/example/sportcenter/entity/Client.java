package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",        column = @Column(name = "addr_city",        length = 80)),
            @AttributeOverride(name = "street",      column = @Column(name = "addr_street",      length = 120)),
            @AttributeOverride(name = "houseNumber", column = @Column(name = "addr_house_no",    length = 16)),
            @AttributeOverride(name = "postalCode",  column = @Column(name = "addr_postal_code", length = 12))
    })
    private Address address;
}

