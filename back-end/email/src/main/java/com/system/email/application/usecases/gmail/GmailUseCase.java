package com.system.email.application.usecases.gmail;

import com.system.email.application.gateway.gmail.GmailGateway;

import java.util.List;

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

    /**
     *
     *
     *
     * @param accessToken
     * @param maxResults
     * @param pageToken
     * @return
     */
    public List<String> listEmails(String accessToken, int maxResults, String pageToken) {
        return gmailGateway.listEmails(accessToken, maxResults, pageToken);
    }

    /**
     *
     *
     *
     * @param messageId
     * @param accessToken
     * @return
     */
    public String getEmailContent(String messageId, String accessToken) {
        return gmailGateway.getEmailContent(messageId, accessToken);
    }

    /**
     *
     *
     *
     * @param messageId
     * @param accessToken
     * @return
     */
    public String getEmailHtml(String messageId, String accessToken) {
        return gmailGateway.getEmailHtml(messageId, accessToken);
    }

    /**
     *
     * This method allows to extract the body from the response.
     *
     * @param jsonResponse the api response.
     *
     * @return the body extracted.
     */
    private String extractBodyFromJson(String jsonResponse) {
        return gmailGateway.extractBodyFromJson(jsonResponse);
    }

    public boolean deleteEmail(String messageId) {
        return gmailGateway.deleteEmail(messageId);
    }
}
