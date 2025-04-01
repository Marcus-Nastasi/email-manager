package com.system.email.adapters.resources.gmail;

import com.system.email.application.usecases.gmail.GmailUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
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

    /**
     * Method opens the endpoint to get a list of e-mail ids.
     *
     * @param maxResults the max number of e-mail ids to retrieve.
     * @param pageToken the token that refers to the next page.
     * @param authenticationToken OAuth token.
     *
     * @return the list of ids and next page token.
     */
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
        return ResponseEntity.ok(emailIds);
    }

    /**
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

    /**
     * This method opens the endpoint to send e-mail to trash based on id.
     *
     * @param id the e-mail id.
     * @param authenticationToken the authentication from security oauth 2.
     *
     * @return The HTML string.
     */
    @GetMapping("/trash/email/{id}")
    public ResponseEntity<String> moveToTrash(@PathVariable("id") String id, OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null) throw new RuntimeException("Usuário não autenticado!");
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName()
        );
        return ResponseEntity.ok(gmailUseCase.moveToTrash(id, authorizedClient.getAccessToken().getTokenValue()));
    }
}
