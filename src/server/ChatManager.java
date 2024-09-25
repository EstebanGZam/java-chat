package server;

import java.io.*;
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

	public void sendTextMessage(String sender, String receiver, String message) throws IOException {
//		Socket socketClient = clients.get(receiver);
//		if (socketClient != null) {
//			messageHistory.put(
//					messagesID.add(BigInteger.valueOf(1)),
//					new Message(sender, receiver, message)
//			);
//
//			// Obtener el flujo de salida del socket del cliente destinatario
//			OutputStream outputStream = socketClient.getOutputStream();
//
//			// Crear un objeto PrintWriter para enviar el mensaje
//			PrintWriter writer = new PrintWriter(outputStream, true);
//
//			// Enviar el mensaje al cliente destinatario
//			writer.println("Mensaje de " + sender + ": " + message);
//		} else {
//			System.out.println("User " + receiver + " is not connected.");
//		}
	}

}
