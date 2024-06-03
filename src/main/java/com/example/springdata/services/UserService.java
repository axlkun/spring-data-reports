package com.example.springdata.services;

import com.example.springdata.models.Rol;
import com.example.springdata.models.User;
import com.example.springdata.repositories.RolRepository;
import com.example.springdata.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserService{

    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RolRepository rolRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
    }

    public void save(User user) {

        logger.info("Saving user with email: " + user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Rol userRole = rolRepository.findByNombre("ROLE_USER");
        if (userRole == null) {
            userRole = new Rol();
            userRole.setNombre("ROLE_USER");
            rolRepository.save(userRole);
        }
        user.getRoles().add(userRole);

        userRepository.save(user);
        logger.info("User saved successfully with email: " + user.getEmail());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
