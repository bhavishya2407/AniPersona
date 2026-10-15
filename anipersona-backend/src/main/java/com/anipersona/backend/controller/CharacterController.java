package com.anipersona.backend.controller;
import com.anipersona.backend.model.CharacterProfile;
import com.anipersona.backend.service.CatalogService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/characters") public class CharacterController {
 private final CatalogService catalog; public CharacterController(CatalogService c) { catalog=c; }
 @GetMapping("/{characterId}") CharacterProfile character(@PathVariable String characterId) { return catalog.character(characterId); }
}
