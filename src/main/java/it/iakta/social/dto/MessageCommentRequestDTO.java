package it.iakta.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageCommentRequestDTO {
	
    @JsonProperty("post_id")
    private Long message_id;
    
    @JsonProperty("comment")
    private String comment;

    public MessageCommentRequestDTO() {}

	public MessageCommentRequestDTO(Long message_id, String comment) {
		this.message_id = message_id;
		this.comment = comment;
	}
	
	public Long getMessage_id() {
		return message_id;
	}

	public String getComment() {
		return comment;
	}
	
}
