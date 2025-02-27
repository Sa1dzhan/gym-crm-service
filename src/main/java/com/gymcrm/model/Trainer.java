package com.gymcrm.model;

public class Trainer extends User {
    private TrainingType specialization;

    public Trainer(){}

    public Trainer(Long userId, String firstName, String lastName, TrainingType specialization) {
        super(userId, firstName, lastName);
        this.specialization = specialization;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }
}
