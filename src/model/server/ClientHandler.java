package model.server;

import model.audio.AudioReceiver;
import model.audio.AudioSender;
import model.manager.ChatManager;
import model.messages.Message;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClientHandler implements Runnable {

	/**
	 * Singleton instance of ChatManager that handles the core chat logic.
	 */
	private final ChatManager chatManager = ChatManager.getInstance();

	/**
	 * Username of the connected client.
	 */
	private final String username;

	/**
	 * Output stream to send messages to the client.
	 */
	private final PrintWriter writer;

	/**
	 * Input stream to receive messages from the client.
	 */
	private final BufferedReader reader;

	private final Socket clientSocket;

	public ClientHandler(String username, Socket clientSocket, BufferedReader reader, PrintWriter writer) {
		this.username = username;
		this.clientSocket = clientSocket;
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	public void run() {
		receiveMessage();
	}

	public void receiveMessage() {
		String header;
		boolean receiving = true;
		while (receiving) {
			try {
				header = reader.readLine();
				if (header == null) {
					// Esto indica que el flujo se ha cerrado
					throw new IOException("Flujo cerrado");
				}
				if (header.equals("TEXT")) {
					String message = reader.readLine();
					processTextMessage(message.trim());
				} else if (header.equals("AUDIO")) {
					String clientsInCommunication = reader.readLine();
					String[] communicationParts = clientsInCommunication.split(":::");
					String sourceUser = communicationParts[0];
					String targetUser = communicationParts[1];
					String audioName = reader.readLine();
					File audio = receiveAudio(audioName);
					sendAudio(sourceUser, targetUser, audio);
				}
			} catch (IOException e) {
				System.out.println("'" + this.username + "' se ha desconectado del chat.");
				chatManager.unregisterClient(this.username);
				receiving = false;
			}
		}
	}

	private void sendAudio(String sourceUser, String targetUser, File audioFile) {
		try {
			ClientHandler targetClient = chatManager.getClient(targetUser);
			Socket targetSocket = targetClient.getClientSocket();

			targetClient.getWriter().println("AUDIO");
			targetClient.getWriter().println(sourceUser);
			targetClient.getWriter().println(audioFile.getName());
			targetClient.getWriter().flush();

			AudioSender audioSender = new AudioSender();
			audioSender.sendAudio(targetSocket, audioFile);
		} catch (IOException e) {
			System.out.println("Error al reenviar el archivo de audio: " + e.getMessage());
		}
	}

	private File receiveAudio(String audioName) {
		File audioFile = null;
		try {
			AudioReceiver audioReceiver = new AudioReceiver();
			audioFile = audioReceiver.receiveAudio(audioName, ChatManager.AUDIOS_FOLDER, this.clientSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return audioFile;
	}

	private void processTextMessage(String message) {
		if (message.startsWith("/msg")) {
			// Private messaging
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendMessageToAnotherClient(sender, instruction);
		} else if (message.equals("/getHistory")) {
			showHistory();
		} else if (message.startsWith("/createGroup")) {
			createGroup(message);
		} else if (message.startsWith("/joinGroup")) {
			// Join a group
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			joinGroup(sender, instruction);
		} else if (message.startsWith("/groupMsg")) {
			// Send a message to a specific group
			String[] parts = message.split(" ", 3);
			String groupName = parts[1];
			String groupMessage = parts[2];
			sendGroupMessage(groupName, groupMessage);
		} else if (message.equals("/listGroups")) {
			listGroups();
		}
	}

	/**
	 * Sends a message to a specific group, but only if the user is a member of that group.
	 *
	 * @param groupName The name of the group.
	 * @param message   The message to be sent to the group.
	 */
	private void sendGroupMessage(String groupName, String message) {
		if (chatManager.isUserInGroup(username, groupName)) {
			chatManager.sendGroupMessage(groupName, username, message);
	
			
			chatManager.saveMessage(username, groupName, message);
	
		} else {
			sendTextResponse("No eres miembro del grupo '" + groupName + "'. Únete al grupo antes de enviar mensajes.");
		}
	}
	

	private void sendMessageToAnotherClient(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		String receiver = parts[1];
		String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
	
		if (!chatManager.clientExists(receiver)) {
			sendTextResponse("El usuario '" + receiver + "' no existe.");
		} else if (receiver.equals(sender)) {
			sendTextResponse("No puedes enviarte mensajes a ti mismo.");
		} else {
			ClientHandler receiverClientHandler = chatManager.getClient(receiver);
			receiverClientHandler.sendTextResponse(sender + " >>>  " + message);
			sendTextResponse("Mensaje enviado a '" + receiver + "'.");
	
			// Guardar el mensaje en el historial (archivo txt)
			  chatManager.saveMessage(sender, receiver, message);
		}
	}
	

	/**
	 * Shows the message history to the client.
	 */
	private void showHistory() {
		List<Message> messages = chatManager.getMessageHistory();
		for (Message savedMessage : messages) {
			sendTextResponse(savedMessage.toString());
		}
	}

	/**
	 * Creates a new group if the group name is valid and does not already exist.
	 *
	 * @param instruction The command received that contains the group name.
	 */
	private void createGroup(String instruction) {
		String[] parts = instruction.split(" ");
		if (parts.length != 2) {
			sendTextResponse("Comando inválido. Use: /createGroup <nombre_del_grupo>");
			return;
		}
		String groupName = parts[1];
		if (chatManager.groupExists(groupName)) {
			sendTextResponse("El grupo '" + groupName + "' ya existe.");
		} else {
			chatManager.createGroup(groupName);
			sendTextResponse("Grupo '" + groupName + "' creado exitosamente.");
		}
	}

	/**
	 * Allows a user to join an existing group if it exists.
	 *
	 * @param sender      The username of the client attempting to join the group.
	 * @param instruction The command received that contains the group name.
	 */
	private void joinGroup(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		if (parts.length != 2) {
			sendTextResponse("Comando inválido. Use: /joinGroup <nombre_del_grupo>");
			return;
		}
		String groupName = parts[1];
		if (chatManager.joinGroup(groupName, sender)) {
			sendTextResponse("Te has unido al grupo '" + groupName + "' exitosamente.");
		} else {
			sendTextResponse("No se pudo unir al grupo '" + groupName + "'. El grupo no existe.");
		}
	}

	/**
	 * Lists all existing groups and their members.
	 * Sends a list of created groups and the users in each group to the client.
	 */
	private void listGroups() {
		Map<String, Set<String>> groupsInfo = chatManager.getGroupsWithMembers();
		if (groupsInfo.isEmpty()) {
			sendTextResponse("No hay grupos creados actualmente.");
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
			sendTextResponse(response.toString());
		}
	}

	/**
	 * Sends a message to the client.
	 *
	 * @param message The message to send to the client.
	 */
	public void sendTextResponse(String message) {
		for (String line : message.split("\n")) {
			writer.println("TEXT");
			writer.println(line);
		}
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public PrintWriter getWriter() {
		return writer;
	}
}
