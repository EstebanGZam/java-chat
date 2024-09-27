package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationBroker implements CommunicationBrokerI {
	// Lee la información que llega del socket
	private final BufferedReader socketReader;
	// Escribe información en el socket
	private final PrintWriter writer;


	public CommunicationBroker(Socket clientSocket) throws IOException {
		socketReader = initReader(clientSocket);
		writer = initWriter(clientSocket);
	}

	/**
	 * Initialize a BufferedReader that reads from the given clientSocket.
	 * <p>
	 * This method creates a BufferedReader that reads from the input stream of the given clientSocket.
	 * <p>
	 *
	 * @param clientSocket the socket to read from
	 * @return a BufferedReader that reads from the given clientSocket
	 * @throws IOException if an I/O error occurs while creating the BufferedReader
	 */
	private BufferedReader initReader(Socket clientSocket) throws IOException {
		return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	/**
	 * Initialize a PrintWriter that writes to the given clientSocket.
	 * <p>
	 * This method creates a PrintWriter that writes to the output stream of the given clientSocket.
	 * <p>
	 *
	 * @param clientSocket the socket to write to
	 * @return a PrintWriter that writes to the given clientSocket
	 * @throws IOException if an I/O error occurs while creating the PrintWriter
	 */
	private PrintWriter initWriter(Socket clientSocket) throws IOException {
		return new PrintWriter(clientSocket.getOutputStream(), true);
	}

	/**
	 * Sends the attempt of register of a client in the chat to the server.
	 * <p>
	 *
	 * @param username the username to register
	 * @return the response from the server, either a success message or an error message
	 * @throws IOException if an I/O error occurs while attempting to register the client
	 */
	@Override
	public String registerClient(String username) throws IOException {
		writer.println(username);
		return socketReader.readLine();
	}

	/**
	 * Receive a message from the server.
	 * <p>
	 * This method reads a line from the input stream of the socket and returns it as a string.
	 * <p>
	 *
	 * @return a message received from the server
	 * @throws IOException if an I/O error occurs while attempting to receive the message
	 */
	@Override
	public String receiveMessage() throws IOException {
		return socketReader.readLine();
	}

	/**
	 * Process an instruction from the user.
	 * <p>
	 * This method takes a string instruction from the user and processes it.
	 * If the instruction starts with "/msg", it is sent to another client.
	 * If the instruction is "/getHistory", the history of messages is shown.
	 * <p>
	 *
	 * @param sourceUser  the username of the user who sent the instruction
	 * @param instruction the instruction to process
	 */
	@Override
	public void processInstruction(String sourceUser, String instruction) {
		if (instruction.startsWith("/msg")) {
			sendMessageToAnotherClient(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/getHistory")) {
			showHistory(instruction);
		}
	}

	/**
	 * Sends a message to another client.
	 * <p>
	 * This method takes the instruction with the message as a string and sends it to another client.
	 * <p>
	 *
	 * @param instruction the message to send to another client
	 */
	@Override
	public void sendMessageToAnotherClient(String instruction) {
		writer.println(instruction);
	}

	/**
	 * Shows the history of messages sent in the chat.
	 * <p>
	 * This method takes a string instruction and sends it to the server for get the messages history.
	 * <p>
	 *
	 * @param historialRequest the instruction "/getHistory"
	 */
	@Override
	public void showHistory(String historialRequest) {
		writer.println(historialRequest);
	}

}
