package com.system.email.infra.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 *
 * The security configured to handle OAuth 2.0 authentication.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${api.google.clientId}")
    private String clientId;
    @Value("${api.google.clientSecret}")
    private String clientSecret;
    @Autowired
    private CorsFilter corsFilter;

    /**
     *
     * Security filter chain configuration.
     *
     * <p>Configured to handle OAuth 2 logins with default handling.<p/>
     *
     * @param http A HttpSecurity type object.
     * @return The security filter chain modified.
     *
     * @throws Exception If fails to configure the security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:4200/home", true))
            .logout(logout -> logout.logoutUrl("/logout").invalidateHttpSession(true));
        return http.build();
    }

    /**
     *
     * Configuration of the details of authentication, like scopes, URIs, redirection, etc.
     *
     * @return A new object of ClientRegistrationRepository, with the details.
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleClientRegistration = ClientRegistration
            .withRegistrationId("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope("openid", "profile", "email","https://www.googleapis.com/auth/gmail.readonly","https://www.googleapis.com/auth/gmail.send","https://www.googleapis.com/auth/gmail.modify")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth?access_type=offline&prompt=consent")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .userNameAttributeName("name")
            .redirectUri("{baseUrl}/login/oauth2/code/google")
            .clientName("Google")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .build();
        return new InMemoryClientRegistrationRepository(googleClientRegistration);
    }
}
