package com.gymcrm.dao.impl;

import com.gymcrm.dao.UserRepository;
import com.gymcrm.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class UserRepositoryImpl<T extends User> implements UserRepository<T> {
    @PersistenceContext
    private EntityManager em;

    private final Class<T> entityClass;

    @Override
    public Optional<T> findById(Long id) {
        T entity = em.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<T> findByUsername(String username) {
        String queryName = entityClass.getSimpleName() + ".findByUsername";
        TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
        query.setParameter("username", username);
        try {
            T result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        String queryName = entityClass.getSimpleName() + ".findAll";
        TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
        return query.getResultList();
    }

    @Override
    public List<T> findAllByUsername(List<String> usernameList) {
        String queryName = entityClass.getSimpleName() + ".findAllByUsername";
        TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
        query.setParameter("usernames", usernameList);
        return query.getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        String queryName = entityClass.getSimpleName() + ".existsByUsername";
        TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
        query.setParameter("username", username);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            em.flush();
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Override
    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
