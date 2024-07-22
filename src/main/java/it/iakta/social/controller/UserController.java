package it.iakta.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.iakta.social.entity.UserFollowing;
import it.iakta.social.login.payload.MessageResponse;
import it.iakta.social.security.service.UserDetailsImpl;
import it.iakta.social.service.MessageService;
import it.iakta.social.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
    private UserService userService;
	
	@Autowired
	private MessageService messageService;
	
	@PostMapping("/follow/{username_id}")
	public ResponseEntity<?> followUser(
	        @AuthenticationPrincipal UserDetailsImpl userDetails,
	        @PathVariable(value = "username_id") Long followedUserId
	        ) {
	    if (followedUserId == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: User cannot be null!"));
	    }
	    if (!userService.existsById(followedUserId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: User does not exist!"));
	    }
	    if (followedUserId.equals(userDetails.getId())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(new MessageResponse("Error: You cannot follow yourself!"));
	    }
	    if(userService.doesUserFollow(userDetails.getId(), followedUserId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: You already follow this user!"));
	    }
	    UserFollowing followed = userService.followUser(userDetails.getId(), followedUserId);
	    return ResponseEntity.ok(messageService.getUsersAndFollowingMessages(userDetails.getId(), followed.getUserFollowId()));
	}

    @PostMapping("/unfollow/{username_id}")
    public ResponseEntity<?> unfollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable(value = "username_id") Long followedUserId
            ) {
        if (followedUserId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: User cannot be null!"));
        }
        if (!userService.existsById(followedUserId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: User does not exist!"));
        }
        if (followedUserId.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: You cannot unfollow yourself!"));
        }
        if (!userService.doesUserFollow(userDetails.getId(), followedUserId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: You are not following this user!"));
        }
        Long unfollowed = userService.unfollowUser(userDetails.getId(), followedUserId);
        return ResponseEntity.ok("You no longer follow user with id: " + unfollowed);
    }
    
    @GetMapping("/list-all-users")
    public  ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/list-followed-users")
	public ResponseEntity<?> getFollowedUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(userService.getFollowedUsers(userDetails.getId()));
	}
}
