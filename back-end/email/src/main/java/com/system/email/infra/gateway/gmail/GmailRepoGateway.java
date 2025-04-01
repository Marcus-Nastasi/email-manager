package com.system.email.infra.gateway.gmail;

import com.google.gson.*;
import com.system.email.application.gateway.gmail.GmailGateway;
import com.system.email.infra.model.gmail.EmailPayloadHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
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
     * This function allows to list all e-mails as a string array.
     *
     * @param accessToken the user access token.
     * @param maxResults the max number of e-mails to get.
     * @param pageToken the page token number.
     *
     * @return a list of strings that are the e-mail ids and next page token.
     */
    @Override
    public List<String> listEmails(String accessToken, int maxResults, String pageToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        // Make api call to get e-mail ids.
        String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=" + maxResults;
        // Check if next page token has been passed as an argument.
        if (pageToken != null && !pageToken.isEmpty()) {
            url += "&pageToken=" + pageToken;
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String response = restTemplate
            .exchange(url, HttpMethod.GET, entity, String.class)
            .getBody();
        JsonObject responseParsed = gson.fromJson(response, JsonObject.class);
        JsonArray emailsList = responseParsed.getAsJsonArray("messages");
        String nextPageToken = responseParsed.get("nextPageToken").getAsString();
        List<String> emailIds = new ArrayList<>();
        for (int i = 0; i < emailsList.size(); i++) {
            JsonObject email = emailsList.get(i).getAsJsonObject();
            String emailId = email.get("id").getAsString();
            emailIds.add(emailId);
        }
        emailIds.add(nextPageToken);
        return emailIds;
    }

    /**
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
        ResponseEntity<String> response = restTemplate.exchange(
            API_URL
            + messageId,
            HttpMethod.GET,
            entity,
            String.class);
        return extractBodyToEmailContent(response.getBody());
    }

    /**
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
        ResponseEntity<String> response = restTemplate.exchange(
            API_URL
            + messageId,
            HttpMethod.GET,
            entity,
            String.class);
        return extractBodyFromJson(response.getBody());
    }

    /**
     * This method allows to extract the card model information from the json
     * response from gmail.
     *
     * @param jsonResponse the gmail response to parse.
     *
     * @return the structured object that represents the card data.
     */
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
            // Including id, labels and snippet on response.
            responseMap.put("id", json.get("id").getAsString());
            responseMap.put("labelIds", labelIds);
            responseMap.put("snippet", json.get("snippet").getAsString());
            if (payload.has("headers")) {
                // Parsing header objects to PayloadHeaders record class.
                List<EmailPayloadHeaders> headers = payload.get("headers")
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .map(e -> gson.fromJson(e.toString(), EmailPayloadHeaders.class))
                    .toList();
                // Getting information from payload header.
                for (EmailPayloadHeaders s: headers) {
                    if (s.name().equals("Date")) responseMap.put("date", s.value());
                    if (s.name().equals("From")) {
                        String fromString = s.value();
                        String name = Arrays.stream(fromString.split("<")).toList().getFirst().trim();
                        String email = Arrays.stream(fromString.split("<")).toList().getLast().replace(">", "").trim();
                        responseMap.put("from", name + " " + email);
                    }
                    if (s.name().equals("Subject")) responseMap.put("subject", s.value());
                }
            }
            // Verify if is parts on e-mail (text/plain and text/html).
            if (payload.has("parts")) {
                JsonArray parts = payload.getAsJsonArray("parts");
                // Iterate into parts to find text/plain mime type object.
                for (int i = 0; i < parts.size(); i++) {
                    JsonObject part = parts.get(i).getAsJsonObject();
                    String mimeType = part.get("mimeType").getAsString();
                    if ("text/plain".equals(mimeType)) {
                        String content = part.getAsJsonObject("body").get("data").getAsString();
                        String contentDecoded = decodeBase64(content);
                        responseMap.put("partOneText", contentDecoded);
                    }
                }
                return gson.toJson(responseMap);
            } else if (payload.has("body")) { // If it doesn't have "parts", verify the body directly.
                String encodedContent = payload.getAsJsonObject("body").get("data").getAsString();
                responseMap.put("partOneText", decodeBase64(encodedContent));
                return gson.toJson(responseMap);
            }
            return "Error processing e-mail: does not have parts or body";
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * This method allows to extract the body from the response and return HTML string.
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
                    // Prioritizes HTML.
                    if ("text/html".equals(mimeType)) {
                        String encodedContent = part.getAsJsonObject("body").get("data").getAsString();
                        return decodeBase64(encodedContent);
                    }
                }
            } else if (payload.has("body")) { // If it doesn't have "parts", verify the body directly.
                String encodedContent = payload.getAsJsonObject("body").get("data").getAsString();
                return decodeBase64(encodedContent);
            }
            return "Error processing e-mail: does not have parts.";
        } catch (Exception e) {
            throw new RuntimeException("Error processing e-mail.");
        }
    }

    /**
     * This method implements the function to move an e-mail to trash.
     *
     * @param messageId e-mail id.
     *
     * @return a string representing the e-mail id moved to trash.
     */
    @Override
    public String moveToTrash(String messageId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL + messageId + "/trash";
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class);
        return response.getBody();
    }

    @Override
    public boolean deleteEmail(String messageId) {
        return false;
    }

    /**
     * This method allows to decode the response from base64.
     *
     * @param encodedContent content encoded in base64.
     *
     * @return the pure html string.
     */
    private String decodeBase64(String encodedContent) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedContent);
        return new String(decodedBytes);
    }
}
