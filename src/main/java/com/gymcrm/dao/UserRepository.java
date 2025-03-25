package com.gymcrm.dao;

import com.gymcrm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T extends User> {
    Optional<T> findById(Long id);

    Optional<T> findByUsername(String username);

    List<T> findAll();

    List<T> findAllByUsername(List<String> usernameList);

    boolean existsByUsername(String username);

    T save(T entity);

    void delete(T entity);
}
