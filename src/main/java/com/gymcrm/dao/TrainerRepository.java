package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    boolean existsByUserUsername(String username);

    Optional<Trainer> findByUserUsername(String username);
}
