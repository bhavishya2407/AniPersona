package com.anipersona.backend.model;

import java.time.Instant;

public record ChatMessage(String role, String content, Instant timestamp) {}
