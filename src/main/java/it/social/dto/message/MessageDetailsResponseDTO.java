package it.social.dto.message;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDetailsResponseDTO {
	
    @JsonProperty("post_id")
    private Long message_id;
    
    @JsonProperty("post")
    private String message;
    
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;
    
    @JsonProperty("author")
    private String username;
    
    @JsonProperty("comments")
    private List<MessageDetailsCommentResponseDTO> comments;
    
    public MessageDetailsResponseDTO() {}
    
	public MessageDetailsResponseDTO(Long message_id, String message, Date date, String username, List<MessageDetailsCommentResponseDTO> comments) {
    	        super();
    	        this.message_id = message_id;
    	        this.message = message;
            	this.date = date;
            	this.username = username;
            	this.comments = comments;
    }
	public Long getMessage_id() {
		return message_id;
	}

	public String getMessage() {
		return message;
	}

	public Date getDate() {
		return date;
	}

	public String getUsername() {
		return username;
	}

	public List<MessageDetailsCommentResponseDTO> getComments() {
		return comments;
	}

	public void setComments(List<MessageDetailsCommentResponseDTO> comments) {
		this.comments = comments;
	}
    
}
