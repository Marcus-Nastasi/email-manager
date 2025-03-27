package com.system.email.infra.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * The Redis Dto to represent the OAuth2AuthorizedClient.
 *
 * <p>This class allows us to use a Dto to save the session's credentials on Redis.<p/>
 *
 * @author Marcus Nastasi
 * @version 1.0.2
 * @since 2025
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2AuthorizedClientDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clientRegistrationId;

    private String principalName;

    private String accessToken;

    private long accessTokenExpiresAt;

    private String refreshToken;

    public OAuth2AuthorizedClientDto() {}

    @JsonCreator
    public OAuth2AuthorizedClientDto(
            @JsonProperty("clientRegistrationId") String clientRegistrationId,
            @JsonProperty("principalName") String principalName,
            @JsonProperty("accessToken") String accessToken,
            @JsonProperty("accessTokenExpiresAt") long accessTokenExpiresAt,
            @JsonProperty("refreshToken") String refreshToken) {
        this.clientRegistrationId = clientRegistrationId;
        this.principalName = principalName;
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshToken = refreshToken;
    }

    public String getClientRegistrationId() {
        return clientRegistrationId;
    }

    public void setClientRegistrationId(String clientRegistrationId) {
        this.clientRegistrationId = clientRegistrationId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(long accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
