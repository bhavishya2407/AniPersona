package com.anipersona.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record IncomingMessage(@Pattern(regexp="user|assistant", message="role must be user or assistant") String role, @NotBlank @Size(max=2000) String content) {}
