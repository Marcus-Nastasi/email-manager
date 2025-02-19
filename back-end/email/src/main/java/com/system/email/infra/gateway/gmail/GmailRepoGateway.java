package com.system.email.infra.gateway.gmail;

import com.google.gson.*;
import com.system.email.application.gateway.gmail.GmailGateway;
import com.system.email.infra.model.gmail.PayloadHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 *
 * The gmail gateway implementation.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
public class GmailRepoGateway implements GmailGateway {

    @Autowired
    private Gson gson;
    private static final String API_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/";

    /**
     *
     * This method allows to get an e-mail content from an id.
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return the pure html string.
     */
    public String getEmailContent(String messageId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL + messageId, HttpMethod.GET, entity, String.class);
        return extractBodyToEmailContent(response.getBody());
    }

    /**
     *
     * This method allows to get an e-mail content from an id.
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return the pure html string.
     */
    public String getEmailHtml(String messageId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL + messageId, HttpMethod.GET, entity, String.class);
        return extractBodyFromJson(response.getBody());
    }

    public String extractBodyToEmailContent(String jsonResponse) {
        try {
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
            List<String> labelIds = json.get("labelIds")
                .getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::toString)
                .toList();
            JsonObject payload = json.getAsJsonObject("payload");
            Map<String, Object> responseMap = new HashMap<>();

            responseMap.put("id", json.get("id").getAsString());
            responseMap.put("labelIds", labelIds);
            responseMap.put("snippet", json.get("snippet").getAsString());

            if (payload.has("headers")) {
                List<PayloadHeaders> headers = payload.get("headers")
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .map(e -> gson.fromJson(e.toString(), PayloadHeaders.class))
                    .toList();

                for (PayloadHeaders s: headers) {
                    if (s.name().equals("Date")) responseMap.put("date", s.value());
                    if (s.name().equals("From")) responseMap.put("from", s.value());
                    if (s.name().equals("Subject")) responseMap.put("subject", s.value());
                }

            }

            // Verify if is parts on e-mail (text/plain and text/html).
            if (payload.has("parts")) {
                JsonArray parts = payload.getAsJsonArray("parts");

                for (int i = 0; i < parts.size(); i++) {
                    JsonObject part = parts.get(i).getAsJsonObject();
                    String mimeType = part.get("mimeType").getAsString();

                    if ("text/plain".equals(mimeType)) {
                        String content = part.getAsJsonObject("body").get("data").getAsString();
                        String contentDecoded = this.decodeBase64(content);
                        responseMap.put("partOneText", contentDecoded);
                    }
                }

                return gson.toJson(responseMap);

            } else if (payload.has("body")) { // If it doesn't have "parts", verify the body directly.
                String encodedContent = payload.getAsJsonObject("body").get("data").getAsString();
                responseMap.put("partOneText", decodeBase64(encodedContent));
                return gson.toJson(responseMap);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
        return "Error processing e-mail.";
    }

    /**
     *
     * This method allows to extract the body from the response.
     *
     * @param jsonResponse the api response.
     *
     * @return the body extracted.
     */
    public String extractBodyFromJson(String jsonResponse) {
        try {
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
            JsonObject payload = json.getAsJsonObject("payload");

            // Verify if is parts on e-mail (text/plain and text/html).
            if (payload.has("parts")) {
                JsonArray parts = payload.getAsJsonArray("parts");

                for (int i = 0; i < parts.size(); i++) {
                    JsonObject part = parts.get(i).getAsJsonObject();
                    String mimeType = part.get("mimeType").getAsString();

                    if ("text/html".equals(mimeType)) { // Prioritizes HTML.
                        String encodedContent = part.getAsJsonObject("body").get("data").getAsString();
                        return decodeBase64(encodedContent);
                    }
                }

            } else if (payload.has("body")) { // If it doesn't have "parts", verify the body directly.
                String encodedContent = payload.getAsJsonObject("body").get("data").getAsString();
                return decodeBase64(encodedContent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing e-mail.");
        }
        return "Error processing e-mail.";
    }

    /**
     *
     * This method allows to decode the response from base64.
     *
     * @param encodedContent content encoded in base64.
     *
     * @return the pure html string.
     */
    public String decodeBase64(String encodedContent) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedContent);
        return new String(decodedBytes);
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
    @Override
    public List<String> listEmails(String accessToken, int maxResults, String pageToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=" + maxResults;

        if (pageToken != null && !pageToken.isEmpty()) {
            url += "&pageToken=" + pageToken;
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray messages = jsonResponse.getAsJsonArray("messages");
        List<String> messageIds = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            JsonObject message = messages.get(i).getAsJsonObject();
            messageIds.add(message.get("id").getAsString());
        }

        messageIds.add(jsonResponse.get("nextPageToken").getAsString());
        return messageIds;
    }
}
