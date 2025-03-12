package com.gymcrm.dao.impl;

import com.gymcrm.dao.UserRepository;
import com.gymcrm.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryImpl<T extends User> implements UserRepository<T> {
    @PersistenceContext
    private EntityManager em;

    private final Class<T> entityClass;

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {
        T entity = em.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findByUsername(String username) {
        String jpql = "SELECT u FROM " + entityClass.getSimpleName() + " u WHERE u.username = :username";
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        query.setParameter("username", username);
        try {
            T result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        String jpql = "SELECT t FROM " + entityClass.getSimpleName() + " t";
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllById(List<Long> idList) {
        String jpql = "SELECT u FROM " + entityClass.getSimpleName() + " u WHERE u.id IN :ids";
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        query.setParameter("ids", idList);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        String jpql = "SELECT COUNT(u) FROM " + entityClass.getSimpleName() + " u WHERE u.username = :username";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("username", username);
        Long count = query.getSingleResult();
        return count > 0;
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
