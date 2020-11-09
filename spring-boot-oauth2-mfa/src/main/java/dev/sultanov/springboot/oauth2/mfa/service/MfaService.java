package dev.sultanov.springboot.oauth2.mfa.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MfaService {

    private static final Map<String, String> SECRET_BY_USERNAME = Map.of("john", "JBSWY3DPEHPK3PXP");
    

    public boolean isEnabled(String username) {
        return SECRET_BY_USERNAME.containsKey(username);
    }

    public boolean verifyCode(String username, int code) {
        return SECRET_BY_USERNAME.containsKey(username) && code == 999999;
    }
}
