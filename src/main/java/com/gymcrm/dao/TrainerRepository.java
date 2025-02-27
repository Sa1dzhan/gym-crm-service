package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerRepository {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void create(Trainer trainer) {
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
        Map<String, Long> usr = storage.getTrainerUsernames();
        return storage.getTrainerUsernames().containsKey(username);
    }
}
