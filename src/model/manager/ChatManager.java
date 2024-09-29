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
				messagesID = messagesID.add(BigInteger.ONE), // Increment the ID before saving
				newMessage);
	}

	public void unregisterClient(String username) {
		this.clients.remove(username);
	}

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
 * If the group exists, the message is broadcasted to each member that is currently connected.
 *
 * @param groupName The name of the group to which the message will be sent.
 * @param sender The username of the client sending the message.
 * @param message The message content to be sent to the group members.
 */
public void sendGroupMessage(String groupName, String sender, String message) {
   if (groups.containsKey(groupName)) {
        Group group = groups.get(groupName);
        for (String member : group.getMembers()) {
            if (clients.containsKey(member)) {
                clients.get(member).sendResponse(sender + " (en grupo " + groupName + "): " + message);
            }
        }
    }
}

public boolean isUserInGroup(String username, String groupName) {
    if (groups.containsKey(groupName)) {
        Group group = groups.get(groupName);
        return group.isMember(username);
    }
    return false;
}



}