package com.gymcrm.model;

import java.time.LocalDate;

public class Trainee extends User {
    private String address;
    private LocalDate dateOfBirth;

    public Trainee() {
        super();
    }

    public Trainee(Long userId, String firstName, String lastName, String address, LocalDate dateOfBirth) {
        super(userId, firstName, lastName);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
