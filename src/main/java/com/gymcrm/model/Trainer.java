package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "trainer")
@Getter
@Setter
@RequiredArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
@NamedQueries({
        @NamedQuery(
                name = "Trainer.findByUsername",
                query = "SELECT t FROM Trainer t WHERE t.username = :username"
        ),
        @NamedQuery(
                name = "Trainer.findAll",
                query = "SELECT t FROM Trainer t"
        ),
        @NamedQuery(
                name = "Trainer.findAllById",
                query = "SELECT t FROM Trainer t WHERE t.id IN :ids"
        ),
        @NamedQuery(
                name = "Trainer.existsByUsername",
                query = "SELECT t FROM Trainer t WHERE t.username = :username"
        ),
        @NamedQuery(
                name = "Trainer.findAllTrainersNotAssigned",
                query = "SELECT t FROM Trainer t\n" +
                        "        WHERE t NOT IN (\n" +
                        "            SELECT tr FROM Trainer tr\n" +
                        "            JOIN tr.trainees tt\n" +
                        "            WHERE tt.username = :traineeUsername\n" +
                        "        )"
        )
})
public class Trainer extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType specialization;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer")
    private Set<Training> trainings = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Trainer{" +
                ", specialization=" + specialization +
                super.toString() +
                '}';
    }
}
