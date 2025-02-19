package com.system.email.adapters.resources.gmail;

import com.google.gson.JsonObject;
import com.system.email.application.usecases.gmail.GmailUseCase;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    public GmailResources(GmailUseCase gmailUseCase, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.gmailUseCase = gmailUseCase;
        this.authorizedClientService = oAuth2AuthorizedClientService;
    }

    @GetMapping("/find/email")
    public ResponseEntity<List<String>> findEmails(@RequestParam(defaultValue = "10") int maxResults,
                                             @RequestParam(required = false) String pageToken,
                                             OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null) throw new RuntimeException("Usuário não autenticado!");
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
        );
        List<String> emailIds = gmailUseCase.listEmails(authorizedClient
                .getAccessToken()
                .getTokenValue(),
                maxResults, pageToken);
//        List<Map<String, String>> emails = emailIds;

        return ResponseEntity.ok(emailIds);
    }

    /**
     *
     * This method opens the endpoint to retrieve e-mail HTML based on id.
     *
     * @param id the e-mail id.
     * @param authenticationToken the authentication from security oauth 2.
     *
     * @return The HTML string.
     */
    @GetMapping("/find/email/{id}")
    public ResponseEntity<String> findEmailById(@PathVariable("id") String id, OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null) throw new RuntimeException("Usuário não autenticado!");
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            authenticationToken.getAuthorizedClientRegistrationId(),
            authenticationToken.getName()
        );
        return ResponseEntity.ok(gmailUseCase.getEmailContent(id, authorizedClient.getAccessToken().getTokenValue()));
    }

    /**
     *
     * This method opens the endpoint to retrieve e-mail HTML based on id.
     *
     * @param id the e-mail id.
     * @param authenticationToken the authentication from security oauth 2.
     *
     * @return The HTML string.
     */
    @GetMapping("/find/email/html/{id}")
    public ResponseEntity<String> findEmailHtml(@PathVariable("id") String id, OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null) throw new RuntimeException("Usuário não autenticado!");
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
        );
        return ResponseEntity.ok(gmailUseCase.getEmailHtml(id, authorizedClient.getAccessToken().getTokenValue()));
    }
}
