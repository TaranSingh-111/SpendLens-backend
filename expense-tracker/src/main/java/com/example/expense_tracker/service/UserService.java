package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.UserAlreadyExistsException;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new UserAlreadyExistsException(
                    "An account with this email already exists"
            );
        }
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
