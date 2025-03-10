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
public class Trainee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

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
                "id=" + id +
                ", user=" + user +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
