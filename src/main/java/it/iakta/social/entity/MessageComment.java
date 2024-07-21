package it.iakta.social.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class MessageComment {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private Long messageId;

    @NotBlank
    private Long userId;
	
    @NotBlank
    @Size(max = 240)
    private String comment;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public MessageComment() {}
    
	public MessageComment(Long messageId, Long userId, String comment) {
		this.messageId = messageId;
		this.userId = userId;
		this.comment = comment;
	}

	public Long getId() {
		return id;
	}

	public Long getMessageId() {
		return messageId;
	}

	public Long getUserId() {
		return userId;
	}
	
	public String getComment() {
		return comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
}
