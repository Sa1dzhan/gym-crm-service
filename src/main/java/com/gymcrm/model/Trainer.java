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
public class Trainer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id")
    private TrainingType specialization;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer")
    private Set<Training> trainings = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", user=" + user +
                ", specialization=" + specialization +
                '}';
    }
}
