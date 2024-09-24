package server;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
	private static ChatManager instance;
	private final BigInteger counter = BigInteger.ZERO;
	private final Map<String, Socket> clientMap = new HashMap<>();
	private final Map<BigInteger, Message> messageHistory = new HashMap<>();

	public static ChatManager getInstance() {
		if (instance == null) instance = new ChatManager();
		return instance;
	}

	public void registerClient(String username, Socket socketClient) {
		clientMap.put(username, socketClient);
	}

	public void sendTextMessage(String sender, String receiver, String message) throws IOException {
		Socket socketClient = clientMap.get(receiver);
		if (socketClient != null) {
			messageHistory.put(
					counter.add(BigInteger.valueOf(1)),
					new Message(sender, receiver, message)
			);

			// Obtener el flujo de salida del socket del cliente destinatario
			OutputStream outputStream = socketClient.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream, true);

			// Enviar el mensaje al cliente destinatario
			writer.println("Mensaje de " + sender + ": " + message);
		} else {
			System.out.println("User " + receiver + " is not connected.");
		}
	}

	public boolean clientExists(String username) {
		return clientMap.containsKey(username);
	}
}
