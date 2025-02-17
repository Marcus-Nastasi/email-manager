package com.system.email.adapters.resources.profile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProfileResource {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public ProfileResource(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     *
     * Validates if user is logged in.
     *
     * <p>This endpoint allows users to retrieve their personal Google Account object,
     * to check if it's logged in.<p/>
     *
     * @param user The Oidc User object.
     * @param authentication The authentication token.
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
        response.put(
            "refresh_token",
            authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : "No refresh token"
        );
        return response;
    }
}
