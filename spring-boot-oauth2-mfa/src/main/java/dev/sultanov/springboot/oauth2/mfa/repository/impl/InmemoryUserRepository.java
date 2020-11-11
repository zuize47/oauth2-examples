package dev.sultanov.springboot.oauth2.mfa.repository.impl;

// import dev.sultanov.springboot.oauth2.mfa.model.Role;
import dev.sultanov.springboot.oauth2.mfa.model.User;
import dev.sultanov.springboot.oauth2.mfa.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class InmemoryUserRepository implements UserRepository {

    private final PasswordEncoder passwordEncoder;


    final Map<String, User> users = new LinkedHashMap<>();

    public InmemoryUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        users.put("john", new User(1,"john", this.passwordEncoder.encode("pass"), false, 0, 1));
        users.put("anna", new User(1,"anna", this.passwordEncoder.encode("pass"), false, 0, 1));
    }

    @Override
    public Optional<User> loadUserByUserLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

}