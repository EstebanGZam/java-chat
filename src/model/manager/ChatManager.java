package model.manager;

import model.messages.Audio;
import model.messages.Message;
import model.server.ClientHandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager {
	private static ChatManager instance;
	private BigInteger messagesID = BigInteger.ZERO;
	private final Map<String, ClientHandler> clients = new HashMap<>();
	private final Map<BigInteger, Message> messageHistory = new HashMap<>();

	public static ChatManager getInstance() {
		if (instance == null)
			instance = new ChatManager();
		return instance;
	}

	public void registerClient(String username, ClientHandler clientHandler) {
		clients.put(username, clientHandler);
	}

	public boolean clientExists(String username) {
		return clients.containsKey(username);
	}

	public ClientHandler getClient(String username) {
		return clients.get(username);
	}

	public void saveMessage(String sender, String receiver, String message) {
		Message newMessage = new Message(sender, receiver, message);
		messageHistory.put(
				messagesID = messagesID.add(BigInteger.ONE), // Incremento el ID antes de guardar
				newMessage);
	}

	public void unregisterClient(String username) {
		this.clients.remove(username);
	}

	public List<Message> getMessageHistory() {
		// Retorna una lista de los mensajes en el orden en que fueron guardados
		return new ArrayList<>(messageHistory.values());
	}

	public void saveAudio(String sender, String receiver, Audio audio) {
		Message newMessage = new Message(sender, receiver, audio);
		messageHistory.put(
				messagesID = messagesID.add(BigInteger.ONE), // Incremento el ID antes de guardar
				newMessage);
	}

}
