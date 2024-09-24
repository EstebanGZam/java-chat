package server;

import java.time.LocalDateTime;

public class Message {
	private final String sender;
	private final String receiver;
	private final LocalDateTime sentDate;
	private final MessageType type;
	private String textContent = null;
	private Audio audio = null;

	// Constructor for all types of messages (generic)
	public Message(String sender, String receiver, String textContent) {
		this.sentDate = LocalDateTime.now();
		this.sender = sender;
		this.receiver = receiver;
		this.type = MessageType.TEXT;
		this.textContent = textContent;
	}

	public Message(String sender, String receiver, Audio audio) {
		this.sentDate = LocalDateTime.now();
		this.sender = sender;
		this.receiver = receiver;
		this.type = MessageType.AUDIO;
		this.audio = audio;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public LocalDateTime getSentDate() {
		return sentDate;
	}

	public MessageType getType() {
		return type;
	}

	public String getTextContent() {
		return textContent;
	}

	public Audio getAudio() {
		return audio;
	}
}
