package com.anipersona.backend.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("conversations")
public class Conversation {
  @Id private String id;
  private String sessionId, universeId, aiCharacterId, userCharacterId, scenario, tone, language;
  private Instant createdAt = Instant.now(), updatedAt = Instant.now();
  private List<ChatMessage> messages = new ArrayList<>();
  public String getId() { return id; } public void setId(String id) { this.id = id; }
  public String getSessionId() { return sessionId; } public void setSessionId(String value) { sessionId = value; }
  public String getUniverseId() { return universeId; } public void setUniverseId(String value) { universeId = value; }
  public String getAiCharacterId() { return aiCharacterId; } public void setAiCharacterId(String value) { aiCharacterId = value; }
  public String getUserCharacterId() { return userCharacterId; } public void setUserCharacterId(String value) { userCharacterId = value; }
  public String getScenario() { return scenario; } public void setScenario(String value) { scenario = value; }
  public String getTone() { return tone; } public void setTone(String value) { tone = value; }
  public String getLanguage() { return language; } public void setLanguage(String value) { language = value; }
  public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant value) { createdAt = value; }
  public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant value) { updatedAt = value; }
  public List<ChatMessage> getMessages() { return messages; } public void setMessages(List<ChatMessage> value) { messages = value; }
}
