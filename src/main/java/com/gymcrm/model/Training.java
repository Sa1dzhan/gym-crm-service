package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "training")
@Getter
@Setter
@RequiredArgsConstructor
public class Training {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @Column(name = "training_name")
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;

    @Column(name = "training_date")
    private Date trainingDate;

    @Column(name = "training_duration")
    private Long trainingDuration;
}
