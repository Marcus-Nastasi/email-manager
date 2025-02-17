package com.system.email.adapters.resources.gmail;

import com.system.email.application.usecases.gmail.GmailUseCase;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * The gmail resource.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
@RestController
@RequestMapping("/gmail")
public class GmailResources {

    private final GmailUseCase gmailUseCase;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public GmailResources(GmailUseCase gmailUseCase, OAuth2AuthorizedClientService authorizedClientService) {
        this.gmailUseCase = gmailUseCase;
        this.authorizedClientService = authorizedClientService;
    }

    /**
     *
     * This method opens the endpoint to retrieve e-mail HTML based on id.
     *
     * @param id the e-mail id.
     * @param authentication the authentication from security oauth 2.
     *
     * @return The HTML string.
     */
    @GetMapping("/find/{id}")
    public String getEmailById(@PathVariable String id, OAuth2AuthenticationToken authentication) {
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName()
        );
        return gmailUseCase.getEmailContent(id, authorizedClient.getAccessToken().getTokenValue());
    }
}
