package it.social.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.social.entity.UserFollowing;

@Repository
public interface UserFollowingRepository extends CrudRepository<UserFollowing, Long> {
	
	 Optional<UserFollowing> findByUserId(Long userId);
	 Optional<UserFollowing> findByUserIdAndUserFollowId(Long userId, Long userFollowId);
	 List<UserFollowing> findAllByUserId(Long userId);
	 

}
