package communication;

import java.io.File;
import java.io.IOException;

public interface CommunicationBrokerI {
	/**
	 * Registers a client with a unique ID and socket.
	 *
	 * @param clientId Unique identifier for the client.
	 * @return Confirmation message or error if registration fails.
	 * @throws IOException If there's an issue with network communication.
	 */
	String registerClient(String clientId) throws IOException;


	/**
	 * Receives messages from the server.
	 *
	 * @return Received message.
	 * @throws IOException If there's an issue with network communication.
	 */
	String receiveMessage() throws IOException;


	/**
	 * Processes an instruction from a user.
	 *
	 * @param sourceUser  User who sent the instruction.
	 * @param instruction Command or message to process.
	 */
	void processInstruction(String sourceUser, String instruction);


	/**
	 * Sends a message to another client.
	 *
	 * @param instruction Message to send to another client.
	 */
	void sendMessageToAnotherClient(String instruction);


	/**
	 * Displays the message history.
	 *
	 * @param instruction Optional filter or command for history display.
	 * @throws IOException If there's an issue with network communication.
	 */
	void showHistory(String instruction) throws IOException;


	/**
	 * Creates a new group.
	 *
	 * @param instruction Group name or creation command.
	 */
	void createGroup(String instruction);


	/**
	 * Lists all available groups.
	 *
	 * @param instruction Optional filter or command for group listing.
	 */
	void listGroups(String instruction);


	/**
	 * Joins a client to an existing group.
	 *
	 * @param instruction Group name or join command.
	 */
	void joinGroup(String instruction);


	/**
	 * Sends a message to all members of a group.
	 *
	 * @param instruction Message and group identifier.
	 */
	void sendGroupMessage(String instruction);

	// Enviar audio a otro cliente
	void sendAudio(String sourceUser, String targetUser, File audioFile);

}

