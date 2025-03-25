package com.gymcrm.dao.impl;

import com.gymcrm.dao.GeneralUserRepository;
import com.gymcrm.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class GeneralUserRepositoryImpl extends UserRepositoryImpl<User> implements GeneralUserRepository {
    public GeneralUserRepositoryImpl() {
        super(User.class);
    }
}
