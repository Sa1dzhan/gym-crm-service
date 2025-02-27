package com.gymcrm.model;

public class TrainingType {
    private String name;

    public TrainingType(){}

    public TrainingType(String trainingName){
        this.name = trainingName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
