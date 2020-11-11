package dev.sultanov.springboot.oauth2.mfa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

@Configuration
public class AuthResourceConfig  extends ResourceServerConfigurerAdapter {

    private final WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator;

    public AuthResourceConfig(@Qualifier("webResponseExceptionTranslator") WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator) {
        this.webResponseExceptionTranslator = webResponseExceptionTranslator;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(webResponseExceptionTranslator);
        resources.authenticationEntryPoint(authenticationEntryPoint);
    }
}
