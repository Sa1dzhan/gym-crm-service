package com.gymcrm.model;

public class Trainer extends User {
    private String specialization;

    public Trainer(){}

    public Trainer(Long userId, String firstName, String lastName, String specialization) {
        super(userId, firstName, lastName);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
