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
import org.springframework.web.bind.annotation.*;

import it.social.dto.message.MessageListResponseDTO;
import it.social.login.payload.MessageResponse;
import it.social.security.service.UserDetailsImpl;
import it.social.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post", description = "Endpoints for post operations")
public class PostController {

    @Autowired
    MessageService messageService;

    @Operation(summary = "Add Post", 
               description = "Add a new post by the authenticated user. The request body must contain the content of the post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added the post"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Message cannot be null or is too long")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "Content of the new post", example = "This is a new post.") @RequestBody String message
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

    @Operation(summary = "Add Comment", 
               description = "Add a comment to a post by the authenticated user. The request body must contain the comment content.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added the comment"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Comment cannot be null, is too long, or post does not exist"),
        @ApiResponse(responseCode = "403", description = "Forbidden: User is not the owner of the comment")
    })
    @PostMapping("/{post_id}/comment/add")
    public ResponseEntity<?> addComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "ID of the post to comment on", example = "123") @PathVariable(value = "post_id") Long messageId,
            @Parameter(description = "Content of the new comment", example = "This is a new comment.") @RequestBody String comment
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

    @Operation(summary = "Delete Comment", 
               description = "Delete a comment from a post by the authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the comment"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Comment or post does not exist"),
        @ApiResponse(responseCode = "403", description = "Forbidden: User is not the owner of the comment")
    })
    @PostMapping("/{post_id}/comment/delete/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "ID of the post from which the comment is to be deleted", example = "123") @PathVariable(value = "post_id") Long messageId,
            @Parameter(description = "ID of the comment to be deleted", example = "456") @PathVariable(value = "comment_id") Long commentId
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

    @Operation(summary = "List All Posts", 
               description = "Retrieve all posts made by the authenticated user and their followed users. Optionally limit the number of results.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of posts")
    })
    @GetMapping("/list-all-posts")
    public ResponseEntity<?> getAllPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "Maximum number of posts to retrieve. If not provided, all posts will be returned.", example = "10") @RequestParam(value = "max_results", required = false) Integer maxResults
    ) {
        List<MessageListResponseDTO> messages = messageService.getUsersAndFollowingMessages(userDetails.getId(), null);
        if (maxResults != null && maxResults < messages.size()) {
            messages = messages.subList(0, maxResults);
        }
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Post Details", 
               description = "Retrieve details of a specific post by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the post details"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Post does not exist")
    })
    @GetMapping("/post-details/{post_id}")
    public ResponseEntity<?> getPostDetails(
            @Parameter(description = "ID of the post to retrieve details for", example = "123") @PathVariable(value = "post_id") Long messageId
    ) {
        if (!messageService.doesMessageExist(messageId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Post does not exist!"));
        }
        return ResponseEntity.ok(messageService.getMessageDetails(messageId));
    }
}
