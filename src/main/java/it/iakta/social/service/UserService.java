package it.iakta.social.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.iakta.social.dto.UserDTO;
import it.iakta.social.dto.UsernameDTO;
import it.iakta.social.entity.UserFollowing;
import it.iakta.social.entity.UserLogin;
import it.iakta.social.repository.UserFollowingRepository;
import it.iakta.social.repository.UserLoginRepository;

@Service
public class UserService {
	
	@Autowired
    private UserLoginRepository userRepository;
	
	@Autowired
    private UserFollowingRepository userFollowingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public boolean existsById(Long id) {
		return userRepository.existsById(id);
	}
	
	public boolean doesUserFollow(Long userId, Long followedUserId) {
		return userFollowingRepository.findByUserIdAndUserFollowId(userId, followedUserId).isPresent();
	}
	
	public UserFollowing followUser(Long userId, Long followedUserId) {
			UserFollowing userFollowing = new UserFollowing(userId, followedUserId);
	        return userFollowingRepository.save(userFollowing);
	}
	
	public Long unfollowUser(Long userId, Long followedUserId) {
		UserFollowing userFollowing = userFollowingRepository.findByUserIdAndUserFollowId(userId, followedUserId).get();
		userFollowingRepository.delete(userFollowing);
		return followedUserId;
	}
	
	public List<UsernameDTO> getFollowedUsers(Long userId) {
		return userFollowingRepository.findAllByUserId(userId).stream()
                .map(userFollowing -> new UsernameDTO(userFollowing.getUserFollowId(), userRepository.findById(userFollowing.getUserFollowId()).get().getUsername()))
                .toList();
	}

//    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String addUser(UserDTO userDTO) {
    	String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        UserLogin user = new UserLogin(userDTO.getUsername(), encryptedPassword);
        return userRepository.save(user).getUsername();
    }
    
    public List<UsernameDTO> getAllUsers() {
        List<UsernameDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> 
            users.add(new UsernameDTO(user.getId(), user.getUsername()))
        );
        return users;
    }
    
	public String getUsernameFromId(Long id) {
		return userRepository.findById(id).get().getUsername();
	}
}