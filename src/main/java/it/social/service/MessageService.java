package it.social.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.social.dto.comment.MessageCommentResponseDTO;
import it.social.dto.message.MessageDetailsCommentResponseDTO;
import it.social.dto.message.MessageDetailsResponseDTO;
import it.social.dto.message.MessageListResponseDTO;
import it.social.dto.message.MessageResponseDTO;
import it.social.dto.user.UsernameDTO;
import it.social.entity.Image;
import it.social.entity.Message;
import it.social.entity.MessageComment;
import it.social.repository.ImageRepository;
import it.social.repository.MessageCommentRepository;
import it.social.repository.MessageRepository;
import it.social.repository.UserLoginRepository;

@Service
public class MessageService {
	
	@Autowired
    private MessageRepository messageRepository;
	
	@Autowired
    private MessageCommentRepository messageCommentRepository;
	
	@Autowired
	private UserLoginRepository userRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private UserService userService;
	
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

	
	
	//ADD POST AND IMAGE
	public MessageResponseDTO addMessage(Long userId, String message, MultipartFile image) {
	    Message newMessage = new Message(userId, message);
	    newMessage = messageRepository.save(newMessage);
	    if (image != null && !image.isEmpty()) {
			uploadImage(newMessage.getId(), image);
			try {
				newMessage.setImageName(URLEncoder.encode(image.getOriginalFilename(), StandardCharsets.UTF_8.toString()));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageRepository.save(newMessage);
		}
	    return new MessageResponseDTO(newMessage.getId(), newMessage.getMessage());
	}

	public MessageCommentResponseDTO addComment(Long userId, Long messageId, String comment) {
		MessageComment newComment = messageCommentRepository.save(new MessageComment(messageId, userId, comment));
		return commentToDTO(newComment);
	}
	
	public List<MessageListResponseDTO> getUsersAndFollowingMessages(Long userId, Long followedUserId) {
		List<MessageListResponseDTO> messages = new ArrayList<>();
		if (followedUserId != null) {
			messages.addAll(getMessages(followedUserId));
		} else {
			List<UsernameDTO> followers = userService.getFollowedUsers(userId);
			messages.addAll(followers.stream().map(follower -> getMessages(follower.getId())).flatMap(List::stream).toList());
			messages.addAll(getMessages(userId));
		}
		messages.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
		return messages;
    }
	
	
	//RETRRIEVE POST AND COMMENTS
	public MessageDetailsResponseDTO getMessageDetails(Long messageId) {
		Message message = messageRepository.findById(messageId).get();
		List<MessageDetailsCommentResponseDTO> comments = messageCommentRepository.findAllByMessageId(messageId).stream()
				.map(comment -> messageDetailscommentToDTO(comment)).toList();
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant = message.getCreatedAt().atZone(zoneId).toInstant();
		String username = userRepository.findById(message.getUserId()).get().getUsername();
		return new MessageDetailsResponseDTO(message.getId(), message.getMessage(), Date.from(instant), username,
				comments);
	}
	
	//RETRIEVE POST IMAGE
	public byte[] downloadImage(Long messageId, String fileName) throws IOException, DataFormatException {
		Optional<Image> image = imageRepository.findByMessageIdAndName(messageId, fileName);
		if (image.isPresent()) {
			return image.get().getImage();
		} else {
			throw new ContextedRuntimeException("Image not found").addContextValue("fileName", fileName);
		}
	}
	
	
	 //SAVE IMAGE TO DATABASE
    private String uploadImage(Long messageId, MultipartFile imageFile) {
        byte[] imageBytes = null;
        String encodedFileName = null;
        try {
            // URL Encoding del nome del file
            encodedFileName = URLEncoder.encode(imageFile.getOriginalFilename(), StandardCharsets.UTF_8.toString());

            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
            if (bufferedImage == null) {
                logger.error("Error reading image: The image may be empty or invalid.");
                return "Error: Could not read the image.";
            }

            BufferedImage resizedImage = resizeImage(bufferedImage, 400, 400);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            imageBytes = baos.toByteArray();

            logger.info("Trying to save image: {}", imageFile.getContentType());
            Image imageToSave = new Image(messageId, encodedFileName, imageFile.getContentType(), imageBytes);
            imageRepository.save(imageToSave);

            logger.info("Image correctly saved: {}", encodedFileName);
            return "File uploaded successfully: " + encodedFileName;

        } catch (IOException e) {
            logger.error("Error: Could not process the image: ", e);
            return "Error: Could not process the image.";
        } catch (Exception e) {
            logger.error("Error: Unexpected error occurred: ", e);
            return "Error: Unexpected error occurred.";
        }
    }
	
	
	
	//SAVE IMAGE TO DATABASE
//	private String uploadImage(Long messageId, MultipartFile imageFile) throws IOException {
//		
//		byte[] imageBytes = null;
//        try {
//            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
//
//            BufferedImage resizedImage = resizeImage(bufferedImage, 400, 400);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(resizedImage, "jpg", baos);
//            imageBytes = baos.toByteArray();
//            Image imageToSave = new Image(messageId, imageFile.getOriginalFilename(), imageFile.getContentType(), imageBytes);
//            imageRepository.save(imageToSave);
//        } catch (IOException e) {
//        	return null;
////                return ResponseEntity.badRequest().body("Error: Could not process the image.");
//        }
//        return "file uploaded successfully : " + imageFile.getOriginalFilename();
//    }
	private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight);
    }
	//RETRIEVE ALL MESSAGES BY A USER
	private List<MessageListResponseDTO> getMessages(Long userId) {
		return messageRepository.findAllByUserId(userId).stream()
				.map(message -> messageToDTO(message)).toList();
	}
	
	
	//CONVERTS MESSAGE TO DTO AND TRUNCATES THE MESSAGE TO 100 CHARACTERS
	private MessageListResponseDTO messageToDTO(Message message) {
	    ZoneId zoneId = ZoneId.systemDefault();
	    Instant instant = message.getCreatedAt().atZone(zoneId).toInstant();
	    String username = userRepository.findById(message.getUserId()).get().getUsername();

	    return new MessageListResponseDTO(
	        message.getId(),
	        message.getMessage().substring(0, Math.min(message.getMessage().length(), 100)),
	        Date.from(instant),
	        username,
	        message.getImageName()
	    );
	}
	
	private MessageCommentResponseDTO commentToDTO(MessageComment comment) {
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant = comment.getCreatedAt().atZone(zoneId).toInstant();
		String username = userRepository.findById(comment.getUserId()).get().getUsername();
		return new MessageCommentResponseDTO(
				comment.getMessageId(), 
				comment.getId(), 
				comment.getComment(), 
				Date.from(instant),
				username
				);
	}
	
	private MessageDetailsCommentResponseDTO messageDetailscommentToDTO(MessageComment comment) {
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant = comment.getCreatedAt().atZone(zoneId).toInstant();
		String username = userRepository.findById(comment.getUserId()).get().getUsername();
		return new MessageDetailsCommentResponseDTO(
				comment.getId(), 
				comment.getComment(), 
				Date.from(instant),
				username
				);
	}
	
	public void deleteComment(Long commentId) {
        messageCommentRepository.deleteById(commentId);
    }
	
	public boolean doesMessageExist(Long messageId) {
		return messageRepository.existsById(messageId);
	}
	
	public boolean doesCommentExist(Long commentId) {
		return messageCommentRepository.existsById(commentId);
	}
	
	public boolean doesCommentExistforMessage(Long messageId, Long commentId) {
		return messageCommentRepository.findByMessageIdAndId(messageId,commentId).isPresent();
	}
    
	public boolean isCommentOwner(Long userId, Long commentId) {
		return messageCommentRepository.findById(commentId).get().getUserId().equals(userId);
	}
	
	public boolean doesImageExist(String imageName) {
		return imageRepository.existsByName(imageName);
	}
}