package dev.sultanov.springboot.oauth2.mfa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
@SpringBootApplication
public class SpringBootOauth2MfaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauth2MfaApplication.class, args);
    }
}
