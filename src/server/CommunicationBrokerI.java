package server;

import java.io.IOException;

public interface CommunicationBrokerI {
	// Registrar un cliente con su ID único y socket
	String registerClient(String clientId) throws IOException;

	// Enviar mensaje a otro cliente
	void sendMessage(String fromClientId, String toClientId, String message);
}

