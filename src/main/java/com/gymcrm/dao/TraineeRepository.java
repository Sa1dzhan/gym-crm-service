package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    boolean existsByUserUsername(String username);

    Optional<Trainee> findByUserUsername(String username);
}
