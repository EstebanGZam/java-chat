package model.manager;

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
		if (instance == null) instance = new ChatManager();
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
				messagesID = messagesID.add(BigInteger.ONE),  // Incremento el ID antes de guardar
				newMessage
		);
	}

	public void unregisterClient(String username) {
		this.clients.remove(username);
	}
}
