package server;

import java.net.Socket;

public interface CommunicationBrokerI {
	// Registrar un cliente con su ID único y handler
	boolean registerClient(String clientId, Socket clientSocket);

	// Enviar mensaje a otro cliente
	void sendMessage(String fromClientId, String toClientId, String message);
}

