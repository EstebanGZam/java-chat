package model.manager;

import model.group.Group;
import model.messages.Message;
import model.server.ClientHandler;

import java.math.BigInteger;
import java.util.*;

public class ChatManager {
	private static ChatManager instance;
	private BigInteger messagesID = BigInteger.ZERO;
	private final Map<String, ClientHandler> clients = new HashMap<>();
	private final Map<BigInteger, Message> messageHistory = new HashMap<>();
	private final Map<String, Group> groups = new HashMap<>();

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

	public List<Message> getMessageHistory() {
		// Retorna una lista de los mensajes en el orden en que fueron guardados
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

	public boolean joinGroup(String groupName, String username) {
		if (groupExists(groupName)) {
			Group group = groups.get(groupName);
			group.addMember(username);
			return true;
		}
		return false;
	}
}