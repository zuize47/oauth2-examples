package dev.sultanov.springboot.oauth2.mfa.config;

import dev.sultanov.springboot.oauth2.mfa.config.granter.MfaTokenGranter;
import dev.sultanov.springboot.oauth2.mfa.config.granter.PasswordTokenGranter;
import dev.sultanov.springboot.oauth2.mfa.exception.CustomOauthException;
import dev.sultanov.springboot.oauth2.mfa.service.MfaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private MfaService mfaService;

    @Autowired
    public AuthServerConfig(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                            MfaService mfaService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.mfaService = mfaService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
            .accessTokenConverter(accessTokenConverter())
            .tokenGranter(tokenGranter(endpoints))
            ;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "mfa")
                .scopes("read");
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<>(List.of(endpoints.getTokenGranter()));
        granters.add(new PasswordTokenGranter(endpoints, authenticationManager, mfaService));
        granters.add(new MfaTokenGranter(endpoints, authenticationManager, mfaService));
        return new CompositeTokenGranter(granters);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        return converter;
    }

    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator(){
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                if (e instanceof OAuth2Exception || e.getCause() instanceof OAuth2Exception) {
                    OAuth2Exception oAuth2Exception = (e instanceof OAuth2Exception) ? (OAuth2Exception)e : (OAuth2Exception)e.getCause();
                    return ResponseEntity
                            .status(oAuth2Exception.getHttpErrorCode())
                            .body(new CustomOauthException(oAuth2Exception));
                }
                else {
                    return super.translate(e);
                }
            }
        };
    }
}
