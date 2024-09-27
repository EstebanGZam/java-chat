package model.server;

import model.manager.ChatManager;
import model.messages.Message;

import java.io.*;
import java.util.List;


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

	/**
	 * This method receives a message from the user and processes it.
	 * <p>
	 * The method is in an infinite loop, waiting for messages from the user.
	 * <p>
	 * If an I/O error occurs while receiving the message, the user is removed from the chat manager,
	 * and the loop is stopped.
	 */
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

	/**
	 * Processes a message from the user.
	 * <p>
	 * For now, the message can be either a message to send to another user or a command to show the message history.
	 * <p>
	 *
	 * @param message the message from the user
	 */
	private void processMessage(String message) {
		if (message.startsWith("/msg")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendMessageToAnotherClient(sender, instruction);
		} else if (message.equals("/getHistory")) {
			showHistory();
		}
	}

	/**
	 * Sends a response to the client with the given message.
	 */
	public void sendResponse(String message) {
		writer.println(message);
	}

	/**
	 * Sends a message to another client.
	 * <p>
	 * This method takes the instruction to send a message to another client and the sender's username.
	 * The instruction must start with "/msg" and have the format "/msg <username> <message>".
	 * <p>
	 * If the receiver does not exist in the chat manager, the method sends a response to the sender saying that the user does not exist.
	 * <p>
	 *
	 * @param sender      the username of the sender
	 * @param instruction the instruction to send a message to another client
	 */
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

	/**
	 * Shows the history of messages sent in the chat.
	 * <p>
	 * This method sends all the messages in the chat manager to the client.
	 * The messages are ordered by the time they were saved, with the most recent messages last.
	 * <p>
	 */
	private void showHistory() {
		List<Message> messages = chatManager.getMessageHistory();
		for (Message savedMessage : messages) {
			sendResponse(savedMessage.toString());
		}
	}

}