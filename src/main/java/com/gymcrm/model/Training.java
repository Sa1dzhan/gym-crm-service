package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "training")
@Getter
@Setter
@RequiredArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Training.findAll",
                query = "SELECT t FROM Training t"
        ),
        @NamedQuery(
                name = "Training.findTrainingsForTrainee",
                query = "SELECT tr FROM Training tr "
                        + "WHERE tr.trainee.username = :traineeUsername "
                        + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
                        + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
                        + "AND (:trainerName IS NULL OR (LOWER(tr.trainer.lastName) LIKE LOWER(CONCAT('%', :trainerName, '%')) "
                        + "     OR LOWER(tr.trainer.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%')))) "
                        + "AND (:trainingType IS NULL OR LOWER(tr.trainingType.trainingTypeName) = LOWER(:trainingType))"
        ),
        @NamedQuery(
                name = "Training.findTrainingsForTrainer",
                query = "SELECT tr FROM Training tr "
                        + "WHERE tr.trainer.username = :trainerUsername "
                        + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
                        + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
                        + "AND (:traineeName IS NULL OR (LOWER(tr.trainee.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%')) "
                        + "     OR LOWER(tr.trainee.firstName) LIKE LOWER(CONCAT('%', :traineeName, '%'))))"
        )
})
public class Training {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Long trainingDuration;
}
