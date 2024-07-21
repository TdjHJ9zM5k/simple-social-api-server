package it.iakta.social.service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.iakta.social.dto.MessageCommentResponseDTO;
import it.iakta.social.dto.MessageListResponseDTO;
import it.iakta.social.dto.MessageResponseDTO;
import it.iakta.social.dto.UsernameDTO;
import it.iakta.social.entity.Message;
import it.iakta.social.entity.MessageComment;
import it.iakta.social.repository.MessageCommentRepository;
import it.iakta.social.repository.MessageRepository;
import it.iakta.social.repository.UserLoginRepository;

@Service
public class MessageService {
	
	@Autowired
    private MessageRepository messageRepository;
	
	@Autowired
    private MessageCommentRepository messageCommentRepository;
	
	@Autowired
	private UserLoginRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	public MessageResponseDTO addMessage(Long userId, String message) {
        Message newMessage = messageRepository.save( new Message(userId, message));
        return new MessageResponseDTO(newMessage.getId(), newMessage.getMessage());
	}
	
	public MessageCommentResponseDTO addComment(Long userId, Long messageId, String comment) {
		MessageComment newComment = messageCommentRepository.save(new MessageComment(userId, messageId, comment));
		return commentToDTO(newComment);
	}
	public List<MessageListResponseDTO> getUsersAndFollowingMessages(Long userId) {
		List<MessageListResponseDTO> messages = new ArrayList<>();
		List<UsernameDTO> followers = userService.getFollowedUsers(userId);
		messages.addAll(followers.stream().map(follower -> getMessages(follower.getId())).flatMap(List::stream).toList());
		messages.addAll(getMessages(userId));
		messages.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
		return messages;
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
				username);
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
	
	public void deleteComment(Long commentId) {
        messageCommentRepository.deleteById(commentId);
    }
	
	public boolean doesMessageExist(Long messageId) {
		return messageRepository.existsById(messageId);
	}
	
	public boolean doescommentExist(Long commentId) {
		return messageCommentRepository.existsById(commentId);
	}
	
	public boolean doescommentExistforMessage(Long messageId, Long commentId) {
		return messageCommentRepository.findByMessageIdAndId(messageId,commentId).isPresent();
	}
    
	public boolean isCommentOwner(Long userId, Long commentId) {
		return messageCommentRepository.findById(commentId).get().getUserId().equals(userId);
	}
	
}