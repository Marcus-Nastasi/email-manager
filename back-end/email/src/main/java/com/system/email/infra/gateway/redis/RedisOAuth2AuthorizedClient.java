package com.system.email.infra.gateway.redis;

import com.system.email.infra.model.auth.OAuth2AuthorizedClientDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Instant;

/**
 * The Redis implementation for OAuth2AuthorizedClientService.
 *
 * <p>This class allows us to use Redis as the default session saving tool by implementing
 * OAuth2AuthorizedClientService methods.<p/>
 *
 * @author Marcus Nastasi
 * @version 1.0.2
 * @since 2025
 * */
public class RedisOAuth2AuthorizedClient implements OAuth2AuthorizedClientService {

    private final RedisTemplate<String, OAuth2AuthorizedClientDto> redisTemplate;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final String REDIS_KEY_PREFIX = "oauth2:client:";

    public RedisOAuth2AuthorizedClient(RedisTemplate<String, OAuth2AuthorizedClientDto> redisTemplate, ClientRegistrationRepository clientRegistrationRepository) {
        this.redisTemplate = redisTemplate;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * <p>This function allows the application to save the login authorizations on Redis.<p/>
     *
     * @param authorizedClient The client object that has logged in.
     * @param principal The authentication object.
     */
    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String key = REDIS_KEY_PREFIX + principal.getName();
        // Manually creating the Dto object to save in Redis.
        OAuth2AuthorizedClientDto dto = new OAuth2AuthorizedClientDto(
                authorizedClient.getClientRegistration().getRegistrationId(),
                principal.getName(),
                authorizedClient.getAccessToken().getTokenValue(),
                authorizedClient.getAccessToken().getExpiresAt().toEpochMilli(),
                authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null
        );
        redisTemplate.opsForValue().set(key, dto); // Saving object in Redis database.
    }

    /**
     * <p>This function allows the application to load back the user authorized form Redis.<p/>
     *
     * @param clientRegistrationId The client ID that is registered.
     * @param principalName The username saved as the key on Redis.
     *
     * @return Return the OAuth2AuthorizedClient object that we've had parsed from Redis.
     */
    @Override
    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId, String principalName) {
        String key = REDIS_KEY_PREFIX + principalName;
        // Getting the object from Redis by its key.
        OAuth2AuthorizedClientDto dto = redisTemplate.opsForValue().get(key);
        // Constructing the OAuth2AccessToken manually.
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                dto.getAccessToken(),
                Instant.now(),
                Instant.ofEpochMilli(dto.getAccessTokenExpiresAt())
        );
        // Refreshing the token.
        OAuth2RefreshToken refreshToken = dto.getRefreshToken() != null
                ? new OAuth2RefreshToken(dto.getRefreshToken(), Instant.now())
                : null;
        // Manually constructing the OAuth2AuthorizedClient.
        return new OAuth2AuthorizedClient(
                clientRegistrationRepository.findByRegistrationId(dto.getClientRegistrationId()),
                dto.getPrincipalName(),
                accessToken,
                refreshToken
        );
    }

    /**
     * <p>Auxiliar function that deletes a registre on Redis, based on the client ID and name.<p/>
     *
     * @param clientRegistrationId The client ID that is registered.
     * @param principalName The username saved as the key on Redis.
     */
    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        redisTemplate.delete(REDIS_KEY_PREFIX + principalName);
    }
}
