package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class TraineeRepository {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainee create(Trainee trainee) {
        trainee.setId(Collections.max(storage.getTraineeStorage().keySet()) + 1);
        storage.getTraineeStorage().put(trainee.getId(), trainee);
        storage.getTraineeUsernames().put(trainee.getUsername(), trainee.getId());

        return trainee;
    }

    public Trainee read(Long id) {
        return storage.getTraineeStorage().get(id);
    }

    public Trainee update(Trainee trainee) {
        storage.getTraineeStorage().put(trainee.getId(), trainee);
        storage.getTraineeUsernames().put(trainee.getUsername(), trainee.getId());

        return trainee;
    }

    public void delete(Long id) {
        storage.getTraineeStorage().remove(id);
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.getTraineeStorage().values());
    }

    public boolean existsByUsername(String username) {
        return storage.getTraineeUsernames().containsKey(username);
    }
}
