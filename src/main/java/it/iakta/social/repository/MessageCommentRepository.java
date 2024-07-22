package it.iakta.social.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.iakta.social.entity.MessageComment;

@Repository
public interface MessageCommentRepository extends CrudRepository<MessageComment, Long> {
	
	 Optional<MessageComment> findByMessageIdAndId(Long messageId, Long id);
	 
	 List<MessageComment> findAllByMessageId(Long messageId);

}
