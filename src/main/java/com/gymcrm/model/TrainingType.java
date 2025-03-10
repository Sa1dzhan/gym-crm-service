package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "training_type")
@Getter
@Setter
@RequiredArgsConstructor
public class TrainingType {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name")
    private String trainingTypeName;
}
