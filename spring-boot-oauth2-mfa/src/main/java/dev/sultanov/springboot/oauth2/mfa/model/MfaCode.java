package dev.sultanov.springboot.oauth2.mfa.model;

import java.io.Serializable;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class MfaCode implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String mfaCode;
    private final String mfaPin;

    private final OAuth2Authentication oAuth2Authentication;

    public MfaCode(String mfaCode, String mfaPin, OAuth2Authentication oAuth2Authentication){
        this.mfaCode = mfaCode;
        this.mfaPin = mfaPin;
        this.oAuth2Authentication = oAuth2Authentication;
    }

    public String getMfaCode(){
        return this.mfaCode;
    }
    public String getMfaPin(){
        return this.mfaPin;
    }
    public OAuth2Authentication getOAuth2Authentication(){
        return this.oAuth2Authentication;
    }
}
