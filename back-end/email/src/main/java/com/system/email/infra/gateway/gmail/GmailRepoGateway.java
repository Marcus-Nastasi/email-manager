package com.system.email.infra.gateway.gmail;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.system.email.application.gateway.gmail.GmailGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

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
     *
     *
     * @param messageId the id of the e-mail.
     * @param accessToken the user token.
     *
     * @return
     */
    public String getEmailContent(String messageId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL + messageId, HttpMethod.GET, entity, String.class);
        return extractBodyFromJson(response.getBody());
    }

    /**
     *
     *
     *
     * @param jsonResponse
     *
     * @return
     */
    public String extractBodyFromJson(String jsonResponse) {
        try {
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
            JsonObject payload = json.getAsJsonObject("payload");
            // Verifica se há partes no e-mail (text/plain e text/html)
            if (payload.has("parts")) {
                JsonArray parts = payload.getAsJsonArray("parts");
                for (int i = 0; i < parts.size(); i++) {
                    JsonObject part = parts.get(i).getAsJsonObject();
                    String mimeType = part.get("mimeType").getAsString();
                    if ("text/html".equals(mimeType)) { // Prioriza HTML
                        String encodedContent = part.getAsJsonObject("body").get("data").getAsString();
                        return decodeBase64(encodedContent);
                    }
                }
            } else if (payload.has("body")) { // Caso não tenha "parts", verifica o body diretamente
                String encodedContent = payload.getAsJsonObject("body").get("data").getAsString();
                return decodeBase64(encodedContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao processar o e-mail.";
    }

    /**
     *
     *
     *
     * @param encodedContent
     *
     * @return
     */
    public String decodeBase64(String encodedContent) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedContent);
        return new String(decodedBytes);
    }
}
