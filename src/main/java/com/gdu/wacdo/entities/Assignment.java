package com.gdu.wacdo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Collaborator collaborator;
    @ManyToOne
    private Restaurant restaurant;
    @ManyToOne
    private Job job;
    @PastOrPresent
    private LocalDate startDate;
    @PastOrPresent
    private LocalDate endDate;
}