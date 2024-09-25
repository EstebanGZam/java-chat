package server;

import java.io.*;


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
		}
	}

	private void processMessage(String message) {
		if (message.startsWith("/msg")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendMessageToAnotherClient(sender, instruction);
		}
	}

	private void sendMessageToAnotherClient(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		String receiver = parts[1];
		String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
		if (!chatManager.clientExists(receiver)) {
			sendResponse("El usuario '" + receiver + "' no existe.");
		} else {
			ClientHandler receiverClientHandler = chatManager.getClient(receiver);
			receiverClientHandler.sendResponse(sender + ": " + message);
			sendResponse("Mensaje enviado a '" + receiver + "'.");
			chatManager.saveMessage(sender, receiver, message);
		}
	}

	public void sendResponse(String message) {
		writer.println(message);
	}

}