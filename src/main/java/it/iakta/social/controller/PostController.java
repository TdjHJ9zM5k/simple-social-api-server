package it.iakta.social.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.iakta.social.dto.message.MessageListResponseDTO;
import it.iakta.social.login.payload.MessageResponse;
import it.iakta.social.security.service.UserDetailsImpl;
import it.iakta.social.service.MessageService;

@RestController
@RequestMapping("/api/post")
public class PostController {
	
	@Autowired
	MessageService messageService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addPost(
	        @AuthenticationPrincipal UserDetailsImpl userDetails,
	        @RequestBody String message
	        ) { 
	    if (message == null) { 
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Message cannot be null!"));
	    }
	    if (message.length() > 255) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Message is too long!"));
	    }
	    return ResponseEntity.ok(messageService.addMessage(userDetails.getId(), message));
	}
	
	@PostMapping("/{post_id}/comment/add")
	public ResponseEntity<?> addComment(
	        @AuthenticationPrincipal UserDetailsImpl userDetails,
	        @PathVariable(value = "post_id") Long messageId,
	        @RequestBody String comment
	        ) {
	    if (comment == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Comment cannot be null!"));
	    }
	    if (comment.length() > 255) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Comment is too long!"));
	    }
	    if (!messageService.doesMessageExist(messageId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Post does not exist!"));
	    }

	    return ResponseEntity.ok(messageService.addComment(userDetails.getId(), messageId, comment));
	}
	
	
	@PostMapping("/{post_id}/comment/delete/{comment_id}")
	public ResponseEntity<?> deleteComment(
	        @AuthenticationPrincipal UserDetailsImpl userDetails,
	        @PathVariable(value = "post_id") Long messageId,
	        @PathVariable(value = "comment_id") Long commentId
	        ) {

	    if (!messageService.doesCommentExist(commentId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Comment does not exist!"));
	    }
	    if (!messageService.doesCommentExistforMessage(messageId, commentId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Comment does not exist for this post!"));
	    }
		if (!messageService.isCommentOwner(userDetails.getId(), commentId)) {
		    return ResponseEntity.status(HttpStatus.FORBIDDEN)
		                .body(new MessageResponse("Error: You are not the owner of this comment!"));
		}
	    if (!messageService.doesMessageExist(messageId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Post does not exist!"));
	    }
	    
	    messageService.deleteComment(commentId);
	    return ResponseEntity.ok("Comment deleted successfully.");
	}
	
	@GetMapping("/list-all-posts")
	public ResponseEntity<?> getAllPosts(
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			@RequestParam(value = "max_results", required = false) Integer maxResults
			) {
		List<MessageListResponseDTO> messages = messageService.getUsersAndFollowingMessages(userDetails.getId(), null);
		if (maxResults != null && maxResults < messages.size()) {
            messages = messages.subList(0, maxResults);
        }
		return ResponseEntity.ok(messages);
	}
	
	@GetMapping("post-details/{post_id}")
	public ResponseEntity<?> getPostDetails(
			@PathVariable(value = "post_id", required = true)  Long messageId) {
		if (!messageService.doesMessageExist(messageId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Error: Post does not exist!"));
	    }
		return ResponseEntity.ok(messageService.getMessageDetails(messageId));
	}
}
