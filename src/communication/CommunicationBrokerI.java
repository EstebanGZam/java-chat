package communication;

import model.audio.AudioReceiver;
import model.audio.AudioSender;

import java.io.*;
import java.net.Socket;

import static model.client.Client.RECEIVED_AUDIO_PATH;

public class CommunicationBrokerI implements CommunicationBroker {
	private final Socket clientSocket;
	private final BufferedReader socketReader;
	private final PrintWriter writer;

	public CommunicationBrokerI(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
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
		// Leer el encabezado primero para identificar el tipo de mensaje
		String header = socketReader.readLine();

		if ("TEXT".equals(header)) {
			// Recibir y procesar el texto
			return socketReader.readLine();
		} else if ("AUDIO".equals(header)) {
			// Recibir y procesar el audio
			receiveAudio();
			return "Audio ";
		} else {
			// Manejar casos donde el tipo de mensaje no es reconocido
			return "Tipo de mensaje no reconocido.";
		}
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
		} else if (instruction.startsWith("/createGroup")) {
			createGroup(instruction);
		} else if (instruction.startsWith("/joinGroup")) {
			joinGroup(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/listGroups")) {
			listGroups(instruction);
		} else if (instruction.startsWith("/groupMsg")) {
			sendGroupMessage(instruction);
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
		writer.println("TEXT"); // Enviar encabezado indicando que es un mensaje de texto
		writer.println(instruction); // Enviar el mensaje de texto
	}

	@Override
	public void sendAudio(String sourceUser, String targetUser, File audioFile) throws IOException {
		writer.println("AUDIO"); // Enviar encabezado indicando que es audio
		writer.println(sourceUser + ":::" + targetUser); // Enviar el nombre del usuario
		String audioFileName = audioFile.getName();

		System.out.println(audioFileName);

		writer.println(audioFileName); // Enviar el nombre del archivo de audio

		AudioSender audioSender = new AudioSender();
		audioSender.sendAudio(writer, audioFile);
	}

	// Method to receive an audio file
	private void receiveAudio() {
		try {
			String sourceUser = socketReader.readLine();
			String audioFileName = socketReader.readLine();
			// Para leer audio o datos binarios
			AudioReceiver audioReceiver = new AudioReceiver();
			File audioFile = audioReceiver.receiveAudio(audioFileName, RECEIVED_AUDIO_PATH, socketReader);
			if (audioFile != null) {
				System.out.println("Audio recibido de '" + sourceUser + "'. Para reproducirlo, escriba el comando: '/play " + audioFileName + "'");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createGroup(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void listGroups(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void joinGroup(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void sendGroupMessage(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void closeConnection() throws IOException {
		writer.close();
		socketReader.close();
		clientSocket.close();
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
		writer.println("TEXT"); // Enviar encabezado indicando que es un mensaje de texto
		writer.println(historialRequest); // Enviar el historial
	}
}