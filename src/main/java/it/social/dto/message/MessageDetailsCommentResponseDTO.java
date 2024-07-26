package it.social.dto.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDetailsCommentResponseDTO {
	
    @JsonProperty("comment_id")
    private Long comment_id;

    @JsonProperty("comment")
    private String comment;
    
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;
    
    @JsonProperty("author")
    private String username;

    public MessageDetailsCommentResponseDTO() {}

	public MessageDetailsCommentResponseDTO(Long comment_id, String comment, Date date, String username) {
		super();
		this.comment_id = comment_id;
		this.comment = comment;
		this.date = date;
		this.username = username;
	}

	public Long getComment_id() {
		return comment_id;
	}

	public String getComment() {
		return comment;
	}

	public Date getDate() {
		return date;
	}

	public String getUsername() {
		return username;
	}
	
}
