package dev.sultanov.springboot.oauth2.mfa.config.granter;

import dev.sultanov.springboot.oauth2.mfa.exception.MfaRequiredException;
import dev.sultanov.springboot.oauth2.mfa.model.MfaCode;
import dev.sultanov.springboot.oauth2.mfa.service.MfaService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "password";
    private static final GrantedAuthority PRE_AUTH = new SimpleGrantedAuthority("PRE_AUTH");
    private final ClientDetailsService clientDetailsService;
    private final AuthenticationManager authenticationManager;
    private final MfaService mfaService;

    public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
                                AuthenticationManager authenticationManager, MfaService mfaService) {
        super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
        this.clientDetailsService = endpointsConfigurer.getClientDetailsService();
        this.authenticationManager = authenticationManager;
        this.mfaService = mfaService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String loginByPin = parameters.get("by_pin");
        String password = parameters.get("password");
        parameters.remove("password");
        if("by_pin".equals(loginByPin) && mfaService.isEnabled(username)){
            parameters.remove("by_pin");
            return loginWithPinAndThrowException(username, parameters, client, tokenRequest);
        }else {
            return loginWithPassword(username, password, parameters, client, tokenRequest);
        }
    }

    private OAuth2Authentication loginWithPinAndThrowException(String username, Map<String, String> parameters, ClientDetails client, TokenRequest tokenRequest){
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(PRE_AUTH));
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
        var mfaCode = new MfaCode(java.util.UUID.randomUUID().toString(), 999999 , new OAuth2Authentication(storedOAuth2Request, userAuth));
        mfaService.storeMfa(mfaCode);
        throw new MfaRequiredException(mfaCode.getMfaCode());
    }

    private OAuth2Authentication loginWithPassword(String username, String password, Map<String, String> parameters,
                                   ClientDetails client, TokenRequest tokenRequest){
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);

        try {
            userAuth = this.authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException e) {
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }
        OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
