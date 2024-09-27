package communication;

import java.io.IOException;

public interface CommunicationBrokerI {
	// Registrar un cliente con su ID único y socket
	String registerClient(String clientId) throws IOException;

	// Recibir mensajes del servidor
	String receiveMessage() throws IOException;

	// Procesar instrucción
	void processInstruction(String sourceUser, String instruction);

	// Enviar mensaje a otro cliente
	void sendMessageToAnotherClient(String instruction);

	// Mostrar el historial de mensajes
	void showHistory(String instruction) throws IOException;
}
