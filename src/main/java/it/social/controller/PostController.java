package it.social.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.social.dto.message.MessageListResponseDTO;
import it.social.login.payload.MessageResponse;
import it.social.security.service.UserDetailsImpl;
import it.social.service.MessageService;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post", description = "Endpoints for post operations")
public class PostController {

    @Autowired
    MessageService messageService;

    @Operation(summary = "Add Post", 
	  description = "Add a new post by the authenticated user. The request body must contain the content of the post and optionally an image.")
	@ApiResponses(value = {
	@ApiResponse(responseCode = "200", description = "Successfully added the post"),
	@ApiResponse(responseCode = "400", description = "Bad Request: Message cannot be null or is too long, or image is invalid")
	})
    @PostMapping("/add")
    public ResponseEntity<?> addPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(value = "post", required = false) Optional<String> message,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        if ((message == null || !message.isPresent() || message.get().isEmpty()) && (image == null || image.isEmpty())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Message and image cannot be both null!"));
        }
        
        if (message.isPresent() && message.get().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Message is too long!"));
        }
        
        if (image != null && !image.isEmpty()) {
        	String contentType = image.getContentType();
            if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
                return ResponseEntity.badRequest().body("Error: Only PNG or JPEG files are allowed.");
            }
            try {
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                if (bufferedImage == null) {
                    return ResponseEntity.badRequest().body("Error: Uploaded file is not a valid image.");
                }
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Error: Could not process the image.");
            }
        }

        return ResponseEntity.ok(messageService.addMessage(userDetails.getId(), message.orElse(""), image));
    }

    @GetMapping("/image/{messageId}/{imageName}")
    public ResponseEntity<?> downloadImage(@PathVariable Long messageId, @PathVariable String imageName) {
        byte[] imageData;
		if (!messageService.doesImageExist(imageName)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Image does not exist.");
		}
		try {
			imageData = messageService.downloadImage(messageId, imageName);
			return ResponseEntity.status(HttpStatus.OK)
	                .contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
	                .body(imageData);
		} catch (IOException e) {
			return ResponseEntity.badRequest().body("Error: Could not process the image.");
		} catch (DataFormatException e) {
			return ResponseEntity.badRequest().body("Error: Could not process the image.");
		}
        
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
        
        maxResults = maxResults == null ? 15 : maxResults;
        if (maxResults != null && maxResults < messages.size()) {
            messages = messages.subList(0, maxResults);
        }
        return ResponseEntity.status(HttpStatus.OK).body(messages);
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
