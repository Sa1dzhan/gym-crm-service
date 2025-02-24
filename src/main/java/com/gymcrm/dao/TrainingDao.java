package com.gymcrm.dao;

import com.gymcrm.model.Training;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingDao {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Training training) {
        storage.getTrainingStorage().put(training.getId(), training);
    }

    public Training read(Long id) {
        return storage.getTrainingStorage().get(id);
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.getTrainingStorage().values());
    }
}
