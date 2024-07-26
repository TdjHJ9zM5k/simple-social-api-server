package it.social.entity;

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
@Table(name = "messages")
public class Message {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private Long userId;
	
    @NotBlank
    @Size(max = 240)
    private String message;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public Message() {}
    
	public Message(Long userId, String message) {
		this.userId = userId;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}
	
	public String getMessage() {
		return message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
}
