package it.iakta.social.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.iakta.social.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	 Optional<User> findByUsername(String username);

	 Boolean existsByUsername(String username);
}
