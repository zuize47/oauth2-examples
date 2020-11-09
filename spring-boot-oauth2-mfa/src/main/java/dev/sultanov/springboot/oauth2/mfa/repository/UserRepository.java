package dev.sultanov.springboot.oauth2.mfa.repository;

import dev.sultanov.springboot.oauth2.mfa.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> loadUserByUserLogin(String login);
}