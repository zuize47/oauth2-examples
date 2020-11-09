package dev.sultanov.springboot.oauth2.mfa.repository.impl;

// import dev.sultanov.springboot.oauth2.mfa.model.Role;
import dev.sultanov.springboot.oauth2.mfa.model.User;
import dev.sultanov.springboot.oauth2.mfa.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class InmemoryUserRepository implements UserRepository {

    final Map<String, User> users = new LinkedHashMap<>();

    public InmemoryUserRepository() {
        users.put("john", new User(1,"john", "123456", false, 0, 1));
        users.put("anna", new User(1,"anna", "123456", false, 0, 1));
    }

    @Override
    public Optional<User> loadUserByUserLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

}