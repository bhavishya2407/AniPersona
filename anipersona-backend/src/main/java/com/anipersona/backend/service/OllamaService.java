package com.anipersona.backend.service;

import com.anipersona.backend.dto.IncomingMessage;
import com.anipersona.backend.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Calls Ollama's local /api/chat endpoint. No cloud key is required. */
@Service
public class OllamaService {
  private final String baseUrl, model;
  private final HttpClient client;
  private final ObjectMapper mapper;

  public OllamaService(@Value("${anipersona.ollama.base-url}") String baseUrl,
      @Value("${anipersona.ollama.model}") String model,
      @Value("${anipersona.ollama.connect-timeout-seconds}") int connectTimeout, ObjectMapper mapper) {
    this.baseUrl = baseUrl.replaceAll("/$", ""); this.model = model; this.mapper = mapper;
    this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(connectTimeout)).build();
  }

  public String reply(String systemInstruction, List<IncomingMessage> history, int requestTimeout) {
    try {
      List<Map<String, String>> messages = new ArrayList<>();
      messages.add(Map.of("role", "system", "content", systemInstruction));
      for (IncomingMessage message : history) messages.add(Map.of("role", message.role(), "content", message.content().trim()));
      Map<String, Object> payload = new LinkedHashMap<>();
      payload.put("model", model); payload.put("messages", messages); payload.put("stream", false);
      payload.put("options", Map.of("temperature", 0.8));
      HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/chat"))
        .header("Content-Type", "application/json").timeout(Duration.ofSeconds(requestTimeout))
        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))).build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) throw new ApiException(HttpStatus.BAD_GATEWAY, "Ollama could not generate a reply. Confirm the model is downloaded and try again.");
      JsonNode content = mapper.readTree(response.body()).path("message").path("content");
      if (content.isMissingNode() || content.asText().isBlank()) throw new ApiException(HttpStatus.BAD_GATEWAY, "Ollama returned an empty reply.");
      return content.asText().trim();
    } catch (ApiException e) { throw e;
    } catch (Exception e) { throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Ollama is not running. Install Ollama, run 'ollama pull llama3.2', then start it."); }
  }
}
