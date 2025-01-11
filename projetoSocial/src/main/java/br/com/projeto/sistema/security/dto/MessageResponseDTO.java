package br.com.projeto.sistema.security.dto;

public class MessageResponseDTO {
	private String message;
	
	public MessageResponseDTO(String message) {
		this.message = message;
	}

	//Get e Set
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}