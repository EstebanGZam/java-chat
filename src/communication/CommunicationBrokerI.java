package communication;

import java.io.IOException;

public interface CommunicationBrokerI {
	// Register a client with its unique ID and socket
	String registerClient(String clientId) throws IOException;

	// Receive messages from the server
	String receiveMessage() throws IOException;

	// Process instruction
	void processInstruction(String sourceUser, String instruction);

	// Send message to another client
	void sendMessageToAnotherClient(String instruction);

	// Show message history
	void showHistory(String instruction) throws IOException;
}

