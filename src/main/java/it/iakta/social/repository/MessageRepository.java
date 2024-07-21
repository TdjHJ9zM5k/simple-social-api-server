package it.iakta.social.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.iakta.social.entity.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
	
	 List<Message> findAllByUserId(Long userId);

}
