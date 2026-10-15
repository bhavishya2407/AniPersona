package com.anipersona.backend.controller;
import com.anipersona.backend.model.AnimeUniverse;
import com.anipersona.backend.model.CharacterProfile;
import com.anipersona.backend.service.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/anime") public class AnimeController {
 private final CatalogService catalog; public AnimeController(CatalogService c) { catalog=c; }
 @GetMapping("/themes") List<AnimeUniverse> themes() { return catalog.universes(); }
 @GetMapping("/{animeId}/characters") List<CharacterProfile> characters(@PathVariable String animeId) { return catalog.characters(animeId); }
}
