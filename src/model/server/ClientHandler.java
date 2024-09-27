package model.server;

import model.manager.ChatManager;
import model.messages.Message;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClientHandler implements Runnable {
	private final ChatManager chatManager = ChatManager.getInstance();
	private final String username;
	private final PrintWriter writer;
	private final BufferedReader reader;

	public ClientHandler(String username, BufferedReader reader, PrintWriter writer) {
		this.username = username;
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	public void run() {
		receiveMessage();
	}

	public void receiveMessage() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				processMessage(message);
			}
		} catch (IOException e) {
			System.out.println("'" + this.username + "' se ha desconectado del chat.");
			chatManager.unregisterClient(this.username);
		}
	}

	private void processMessage(String message) {
		if (message.startsWith("/msg")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendMessageToAnotherClient(sender, instruction);
		} else if (message.equals("/getHistory")) {
			showHistory();
		} else if (message.startsWith("/createGroup")) {
			createGroup(message);
		} else if (message.startsWith("/joinGroup")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			joinGroup(sender, instruction);
		} else if (message.equals("/listGroups")) {
			listGroups();
		}
	}

	public void sendResponse(String message) {
		writer.println(message);
	}

	private void sendMessageToAnotherClient(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		String receiver = parts[1];
		String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
		if (!chatManager.clientExists(receiver)) {
			sendResponse("El usuario '" + receiver + "' no existe.");
		} else if (receiver.equals(sender)) {
			sendResponse("No puedes enviarte mensajes a ti mismo.");
		} else {
			ClientHandler receiverClientHandler = chatManager.getClient(receiver);
			receiverClientHandler.sendResponse(sender + " >>>  " + message);
			sendResponse("Mensaje enviado a '" + receiver + "'.");
			chatManager.saveMessage(sender, receiver, message);
		}
	}

	private void showHistory() {
		List<Message> messages = chatManager.getMessageHistory();
		for (Message savedMessage : messages) {
			sendResponse(savedMessage.toString());
		}
	}

	private void createGroup(String instruction) {
		String[] parts = instruction.split(" ");
		if (parts.length != 2) {
			sendResponse("Comando inválido. Use: /createGroup nombreDelGrupo");
			return;
		}
		String groupName = parts[1];
		if (chatManager.groupExists(groupName)) {
			sendResponse("El grupo '" + groupName + "' ya existe.");
		} else {
			chatManager.createGroup(groupName);
			sendResponse("Grupo '" + groupName + "' creado exitosamente.");
		}
	}

	private void joinGroup(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		if (parts.length != 2) {
			sendResponse("Comando inválido. Use: /joinGroup nombreDelGrupo");
			return;
		}
		String groupName = parts[1];
		if (chatManager.joinGroup(groupName, sender)) {
			sendResponse("Te has unido al grupo '" + groupName + "' exitosamente.");
		} else {
			sendResponse("No se pudo unir al grupo '" + groupName + "'. El grupo no existe.");
		}
	}

	private void listGroups() {
		Map<String, Set<String>> groupsInfo = chatManager.getGroupsWithMembers();
		if (groupsInfo.isEmpty()) {
			sendResponse("No hay grupos creados actualmente.");
		} else {
			StringBuilder response = new StringBuilder("Grupos existentes y sus miembros:\n");
			for (Map.Entry<String, Set<String>> entry : groupsInfo.entrySet()) {
				response.append("- ").append(entry.getKey()).append(":\n");
				if (entry.getValue().isEmpty()) {
					response.append("  (No hay miembros)\n");
				} else {
					for (String member : entry.getValue()) {
						response.append("  • ").append(member).append("\n");
					}
				}
			}
			sendResponse(response.toString());
		}
	}
}