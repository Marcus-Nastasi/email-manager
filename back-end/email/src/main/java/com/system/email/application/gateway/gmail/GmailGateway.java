package com.system.email.application.gateway.gmail;

import java.util.List;

/**
 * The gmail gateway interface.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
public interface GmailGateway {

    /**
     * This method should list the e-mail ids.
     *
     * @param accessToken user token.
     * @param maxResults the max number of e-mail ids to list.
     * @param pageToken the next page's token.
     *
     * @return a {@link List} of e-mail ids {@link String}
     */
    List<String> listEmails(String accessToken, int maxResults, String pageToken);

    /**
     * This method should allow to get an e-mail content from an id.
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return A {@link String} (json) with data.
     */
    String getEmailContent(String messageId, String accessToken);

    /**
     * Should make the request to get the e-mail html.
     *
     * @param messageId the e-mail id.
     * @param accessToken the user token.
     *
     * @return the e-mail html in {@link String} format.
     */
    String getEmailHtml(String messageId, String accessToken);

    /**
     * This method should allow to extract the body from the response.
     *
     * @param jsonResponse the response.
     *
     * @return the body extracted in {@link String} format.
     */
    String extractBodyFromJson(String jsonResponse);

    /**
     * This method should allow to move an e-mail to trash.
     *
     * @param messageId the e-mail id.
     *
     * @return a {@link String} representing the e-mail id moved to trash.
     */
    String moveToTrash(String messageId, String accessToken);

    /**
     * This method should allow to delete permanently the e-mail.
     *
     * @param messageId the e-mail id.
     *
     * @return a {@link String} representing the deleted e-mail.
     */
    String deleteEmail(String messageId, String accessToken);
}
