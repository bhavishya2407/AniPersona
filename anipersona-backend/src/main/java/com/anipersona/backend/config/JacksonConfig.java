package com.anipersona.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Supplies the JSON mapper used for Groq's OpenAI-compatible HTTP payloads. */
@Configuration
public class JacksonConfig {
  @Bean
  ObjectMapper objectMapper() {
    return JsonMapper.builder().findAndAddModules().build();
  }
}
