package it.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.social.entity.UserFollowing;
import it.social.login.payload.MessageResponse;
import it.social.security.service.UserDetailsImpl;
import it.social.service.MessageService;
import it.social.service.UserService;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Endpoints for user operations such as following, unfollowing, and listing users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Operation(summary = "Follow User", 
               description = "Follow a user by their ID. The endpoint requires the ID of the user to be followed. The authenticated user is retrieved from the security context.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully followed the user"),
        @ApiResponse(responseCode = "400", description = "Bad Request: User cannot be null or does not exist"),
        @ApiResponse(responseCode = "409", description = "Conflict: Cannot follow yourself or already following this user")
    })
    @PostMapping("/follow/{username_id}")
    public ResponseEntity<?> followUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "ID of the user to be followed", example = "123") @PathVariable(value = "username_id") Long followedUserId
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
        if (userService.doesUserFollow(userDetails.getId(), followedUserId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: You already follow this user!"));
        }
        UserFollowing followed = userService.followUser(userDetails.getId(), followedUserId);
        return ResponseEntity.ok(messageService.getUsersAndFollowingMessages(userDetails.getId(), followed.getUserFollowId()));
    }

    @Operation(summary = "Unfollow User", 
               description = "Unfollow a user by their ID. The endpoint requires the ID of the user to be unfollowed. The authenticated user is retrieved from the security context.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully unfollowed the user"),
        @ApiResponse(responseCode = "400", description = "Bad Request: User cannot be null or does not exist"),
        @ApiResponse(responseCode = "409", description = "Conflict: Cannot unfollow yourself or not following this user")
    })
    @PostMapping("/unfollow/{username_id}")
    public ResponseEntity<?> unfollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "ID of the user to be unfollowed", example = "123") @PathVariable(value = "username_id") Long followedUserId
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

    @Operation(summary = "List All Users", 
               description = "Retrieve a list of all users. This endpoint does not require any parameters.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all users")
    @GetMapping("/list-all-users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "List Followed Users", 
               description = "Retrieve a list of users that the authenticated user is following. The authenticated user is retrieved from the security context.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of followed users")
    @GetMapping("/list-followed-users")
    public ResponseEntity<?> getFollowedUsers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getFollowedUsers(userDetails.getId()));
    }
}
