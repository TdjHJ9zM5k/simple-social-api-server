package it.iakta.social.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponseDTO {
	
    @JsonProperty("post_id")
    private Long message_id;

    @JsonProperty("post")
    private String message;

    public MessageResponseDTO() {}

	public MessageResponseDTO(Long message_id, String message) {
		this.message_id = message_id;
		this.message = message;
	}
	
	public Long getMessage_id() {
		return message_id;
	}
	
	public String getMessage() {
		return message;
	}
}
