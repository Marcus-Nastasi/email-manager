package com.system.email.application.gateway.gmail;

/**
 *
 * The gmail gateway interface.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
public interface GmailGateway {

    /**
     *
     * This method should allow to get an e-mail content from an id.
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return A string (json) with data.
     */
    String getEmailContent(String messageId, String accessToken);

    /**
     *
     *
     *
     * @param jsonResponse
     *
     * @return
     */
    String extractBodyFromJson(String jsonResponse);

    /**
     *
     *
     *
     * @param encodedContent
     *
     * @return
     */
    String decodeBase64(String encodedContent);
}
