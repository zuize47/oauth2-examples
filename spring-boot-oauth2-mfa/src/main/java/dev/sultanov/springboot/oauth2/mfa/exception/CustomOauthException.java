package dev.sultanov.springboot.oauth2.mfa.exception;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.Map;
import java.util.TreeMap;

@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {

    private static final long serialVersionUID = 9034375894294453734L;
    
    private static final Map<String, Integer> errorCodeNumber = new TreeMap<>();
    static {
        errorCodeNumber.put(INVALID_REQUEST, 110);
        errorCodeNumber.put(INVALID_CLIENT, 111);
        errorCodeNumber.put(INVALID_GRANT, 112);
        errorCodeNumber.put(UNAUTHORIZED_CLIENT, 113);
        errorCodeNumber.put(UNSUPPORTED_GRANT_TYPE, 114);
        errorCodeNumber.put(INVALID_SCOPE, 115);
        errorCodeNumber.put(INSUFFICIENT_SCOPE, 116);
        errorCodeNumber.put(INVALID_TOKEN, 117);
        errorCodeNumber.put(ACCESS_DENIED, 118);
    }
    final int errorId;
    final String errorMsg;
    public CustomOauthException(OAuth2Exception ex) {
        super(ex.getMessage());
        if(errorCodeNumber.containsKey(ex.getOAuth2ErrorCode())){
            errorId = errorCodeNumber.get(ex.getOAuth2ErrorCode());
            errorMsg = ex.getOAuth2ErrorCode();
        }else {
            errorId = 999;
            errorMsg = ex.getOAuth2ErrorCode();
        }
    }

    public int getErrorId() {
        return errorId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}