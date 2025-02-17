package com.system.email.infra.config.gmail;

import com.system.email.application.gateway.gmail.GmailGateway;
import com.system.email.application.usecases.gmail.GmailUseCase;
import com.system.email.infra.gateway.gmail.GmailRepoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * The gmail config.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 *
 */
@Configuration
public class GmailConfig {

    @Bean
    public GmailGateway gmailGateway() {
        return new GmailRepoGateway();
    }
    @Bean
    public GmailUseCase gmailUseCase(GmailGateway gmailGateway) {
        return new GmailUseCase(gmailGateway);
    }
}
