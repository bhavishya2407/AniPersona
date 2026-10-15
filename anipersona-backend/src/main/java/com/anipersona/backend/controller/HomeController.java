package com.anipersona.backend.controller;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController public class HomeController {
 @GetMapping("/") String home() { return "Welcome to AniPersona! Backend is working."; }
 @GetMapping("/api/health") Map<String,String> health() { return Map.of("status","UP","application","AniPersona"); }
}
