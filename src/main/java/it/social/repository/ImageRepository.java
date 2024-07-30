package it.social.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.social.entity.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
	
	Optional<Image> findByName(String name);
	
	Optional<Image> findByMessageIdAndName(Long messageId, String name);
	
	Optional<Image> findByMessageId(Long messageId);
	
	boolean existsByName(String name);
	
}
