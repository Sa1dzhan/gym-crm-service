package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TraineeDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Trainee trainee) {
        storage.getTraineeStorage().put(trainee.getId(), trainee);
    }

    public Trainee read(Long id) {
        return storage.getTraineeStorage().get(id);
    }

    public void update(Trainee trainee) {
        storage.getTraineeStorage().put(trainee.getId(), trainee);
    }

    public void delete(Long id) {
        storage.getTraineeStorage().remove(id);
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.getTraineeStorage().values());
    }
}
