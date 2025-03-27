package com.system.email.adapters.resources.auth;

import com.system.email.adapters.output.AuthenticationResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * The resource to get profile information or authentication.
 *
 * @author Marcus Rolemberg
 * @version 1.0.1
 * @since 2025
 */
@RestController
public class AuthResource {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthResource(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * Validates if user is logged in.
     *
     * <p>This endpoint allows users to retrieve their personal Google Account object,
     * to check if it's logged in.<p/>
     *
     * @param user The Oidc User object.
     * @param authentication The authentication token.
     *
     * @return Return a map of a spring an objects, that represents the personal google account.
     */
    @GetMapping("/")
    public Map<String, Object> getUser(@AuthenticationPrincipal OidcUser user, OAuth2AuthenticationToken authentication) {
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName()
        );
        // Creating the response object reference.
        Map<String, Object> response = new HashMap<>(user.getClaims());
        // Putting the access_token and refresh_token manually.
        response.put("access_token", authorizedClient.getAccessToken().getTokenValue());
        response.put("refresh_token", authorizedClient.getRefreshToken() != null
            ? authorizedClient.getRefreshToken().getTokenValue()
            : "No refresh token");
        return response;
    }

    /**
     * This method allows to get access tokens of a logged user.
     *
     * @param authentication the OAuth2AuthenticationToken object.
     *
     * @return the access toke in JSON format.
     */
    @GetMapping("/token")
    public AuthenticationResponseDto getToken(OAuth2AuthenticationToken authentication) {
        // Using OAuth2AuthorizedClientService to load the user that is logged.
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName()
        );
        return new AuthenticationResponseDto(authorizedClient.getAccessToken().getTokenValue());
    }
}
