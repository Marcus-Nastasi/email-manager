package com.system.email.application.usecases;

import com.system.email.application.gateway.GmailGateway;

import java.util.List;

/**
 * The gmail use case.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
public class GmailUseCase {

    private final GmailGateway gmailGateway;

    public GmailUseCase(GmailGateway gmailGateway) {
        this.gmailGateway = gmailGateway;
    }

    /**
     * This method lists the e-mail ids.
     *
     * @param accessToken user token.
     * @param maxResults the max number of e-mail ids to list.
     * @param pageToken the next page's token.
     *
     * @return a {@link List} of e-mail ids {@link String}
     */
    public List<String> listEmails(String accessToken, int maxResults, String pageToken) {
        return gmailGateway.listEmails(accessToken, maxResults, pageToken);
    }

    /**
     * This method allows to get an e-mail content from an id.
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return A {@link String} (json) with data.
     */
    public String getEmailContent(String messageId, String accessToken) {
        return gmailGateway.getEmailContent(messageId, accessToken);
    }

    /**
     * Makes the request to get the e-mail html.
     *
     * @param messageId the e-mail id.
     * @param accessToken the user token.
     *
     * @return the e-mail html in {@link String} format.
     */
    public String getEmailHtml(String messageId, String accessToken) {
        return gmailGateway.getEmailHtml(messageId, accessToken);
    }

    /**
     * This method allows to extract the body from the response.
     *
     * @param jsonResponse the response.
     *
     * @return the body extracted in {@link String} format.
     */
    private String extractBodyFromJson(String jsonResponse) {
        return gmailGateway.extractBodyFromJson(jsonResponse);
    }

    /**
     * This method allows to move an e-mail to trash.
     *
     * @param messageId e-mail id.
     *
     * @return a {@link String} representing the e-mail id moved to trash.
     */
    public String moveToTrash(String messageId, String acessToken) {
        return gmailGateway.moveToTrash(messageId, acessToken);
    }

    /**
     * This method allows to delete permanently the e-mail.
     *
     * @param messageId the e-mail id.
     *
     * @return a {@link Boolean} representing if the e-mail was deleted.
     */
    public String deleteEmail(String messageId, String accessToken) {
        return gmailGateway.deleteEmail(messageId, accessToken);
    }
}
