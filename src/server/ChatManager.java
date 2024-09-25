package server;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
	private static ChatManager instance;
	private final BigInteger messagesID = BigInteger.ZERO;
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
				messagesID.add(BigInteger.valueOf(1)),
				newMessage
		);
	}
}
