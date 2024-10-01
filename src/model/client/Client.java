package model.client;

import communication.CommunicationBrokerI;
import communication.CommunicationBroker;
import model.audio.AudioPlayer;
import model.audio.AudioRecorder;
import model.server.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.SecureRandom;

public class Client {
	private String username;
	private Socket socket;
	// Entrada de información (por consola)
	private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	// Lector de entrada de consola
	private CommunicationBroker communicationBroker;
	private final AudioRecorder audioRecorder = new AudioRecorder();
	private final AudioPlayer audioPlayer = new AudioPlayer();

	public static final String RECORDED_AUDIO_PATH = "./resources/audio/recorded/";
	public static final String RECEIVED_AUDIO_PATH = "./resources/audio/received/";

	public static void main(String[] args) throws IOException {
		Client client = new Client();

		client.connectToServer();
		if (client.isConnected()) {
			client.createUsername();
			client.receiveMessages();
			client.displayInstructions();
			client.awaitAndProcessCommands();
		} else {
			System.out.println("\nNo se pudo establecer la conexión con el servidor.");
		}
	}

	/**
	 * Awaits user commands and processes them until the user enters "exit".
	 * <p>
	 * This method displays the console cursor, waits for the user to enter a command,
	 * processes the command using the {@link #processInstruction(String)} method,
	 * and repeats this process until the user enters the command "exit".
	 * <p>
	 * When the user enters "exit", the program is terminated using the
	 * {@link #closeProgram()} method.
	 *
	 * @throws IOException If an I/O error occurs while reading the user's input
	 */
	private void awaitAndProcessCommands() throws IOException {
		String instruction = "";
		while (!instruction.equals("exit")) {
			showConsoleCursor();
			instruction = reader.readLine();
			processInstruction(instruction);
		}
		closeProgram();
	}

	/***
	 * Displays chat usage instructions in the user console.
	 * <p>
	 * A menu with usage instructions is displayed in the user console.
	 */
	public void displayInstructions() {
		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println("Para enviar un mensaje a todos, solo escribe el mensaje y presiona Enter.");
		System.out.println("Para crear un nuevo grupo, escribe: /createGroup <nombre_del_grupo>");
		System.out.println("Para ver la lista de grupos existentes y sus miembros, escribe: /listGroups");
		System.out.println("Para unirte a un grupo existente, escribe: /joinGroup <nombre_del_grupo>");
		System.out.println("Para enviar un mensaje privado a otro cliente, escribe: /msg <usuario_destino> <mensaje>");
		System.out.println("Para enviar un mensaje al grupo del cual es miembro, escribe /groupMsg  <nombre_del_grupo> <mensaje>");
		System.out.println("Para grabar un mensaje de audio que se enviará a otro cliente, escribe: /record <usuario_destino>");
		System.out.println("Para detener la grabación de audio, escribe: /stop-audio");
//		System.out.println("Para enviar un mensaje de audio, escribe: /send-audio <nombre_audio> <usuario_destino>");
		System.out.println("Para reproducir un mensaje de audio, escribe: /play <nombre_audio>");
		System.out.println("Para salir ver el historial de mensajes, escribe: /msgHistory");
		System.out.println("Para salir del chat, escribe: exit");
		System.out.println(
				"----------------------------------------------------------------------------------------------");
	}

	/**
	 * Establishes the connection to the server, trying up to 20 times in case of failure.
	 * If a {@link IOException} exception occurs while attempting the connection, wait 1 second
	 * before retrying.
	 * <p>
	 * If a {@link InterruptedException} interruption occurs while waiting, the connection
	 * process is interrupted.
	 */
	private void connectToServer() {
		System.out.println("Conectando con el servidor");
		int retryDelay = 1000, retryCount = 0, maxRetries = 20;

		while (!isConnected() && retryCount < maxRetries) {
			try {
				initializeConnection();
			} catch (IOException ioException) {
				retryCount++;
				System.out.print(".");
				try {
					Thread.sleep(retryDelay); // Espera de 1 segundo antes de reintentar
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // Manejar la interrupción correctamente
					System.out.println("\nProceso de conexión interrumpido.");
					return;
				}
			}
		}
	}

