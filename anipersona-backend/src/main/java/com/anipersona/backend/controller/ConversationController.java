package com.anipersona.backend.controller;
import com.anipersona.backend.exception.ApiException;
import com.anipersona.backend.model.Conversation;
import com.anipersona.backend.repository.ConversationRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/conversations") public class ConversationController {
 private final ConversationRepository repository; public ConversationController(ConversationRepository r) { repository=r; }
 @GetMapping List<Conversation> list(@RequestHeader(value="X-Session-Id", required=false) String sessionId) { return repository.findBySessionIdOrderByUpdatedAtDesc(session(sessionId)); }
 @GetMapping("/{id}") Conversation get(@PathVariable String id,@RequestHeader(value="X-Session-Id",required=false) String sessionId) { return repository.findByIdAndSessionId(id,session(sessionId)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Conversation not found.")); }
 @PostMapping Conversation create(@RequestBody Conversation conversation,@RequestHeader(value="X-Session-Id",required=false) String sessionId) { conversation.setId(null); conversation.setSessionId(session(sessionId)); conversation.setCreatedAt(Instant.now()); conversation.setUpdatedAt(Instant.now()); return save(conversation); }
 @DeleteMapping("/{id}") void delete(@PathVariable String id,@RequestHeader(value="X-Session-Id",required=false) String sessionId) { Conversation c=get(id,sessionId); repository.delete(c); }
 private Conversation save(Conversation c) { try { return repository.save(c); } catch(DataAccessException e) { throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,"Conversation storage is currently unavailable. Check MongoDB configuration."); } }
 private String session(String value) { if(value==null || !value.matches("[A-Za-z0-9_-]{12,100}")) throw new ApiException(HttpStatus.BAD_REQUEST,"A valid anonymous session ID is required."); return value; }
}
