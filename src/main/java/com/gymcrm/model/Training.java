package com.gymcrm.model;

import java.time.LocalDate;

public class Training {
    private Long id;
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType = new TrainingType();
    private LocalDate trainingDate;
    private int trainingDuration;

    public Training(){
    }

    public Training(Long id, Long trainerId, Long traineeId, String trainingName,
                    String trainingTypeName, LocalDate trainingDate, int trainingDuration) {
        this.id = id;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingName = trainingName;
        this.trainingType = new TrainingType(trainingTypeName);
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingType() {
        return trainingType.getName();
    }

    public void setTrainingType(String trainingTypeName) {
        this.trainingType.setName(trainingTypeName);
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
