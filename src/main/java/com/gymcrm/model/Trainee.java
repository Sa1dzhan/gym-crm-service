package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "trainee")
@Getter
@Setter
@RequiredArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
@NamedQueries({
        @NamedQuery(
                name = "Trainee.findByUsername",
                query = "SELECT t FROM Trainee t WHERE t.username = :username"
        ),
        @NamedQuery(
                name = "Trainee.findAll",
                query = "SELECT t FROM Trainee t"
        ),
        @NamedQuery(
                name = "Trainee.findAllById",
                query = "SELECT t FROM Trainee t WHERE t.id IN :ids"
        ),
        @NamedQuery(
                name = "Trainee.existsByUsername",
                query = "SELECT t FROM Trainee t WHERE t.username = :username"
        )
})
public class Trainee extends User {

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @ManyToMany
    @JoinTable(name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Trainee{" +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                super.toString() +
                '}';
    }
}
