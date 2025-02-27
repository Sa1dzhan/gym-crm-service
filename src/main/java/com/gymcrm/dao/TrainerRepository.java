package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class TrainerRepository {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Trainer trainer) {
        trainer.setId(Collections.max(storage.getTrainerStorage().keySet()) + 1);
        storage.getTrainerStorage().put(trainer.getId(), trainer);
        storage.getTrainerUsernames().put(trainer.getUsername(), trainer.getId());
    }

    public Trainer read(Long id) {
        return storage.getTrainerStorage().get(id);
    }

    public void update(Trainer trainer) {
        storage.getTrainerStorage().put(trainer.getId(), trainer);
        storage.getTrainerUsernames().put(trainer.getUsername(), trainer.getId());
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(storage.getTrainerStorage().values());
    }

    public boolean existsByUsername(String username) {
        return storage.getTrainerUsernames().containsKey(username);
    }
}
