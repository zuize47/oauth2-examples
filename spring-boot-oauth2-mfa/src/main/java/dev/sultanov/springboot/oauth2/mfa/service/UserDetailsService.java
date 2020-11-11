package dev.sultanov.springboot.oauth2.mfa.service;

import dev.sultanov.springboot.oauth2.mfa.model.User;
import dev.sultanov.springboot.oauth2.mfa.model.CustomUser;
import dev.sultanov.springboot.oauth2.mfa.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Authenticate a user from the database.
 * @author hoang
 */
@Service("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String login) {
        logger.debug("Authenticating user '{}'", login);

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository.loadUserByUserLogin(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));

    }

    private CustomUser createSpringSecurityUser(String lowercaseLogin, User user) {
        //User userFull = userRepository.getUserInfo(user);

        final List<GrantedAuthority> authorityList = new ArrayList<>();
        // if(user.getRole() != null){
        //     authorityList.add(new SimpleGrantedAuthority(userFull.getRole().name()));
        // }
        return new CustomUser(user.getId(), user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                authorityList);
    }
}