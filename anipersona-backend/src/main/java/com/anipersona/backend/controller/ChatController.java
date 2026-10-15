
package com.anipersona.backend.controller;

import com.anipersona.backend.dto.ChatRequest;
import com.anipersona.backend.dto.ChatResponse;
import com.anipersona.backend.model.CharacterProfile;
import com.anipersona.backend.service.CatalogService;
import com.anipersona.backend.service.CharacterPromptService;
import com.anipersona.backend.service.OpenRouterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

        private final CatalogService catalog;
        private final CharacterPromptService prompts;
        private final OpenRouterService openRouter;
        private final int timeout;

        public ChatController(
                        CatalogService catalog,
                        CharacterPromptService prompts,
                        OpenRouterService openRouter,
                        @Value("${anipersona.openrouter.request-timeout-seconds:60}") int timeout) {
                this.catalog = catalog;
                this.prompts = prompts;
                this.openRouter = openRouter;
                this.timeout = timeout;
        }

        @PostMapping
        public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
                var universe = catalog.universe(request.universeId());

                CharacterProfile ai = catalog.characterInUniverse(
                                request.aiCharacterId(),
                                request.universeId());

                CharacterProfile user = catalog.characterInUniverse(
                                request.userCharacterId(),
                                request.universeId());

                String reply = openRouter.reply(
                                prompts.build(universe.name(), ai, user, request),
                                request.messages(),
                                timeout);

                return new ChatResponse(reply, ai.name(), user.name());
        }
}
