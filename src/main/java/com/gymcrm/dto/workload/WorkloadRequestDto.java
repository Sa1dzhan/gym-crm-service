package com.gymcrm.dto.workload;

import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.util.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class WorkloadRequestDto {
    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Boolean isActive;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @NotNull
    private Long trainingDuration;

    @NotNull
    private ActionType actionType;

    public WorkloadRequestDto(Training training, ActionType action) {
        Trainer trainer = training.getTrainer();

        setUsername(trainer.getUsername());
        setFirstName(trainer.getFirstName());
        setLastName(trainer.getLastName());
        setIsActive(trainer.getIsActive());
        setTrainingDate(training.getTrainingDate());
        setTrainingDuration(training.getTrainingDuration());
        setActionType(action);
    }
}
