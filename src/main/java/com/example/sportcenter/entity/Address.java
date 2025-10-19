package com.example.sportcenter.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String city;
    private String street;
    private String houseNumber;
    private String postalCode;
}

