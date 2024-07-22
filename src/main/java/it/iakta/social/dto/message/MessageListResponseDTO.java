package it.iakta.social.dto.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageListResponseDTO {
	
    @JsonProperty("post_id")
    private Long message_id;
    
    @JsonProperty("post")
    private String message;
    
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;
    
    @JsonProperty("author")
    private String username;
    
    public MessageListResponseDTO() {}
    
    public MessageListResponseDTO(Long message_id, String message, Date date, String username) {
    	        super();
    	        this.message_id = message_id;
    	        this.message = message;
	        	this.date = date;
	        	this.username = username;
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
    
}
