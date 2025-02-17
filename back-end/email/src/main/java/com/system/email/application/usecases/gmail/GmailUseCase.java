package com.system.email.application.usecases.gmail;

import com.system.email.application.gateway.gmail.GmailGateway;

/**
 *
 * The gmail use case.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 *
 */
public class GmailUseCase {

    private final GmailGateway gmailGateway;

    public GmailUseCase(GmailGateway gmailGateway) {
        this.gmailGateway = gmailGateway;
    }

    public String getEmailContent(String messageId, String accessToken) {
        return gmailGateway.getEmailContent(messageId, accessToken);
    }

    /**
     *
     *
     *
     * @param jsonResponse
     *
     * @return
     */
    private String extractBodyFromJson(String jsonResponse) {
        return gmailGateway.extractBodyFromJson(jsonResponse);
    }

    /**
     *
     *
     *
     * @param encodedContent
     *
     * @return
     */
    private String decodeBase64(String encodedContent) {
        return gmailGateway.decodeBase64(encodedContent);
    }
}
