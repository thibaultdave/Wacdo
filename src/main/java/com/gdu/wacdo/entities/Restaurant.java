package com.gdu.wacdo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    @Size(min = 5, max = 5)
    private String postalCode;
    @NotBlank
    private String city;
    @OneToMany(mappedBy = "restaurant")
    private List<Assignment> assignments = new ArrayList<>();
}