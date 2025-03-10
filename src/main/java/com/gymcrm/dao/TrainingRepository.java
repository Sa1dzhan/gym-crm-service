package com.gymcrm.dao;

import com.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("SELECT tr FROM Training tr "
            + "WHERE tr.trainee.user.username = :traineeUsername "
            + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
            + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
            + "AND (:trainerName IS NULL OR LOWER(tr.trainer.user.lastName) LIKE LOWER(CONCAT('%', :trainerName, '%')) "
            + "     OR LOWER(tr.trainer.user.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%'))) "
            + "AND (:trainingType IS NULL OR LOWER(tr.trainingType.trainingTypeName) = LOWER(:trainingType))")
    List<Training> findTrainingsForTrainee(@Param("traineeUsername") String traineeUsername,
                                           @Param("fromDate") Date fromDate,
                                           @Param("toDate") Date toDate,
                                           @Param("trainerName") String trainerName,
                                           @Param("trainingType") String trainingType);

    @Query("SELECT tr FROM Training tr "
            + "WHERE tr.trainer.user.username = :trainerUsername "
            + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
            + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
            + "AND (:traineeName IS NULL OR LOWER(tr.trainee.user.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%')) "
            + "     OR LOWER(tr.trainee.user.firstName) LIKE LOWER(CONCAT('%', :traineeName, '%')))")
    List<Training> findTrainingsForTrainer(@Param("trainerUsername") String trainerUsername,
                                           @Param("fromDate") Date fromDate,
                                           @Param("toDate") Date toDate,
                                           @Param("traineeName") String traineeName);
}
