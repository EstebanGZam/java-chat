package model.client;

import communication.CommunicationBroker;
import communication.CommunicationBrokerI;
import model.audio.AudioPlayer;
import model.audio.AudioRecorder;
import model.server.Server;

import java.io.BufferedReader;
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
	private CommunicationBrokerI communicationBroker;
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

	private void awaitAndProcessCommands() throws IOException {
		String instruction = "";
		while (!instruction.equals("exit")) {
			showConsoleCursor();
			instruction = reader.readLine();
			processInstruction(instruction);
		}
		closeProgram();
	}

	public void displayInstructions() {
		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println("Para enviar un mensaje a todos, solo escribe el mensaje y presiona Enter.");
		System.out.println("Para enviar un mensaje privado a otro cliente, escribe: /msg <usuario_destino> <mensaje>");
		System.out.println("Para salir ver el historial de mensajes, escribe: /msgHistory");
		System.out.println("Para grabar un mensaje de audio que se enviará a otro cliente, escribe: /record <username_del_receptor>");
		System.out.println("Para detener la grabación de audio, escribe: /stop-audio");
		System.out.println("Para enviar un mensaje de audio, escribe: /send-audio <nombre_audio> <usuario_destino>");
		System.out.println("Para reproducir un mensaje de audio, escribe: /play <nombre_audio>");
		System.out.println("Para salir del chat, escribe: exit");
		System.out.println(
				"----------------------------------------------------------------------------------------------");
	}

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

	private void initializeConnection() throws IOException {
		this.socket = new Socket(Server.IP, Server.PORT);
		communicationBroker = new CommunicationBroker(this.socket);
		System.out.println("\nConexión exitosa!");
	}

	private boolean isConnected() {
		return socket != null && socket.isConnected();
	}

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

	private void closeProgram() {
		try {
			reader.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar el programa.");
		}
		System.exit(0);
	}

	private void showConsoleCursor() {
		System.out.print(username + " >>>  ");
	}

	public void processInstruction(String instruction) {
		if (instruction.startsWith("/record")) {
			String username = instruction.split(" ")[1];
			SecureRandom secureRandom = new SecureRandom();
			String audioName = "aud-from-" + username + "-" + (10000 + secureRandom.nextInt(90000));
			audioRecorder.startRecording(audioName);
		} else if (instruction.startsWith("/stop-audio")) {
			audioRecorder.stopRecording();
		} else if (instruction.startsWith("/play")) {
			String audioName = instruction.split(" ")[1];
			try {
				audioPlayer.playAudio(audioName);
			} catch (Exception ignored) {
				System.out.println("Error al reproducir el audio.");
			}
		} else {
			communicationBroker.processInstruction(this.username, instruction);
		}
	}

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