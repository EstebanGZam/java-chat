package model.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import communication.CommunicationBroker;
import communication.CommunicationBrokerI;
import model.server.Server;

public class Client {
	private String username;
	private Socket socket;
	// Entrada de información (por consola)
	private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	// Lector de entrada de consola
	private CommunicationBrokerI communicationBroker;

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
			System.out.print(username + " >>>  ");
			instruction = reader.readLine();
			processInstruction(instruction);
		}
		closeProgram();
	}

	public void displayInstructions() {
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("Para enviar un mensaje a todos, solo escribe el mensaje y presiona Enter.");
		System.out.println("Para enviar un mensaje privado a otro cliente, escribe: /msg <usuario_destino> <mensaje>");
		System.out.println("Para salir del chat, escribe: 'exit'");
		System.out.println("----------------------------------------------------------------------------------------------");
	}

	public void processInstruction(String instruction) {
		communicationBroker.processInstruction(this.username, instruction);
	}

	private void receiveMessages() {
		Thread receiver = new Thread(() -> {
			while (true) {
				try {
					String message = communicationBroker.receiveMessage();
					System.out.println("\n" + message);
				} catch (IOException e) {
					System.out.println("Error: Ocurrió una desconexión con el servidor.");
					closeProgram();
				}
			}
		});
		receiver.start();
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
					Thread.sleep(retryDelay);  // Espera de 1 segundo antes de reintentar
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();  // Manejar la interrupción correctamente
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
		System.out.print("\nIntroduce tu nombre de usuario: ");
		boolean registered = false;
		String username;
		while (!registered) {
			String response;
			try {
				username = reader.readLine();
				response = communicationBroker.registerClient(username);
			} catch (IOException e) {
				System.out.println("\n" + "Error al intentar registrar el usuario.");
				return;
			}
			System.out.println("\n" + response);
			if (response.startsWith("Bienvenido")) {
				this.username = username;
				registered = true;
			}
		}
	}

}