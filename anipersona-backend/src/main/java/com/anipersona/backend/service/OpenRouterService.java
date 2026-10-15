
package com.anipersona.backend.service;

import com.anipersona.backend.dto.IncomingMessage;
import com.anipersona.backend.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    private final String apiKey;
    private final String model;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public OpenRouterService(
            @Value("${anipersona.openrouter.api-key:}") String apiKey,
            @Value("${anipersona.openrouter.model:}") String model,
            ObjectMapper mapper) {

        this.apiKey = apiKey;
        this.model = model;
        this.mapper = mapper;

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public String reply(
            String systemInstruction,
            List<IncomingMessage> history,
            int requestTimeout) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "OpenRouter API key is missing. Configure OPENROUTER_API_KEY.");
        }

        if (model == null || model.isBlank()) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "OpenRouter model is missing. Configure OPENROUTER_MODEL.");
        }

        try {
            List<Map<String, String>> messages = new ArrayList<>();

            messages.add(Map.of(
                    "role", "system",
                    "content", systemInstruction));

            for (IncomingMessage message : history) {
                if (message == null
                        || message.role() == null
                        || message.content() == null
                        || message.content().isBlank()) {
                    continue;
                }

                String role = message.role().trim();

                if (!role.equals("user") && !role.equals("assistant")) {
                    throw new ApiException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid message role. Use user or assistant.");
                }

                messages.add(Map.of(
                        "role", role,
                        "content", message.content().trim()));
            }

            if (messages.size() < 2) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Please provide at least one non-empty chat message.");
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("messages", messages);
            payload.put("temperature", 0.8);
            payload.put("max_tokens", 500);
            payload.put("stream", false);

            String requestBody = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("X-Title", "AniPersona")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            JsonNode root = mapper.readTree(response.body());

            if (status == 401 || status == 403) {
                throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "OpenRouter rejected the API key or access to the model. HTTP "
                                + status);
            }

            if (status == 402) {
                throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "OpenRouter account has insufficient credits. Check your account balance.");
            }

            if (status == 429) {
                throw new ApiException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "OpenRouter rate limit reached. Please try again later.");
            }

            if (status != 200) {
                String errorMessage = root.path("error")
                        .path("message")
                        .asText("No error details provided");

                throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "OpenRouter request failed with HTTP "
                                + status + ": " + errorMessage);
            }

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                String errorMessage = root.path("error")
                        .path("message")
                        .asText("OpenRouter returned no choices");

                throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "OpenRouter returned no AI response: " + errorMessage);
            }

            JsonNode firstChoice = choices.get(0);
            JsonNode messageNode = firstChoice.path("message");
            JsonNode content = messageNode.path("content");

            if (content.isTextual() && !content.asText().isBlank()) {
                return content.asText().trim();
            }

            if (content.isArray()) {
                StringBuilder combinedContent = new StringBuilder();

                for (JsonNode part : content) {
                    if (part.hasNonNull("text")) {
                        combinedContent.append(part.path("text").asText());
                    }
                }

                if (!combinedContent.toString().isBlank()) {
                    return combinedContent.toString().trim();
                }
            }

            String finishReason = firstChoice.path("finish_reason")
                    .asText("unknown");

            String refusal = messageNode.path("refusal")
                    .asText("");

            if (!refusal.isBlank()) {
                throw new ApiException(
                        HttpStatus.BAD_GATEWAY,
                        "The AI model refused the request: " + refusal);
            }

            throw new ApiException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenRouter returned an empty reply. Finish reason: "
                            + finishReason
                            + ". Check whether the selected model supports chat completions.");

        } catch (ApiException e) {
            throw e;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The AI request was interrupted. Please try again.");

        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not connect to OpenRouter or process its response: "
                            + e.getClass().getSimpleName());
        }
    }
}
