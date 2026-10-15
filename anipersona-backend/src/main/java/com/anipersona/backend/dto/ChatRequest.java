package com.anipersona.backend.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
public record ChatRequest(@NotBlank String universeId, @NotBlank String aiCharacterId, @NotBlank String userCharacterId,
  @Size(max=800) String scenario, @Size(max=60) String tone, @Size(max=60) String language,
  @Valid @Size(min=1, max=20) List<IncomingMessage> messages) {}
