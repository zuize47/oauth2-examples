package dev.sultanov.springboot.oauth2.mfa.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dev.sultanov.springboot.oauth2.mfa.model.MfaCode;

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

    @Cacheable(value = "mfa_token", key = "#mfaCode")
    public MfaCode getMfa(String mfaCode){
        return null;
    }

    @CachePut(cacheNames = "mfa_token", key = "#mfa.mfaCode")
    public MfaCode storeMfa(MfaCode mfa){
        return mfa;
    }
}
