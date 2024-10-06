package model.manager;

import model.group.Group;
import model.audio.Audio;
import model.messages.Message;
import model.persistence.MessagePersistence;
import model.server.ClientHandler;
import model.calls.Call;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class ChatManager {
	private static ChatManager instance;
	private BigInteger messagesID = BigInteger.ZERO;
	private final Map<String, ClientHandler> clients = new HashMap<>();
	private final Map<BigInteger, Message> messageHistory = new HashMap<>();
	private final Map<String, Group> groups = new HashMap<>();
	private final Map<String, Call> calls = new HashMap<>();
	public static final String AUDIOS_FOLDER = "./resources/server/audio/";

	/**
	 * Returns the single instance of the {@link ChatManager}.
	 * <p>
	 * The instance is created only once, at the first call of this method.
	 *
	 * @return the single instance of the {@link ChatManager}
	 */
	public static ChatManager getInstance() {
		if (instance == null)
			instance = new ChatManager();
		return instance;
	}

	/**
	 * Registers a client with its unique ID and handler.
	 * <p>
	 * This method associates a client with its unique ID and a handler that will be
	 * used to communicate with it.
	 * <p>
	 * This method does not throw any checked exceptions.
	 *
	 * @param username      the unique ID of the client to register
	 * @param clientHandler the handler that will be used to communicate with the
	 *                      client
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
	 * The message is associated with a unique identifier that is incremented for
	 * each message.
	 *
	 * @param sender   the username of the user who sent the message
	 * @param receiver the username of the user who received the message
	 * @param message  the content of the message
	 */
	public void saveMessage(String sender, String receiver, String message) {
		Message newMessage = new Message(sender, receiver, message);
		messageHistory.put(
				messagesID = messagesID.add(BigInteger.ONE), // Increment the ID before saving
				newMessage);

		MessagePersistence.saveMessage(sender, receiver, message);
	}

	/**
	 * Unregisters a client with the given username.
	 * <p>
	 * After this method is called, the client with the given username will be
	 * removed from the chat manager.
	 *
	 * @param username the username of the client to unregister
	 */
	public void unregisterClient(String username) {
		this.clients.remove(username);

		for (Group group : groups.values()) {
			if (group.isMember(username)) {
				group.removeMember(username);
			}
		}
	}

	/**
	 * Returns a list of the messages in the order they were saved.
	 * <p>
	 * This method returns a copy of the messages stored in the chat manager.
	 * The messages are ordered by the time they were saved, with the most recent
	 * messages last.
	 *
	 * @return a list of the messages in the order they were saved
	 */
	public List<Message> getMessageHistory() {
		// Return a list of saved messages in order
		return new ArrayList<>(messageHistory.values());
	}

	public void createGroup(String groupName) {
		if (!groups.containsKey(groupName)) {
			groups.put(groupName, new Group(groupName));
		}
	}

	public boolean groupExists(String groupName) {
		return groups.containsKey(groupName);
	}

	public Map<String, Set<String>> getGroupsWithMembers() {
		Map<String, Set<String>> groupsInfo = new HashMap<>();
		for (Map.Entry<String, Group> entry : groups.entrySet()) {
			groupsInfo.put(entry.getKey(), entry.getValue().getMembers());
		}
		return groupsInfo;
	}

	public Group getGroup(String groupName) {
		return groups.get(groupName);
	}

	public boolean joinGroup(String groupName, String username) {
		if (groupExists(groupName)) {
			Group group = groups.get(groupName);
			group.addMember(username);
			return true;
		}
		return false;
	}

	/**
	 * Sends a message to all members of a specified group.
	 * If the group exists, the message is broadcasted to each member that is
	 * currently connected.
	 *
	 * @param groupName The name of the group to which the message will be sent.
	 * @param sender    The username of the client sending the message.
	 * @param message   The message content to be sent to the group members.
	 */
	public void sendGroupMessage(String groupName, String sender, String message) {
		if (groups.containsKey(groupName)) {
			Group group = groups.get(groupName);
			for (String member : group.getMembers()) {
				if (clients.containsKey(member)) {
					clients.get(member).sendTextResponse(sender + " (en grupo " + groupName + "): " + message);
				}
			}
		}
		saveMessage(sender, groupName, message);
	}

	public boolean isUserInGroup(String username, String groupName) {
		if (groups.containsKey(groupName)) {
			Group group = groups.get(groupName);
			return group.isMember(username);
		}
		return false;
	}

	public void saveAudio(String sender, String receiver, Audio audio) {
		Message newMessage = new Message(sender, receiver, audio);
		messageHistory.put(
				messagesID = messagesID.add(BigInteger.ONE), // Incremento el ID antes de guardar
				newMessage);
	}

	public String addCall(Call call) {
		SecureRandom secureRandom = new SecureRandom();
		String callID = "call-" + (10000 + secureRandom.nextInt(90000));
		calls.put(callID, call);
		return callID;
	}

	public boolean callExists(String callID) {
		return calls.containsKey(callID);
	}

	public Call getCall(String callID) {
		return calls.get(callID);
	}

	public void removeCall(String callID) {
		calls.remove(callID);
	}
}