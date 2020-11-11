package dev.sultanov.springboot.oauth2.mfa.config.granter;

import dev.sultanov.springboot.oauth2.mfa.model.MfaCode;
import dev.sultanov.springboot.oauth2.mfa.service.MfaService;
import dev.sultanov.springboot.oauth2.mfa.service.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MfaTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "mfa";

    private final ClientDetailsService clientDetailsService;
    private final MfaService mfaService;
    private final UserDetailsService userDetailsService;

    public MfaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
                           MfaService mfaService, UserDetailsService userDetailsService) {
        super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(), endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
        this.clientDetailsService = endpointsConfigurer.getClientDetailsService();
        this.mfaService = mfaService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        final String mfaToken = parameters.get("mfa_token");
        if (mfaToken != null) {
            MfaCode mfaCode = loadAuthentication(mfaToken);
            OAuth2Authentication authentication = mfaCode.getOAuth2Authentication();
            final String username = authentication.getName();
            if (parameters.containsKey("mfa_code")) {
                int code = parseCode(parameters.get("mfa_code"));
                if (mfaService.verifyCode(username, code, mfaCode.getMfaPin())) {
                    mfaService.removeMfa(mfaCode.getMfaCode());
                    return getAuthentication(tokenRequest, authentication);
                }
            } else {
                throw new InvalidRequestException("Missing MFA code");
            }
            throw new InvalidGrantException("Invalid MFA code");
        } else {
            throw new InvalidRequestException("Missing MFA token");
        }
    }

    private MfaCode loadAuthentication(String accessTokenValue) {
        var mfa = this.mfaService.getMfa(accessTokenValue);
        if (mfa == null) {
            throw new InvalidTokenException("Invalid access token or expired: " + accessTokenValue + "");
        } 

        OAuth2Authentication result = mfa.getOAuth2Authentication();
        if (result == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        return mfa;
        
    }

    private int parseCode(String codeString) {
        try {
            return Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            throw new InvalidGrantException("Invalid MFA code");
        }
    }

    private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        Authentication user = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), null,
               userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object details = authentication.getDetails();
        authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
        authentication.setDetails(details);

        String clientId = authentication.getOAuth2Request().getClientId();
        if (clientId != null && clientId.equals(tokenRequest.getClientId())) {
            if (this.clientDetailsService != null) {
                try {
                    this.clientDetailsService.loadClientByClientId(clientId);
                } catch (ClientRegistrationException e) {
                    throw new InvalidTokenException("Client not valid: " + clientId, e);
                }
            }
            return refreshAuthentication(authentication, tokenRequest);
        } else {
            throw new InvalidGrantException("Client is missing or does not correspond to the MFA token");
        }
    }

    private OAuth2Authentication refreshAuthentication(OAuth2Authentication authentication, TokenRequest request) {
        Set<String> scope = request.getScope();
        OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
        if (scope != null && !scope.isEmpty()) {
            Set<String> originalScope = clientAuth.getScope();
            if (originalScope == null || !originalScope.containsAll(scope)) {
                throw new InvalidScopeException("Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
            }

            clientAuth = clientAuth.narrowScope(scope);
        }
        return new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
    }
}
