package dev.sultanov.springboot.oauth2.mfa.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/user")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/me")
    public Principal getUser(Principal principal) {
        logger.debug("geMe", principal);
        return principal;
    }
}