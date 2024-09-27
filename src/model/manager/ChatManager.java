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

	/**
	 * Returns the single instance of the {@link ChatManager}.
	 * <p>
	 * The instance is created only once, at the first call of this method.
	 *
	 * @return the single instance of the {@link ChatManager}
	 */
	public static ChatManager getInstance() {
		if (instance == null) instance = new ChatManager();
		return instance;
	}

	/**
	 * Registers a client with its unique ID and handler.
	 * <p>
	 * This method associates a client with its unique ID and a handler that will be used to communicate with it.
	 * <p>
	 * This method does not throw any checked exceptions.
	 *
	 * @param username      the unique ID of the client to register
	 * @param clientHandler the handler that will be used to communicate with the client
	 */
	public void registerClient(String username, ClientHandler clientHandler) {
		clients.put(username, clientHandler);
	}

	/**
	 * Checks whether a client with the given username exists.
	 *
	 * @param username the unique ID of the client to check
	 * @return true if the client exists, false otherwise
	 */
	public boolean clientExists(String username) {
		return clients.containsKey(username);
	}

	/**
	 * Returns the handler associated with the client with the given username.
	 * <p>
	 * If the client does not exist, this method returns null.
	 *
	 * @param username the unique ID of the client to retrieve
	 * @return the handler associated with the client if it exists, null otherwise
	 */
	public ClientHandler getClient(String username) {
		return clients.get(username);
	}

	/**
	 * Saves a message in the chat history.
	 * <p>
	 * The message is associated with a unique identifier that is incremented for each message.
	 *
	 * @param sender   the username of the user who sent the message
	 * @param receiver the username of the user who received the message
	 * @param message  the content of the message
	 */
	public void saveMessage(String sender, String receiver, String message) {
		Message newMessage = new Message(sender, receiver, message);
		messageHistory.put(
				messagesID = messagesID.add(BigInteger.ONE),  // Incremento el ID antes de guardar
				newMessage
		);
	}

	/**
	 * Unregisters a client with the given username.
	 * <p>
	 * After this method is called, the client with the given username will be removed from the chat manager.
	 *
	 * @param username the username of the client to unregister
	 */
	public void unregisterClient(String username) {
		this.clients.remove(username);
	}

	/**
	 * Returns a list of the messages in the order they were saved.
	 * <p>
	 * This method returns a copy of the messages stored in the chat manager.
	 * The messages are ordered by the time they were saved, with the most recent messages last.
	 *
	 * @return a list of the messages in the order they were saved
	 */
	public List<Message> getMessageHistory() {
		// Retorna una lista de los mensajes en el orden en que fueron guardados
		return new ArrayList<>(messageHistory.values());
	}

}
