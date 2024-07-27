package it.social.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageImageRequestDTO {

    @JsonProperty("post")
    private String message;

    @JsonProperty("image")
    private String image; // Base64 encoded image data

    public MessageImageRequestDTO() {}

	public MessageImageRequestDTO(String message, String image) {
        this.message = message;
        this.image = image;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
}
