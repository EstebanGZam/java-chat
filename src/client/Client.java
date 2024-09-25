package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import server.CommunicationBroker;
import server.CommunicationBrokerI;
import server.TCPServer;

public class Client {
	private Socket socket;
	// Entrada de información (por consola)
	private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	// Lector de entrada de consola
	private CommunicationBrokerI communicationBroker;

	public static void main(String[] args) {
		Client client = new Client();

		client.connectToServer();
		if (client.isConnected()) {
			client.createUsername();
		} else {
			System.out.println("\nNo se pudo establecer la conexión con el servidor.");
		}

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
		this.socket = new Socket(TCPServer.IP, TCPServer.PORT);
		communicationBroker = new CommunicationBroker(this.socket);
		System.out.println("\nConexión exitosa.");
	}

	private boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	private void createUsername() {
		System.out.print("Introduce tu nombre de usuario:");
		boolean registered = false;
		String username;
		while (!registered) {
			try {
				username = reader.readLine();
			} catch (IOException e) {
				System.out.println("Error al leer el nombre de usuario.");
				return;
			}
			registered = communicationBroker.registerClient(username, this.socket);
			if (!registered) {
				System.out.println("\nEl nombre de usuario ya está en uso. Introduce otro:");
			}
		}
	}

}