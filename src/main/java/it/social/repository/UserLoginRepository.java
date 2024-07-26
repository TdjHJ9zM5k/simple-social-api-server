package it.social.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.social.entity.UserLogin;

@Repository
public interface UserLoginRepository extends CrudRepository<UserLogin, Long> {
	
	 Optional<UserLogin> findByUsername(String username);

	 Boolean existsByUsername(String username);
}
