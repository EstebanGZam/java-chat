package client;

import server.TCPServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	private static Socket socket;
	// Lee la información que llega del socket
	private static BufferedReader socketReader;
	// Escribe información en el socket
	private static PrintWriter writer;
	// Entrada de información (por consola)
	private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {
		try {

			establishConnection();

			if (clientIsConnected()) {
				requestUsername();

				receiveMessages();

				sendMessage();

				reader.close();
				writer.close();
				socket.close();
			}


		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void establishConnection() throws InterruptedException {
		System.out.println("Intentando conectar al servidor...");
		int retryCount = 0;
		int maxRetries = 10;
		long waitTime = 1000; // Tiempo de espera en milisegundos

		while (!clientIsConnected() && retryCount < maxRetries) {
			try {
				initializeConnection();
			} catch (IOException e) {
				if (e instanceof UnknownHostException) {
					System.out.println("Error: Host desconocido.");
					retryCount = maxRetries;
				} else {
					retryCount++;
					System.out.print(".");
					Thread.sleep(waitTime);
				}
			}
		}
		System.out.println("\n" +
				(clientIsConnected() ?
						"Conexión establecida con el servidor." :
						"No se pudo establecer conexión con el servidor. Inténtelo más tarde."
				) + "\n"
		);
	}


	private static void requestUsername() throws IOException {
		String username = "";
		while (username.isEmpty()) {
			System.out.print("Introduce tu nombre de usuario: ");
			username = reader.readLine();
			writer.println(username);
			String response = socketReader.readLine();
			if (response != null && response.contains("El nombre de usuario ya está en uso."))
				username = "";
			System.out.println(response);
		}
	}

	private static void initializeConnection() throws IOException {
		socket = new Socket(TCPServer.IP, TCPServer.PORT);
		socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
	}

	private static boolean clientIsConnected() {
		return socket != null && socket.isConnected();
	}


	public synchronized static void receiveMessages() {
		new Thread(() -> {
			try {
				String message;
				while ((message = socketReader.readLine()) != null) {
					System.out.println(">> " + message);
				}
			} catch (IOException _) {
			}
		}).start();
	}

	public static void sendMessage() throws IOException {
		while (socket.isConnected()) {
			String message = socketReader.readLine();
			writer.println(message);
		}
	}

}