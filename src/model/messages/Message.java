package model.messages;

import model.audio.Audio;

import java.time.LocalDateTime;

public class Message {
	private final String sender;
	private final String receiver;
	private final LocalDateTime sentDate;
	private final MessageType type;
	private String textContent = null;
	private Audio audio = null;

	// Constructor for text messages
	public Message(String sender, String receiver, String textContent) {
		this.sentDate = LocalDateTime.now();
		this.sender = sender;
		this.receiver = receiver;
		this.type = MessageType.TEXT;
		this.textContent = textContent;
	}

	// Constructor for audio messages
	public Message(String sender, String receiver, Audio audio) {
		this.sentDate = LocalDateTime.now();
		this.sender = sender;
		this.receiver = receiver;
		this.type = MessageType.AUDIO;
		this.audio = audio;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("De: ").append(sender)
				.append(", A: ").append(receiver)
				.append(", Fecha: ").append(sentDate)
				.append(", Tipo: ").append(type);

		if (type == MessageType.TEXT) {
			sb.append(", Mensaje: ").append(textContent != null ? textContent : "[sin contenido]");
		} else if (type == MessageType.AUDIO) {
			sb.append(", Audio: ").append(audio != null ? audio.toString() : "[sin audio]");
		}

		return sb.toString();
	}
}
