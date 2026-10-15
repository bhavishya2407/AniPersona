package com.anipersona.backend.repository;
import com.anipersona.backend.model.Conversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ConversationRepository extends MongoRepository<Conversation, String> {
  List<Conversation> findBySessionIdOrderByUpdatedAtDesc(String sessionId);
  Optional<Conversation> findByIdAndSessionId(String id, String sessionId);
}