	/**
	 * Establishes the connection with the server and creates a {@link CommunicationBrokerI} object
	 * to handle communication with the server.
	 * <p>
	 * Throws a {@link IOException} exception if an error occurs while attempting to connect.
	 *
	 * @throws IOException If an error occurs while attempting to connect
	 */
	private void initializeConnection() throws IOException {
		this.socket = new Socket(Server.IP, Server.PORT);
		communicationBroker = new CommunicationBrokerI(this.socket);
		System.out.println("\nConexión exitosa!");
	}

	/**
	 * Returns whether the client is currently connected to the server.
	 * <p>
	 * This method checks whether the client's socket is not null and is connected.
	 *
	 * @return true if the client is connected, false otherwise
	 */
	private boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	/**
	 * Creates a username for the client and registers it on the server.
	 * <p>
	 * Prompts the user to enter a username via the console and registers it on the server.
	 * If the username is already in use, an error message is displayed to the user, and they are prompted again.
	 * If registration is successful, the username is saved in the {@link #username} field of the class.
	 */
	private void createUsername() {
		boolean registered = false;
		String username;
		while (!registered) {
			String response = "";
			try {
				System.out.print("\nIntroduce tu nombre de usuario: ");
				username = reader.readLine();
				if (username.split(" ").length > 1) {
					System.out.println("El nombre de usuario no debe contener espacios.");
				} else {
					response = communicationBroker.registerClient(username);
				}
			} catch (IOException e) {
				System.out.println("\n" + "Error al intentar registrar el usuario.");
				return;
			}
			if (!response.isEmpty()) {
				System.out.println(response);
				if (response.startsWith("Bienvenido")) {
					this.username = username;
					registered = true;
				}
			}
		}
	}

	/**
	 * Closes the program, closing the socket and the console reader.
	 * <p>
	 * If an error occurs while trying to close the socket or the reader, an error message is displayed.
	 * The {@link System#exit(int)} method is then called to terminate the program.
	 */
	private void closeProgram() {
		try {
			reader.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar el programa.");
		}
		System.exit(0);
	}

	/**
	 * Displays the console cursor on the screen, showing the current username and the character " >>>  " to indicate that a command can be entered.
	 */
	private void showConsoleCursor() {
		System.out.print(username + " >>>  ");
	}

	/**
	 * Processes a command or message entered by the user.
	 * <p>
	 * This method forwards the instruction to the {@link CommunicationBrokerI} to be processed.
	 * The instruction is prefixed with the username of the client.
	 *
	 * @param instruction the instruction or message to be processed
	 */
	public void processInstruction(String instruction) {
		String audioName = "";
		if (instruction.startsWith("/record")) {
			String username = instruction.split(" ")[1];
			SecureRandom secureRandom = new SecureRandom();
			audioName = "aud-from-" + username + "-" + (10000 + secureRandom.nextInt(90000));
			audioRecorder.startRecording(audioName);
		} else if (instruction.startsWith("/stop-audio")) {
			if (!audioRecorder.isRecording()) {
				System.out.println("No hay audio en reproducción.");
			} else {
				audioRecorder.stopRecording();
				sendAudio(username, audioName);
			}
		} else if (instruction.startsWith("/play")) {
			audioName = instruction.split(" ")[1];
			try {
				audioPlayer.playAudio(audioName);
			} catch (Exception ignored) {
				System.out.println("Error al reproducir el audio.");
			}
		} else {
			communicationBroker.processInstruction(this.username, instruction);
		}
	}

	private void sendAudio(String targetUser, String audioName) {
		if (audioPlayer.audioExists(audioName)) {
			File audioFile = audioPlayer.searchAudio(audioName);
			communicationBroker.sendAudio(this.username, targetUser, audioFile);
		}
	}

	/**
	 * Starts a thread that is responsible for receiving messages from the server and displaying them on the console.
	 * <p>
	 * If an error occurs while receiving messages, an error message is displayed on the console and the program closes.
	 */
	private void receiveMessages() {
		Thread receiver = new Thread(() -> {
			boolean running = true;
			while (running) {
				try {
					String message = communicationBroker.receiveMessage();
					System.out.println(message);
					showConsoleCursor();
				} catch (IOException e) {
					System.out.println("Error: Ocurrió una desconexión con el servidor.");
					running = false;
					closeProgram();
				}
			}
		});
		receiver.start();
	}
}

