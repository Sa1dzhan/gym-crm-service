package com.gymcrm.service.impl;

import com.gymcrm.dao.GeneralUserRepository;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.User;
import com.gymcrm.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final GeneralUserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (trainerRepository.existsByUsername(user.getUsername())) {
            authList.add(new SimpleGrantedAuthority("ROLE_TRAINER"));
        } else if (traineeRepository.existsByUsername(user.getUsername())) {
            authList.add(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authList
        );
    }
}
