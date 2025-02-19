package com.system.email.application.gateway.gmail;

import java.util.List;

/**
 *
 * The gmail gateway interface.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
public interface GmailGateway {

    List<String> listEmails(String accessToken, int maxResults, String pageToken);

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
     * This method should allow to extract the body from the response.
     *
     * @param jsonResponse the response.
     *
     * @return the body extracted.
     */
    String extractBodyFromJson(String jsonResponse);

    /**
     *
     * This method should allow to decode the response from base64.
     *
     * @param encodedContent content encoded in base64.
     *
     * @return the pure html string.
     */
    String decodeBase64(String encodedContent);
}
