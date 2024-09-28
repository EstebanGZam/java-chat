package model.server;

import model.manager.ChatManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Server {
	private final ExecutorService pool;
	private final ChatManager chatManager;
	public static final String IP = "127.0.0.1";
	public static final int TEXT_PORT = 10000;
	public static final int AUDIO_PORT = 10001;

	private Server() {
		this.pool = java.util.concurrent.Executors.newFixedThreadPool(10);
		this.chatManager = ChatManager.getInstance();
	}

	public static void main(String[] args) {
		Server server = new Server();
		try {

			ServerSocket textServerSocket = new ServerSocket(TEXT_PORT);
			ServerSocket audioServerSocket = new ServerSocket(AUDIO_PORT);

			System.out.println("Servidor de texto iniciado en el puerto " + TEXT_PORT);
			System.out.println("Servidor de audio iniciado en el puerto " + AUDIO_PORT);

			while (!textServerSocket.isClosed() && !audioServerSocket.isClosed()) {
				// Aceptar conexión de cliente para texto
				Socket textClientSocket = server.acceptNewClient(textServerSocket);

				// Aceptar conexión de cliente para audio (relacionado al cliente de texto)
				Socket audioClientSocket = server.acceptNewClient(audioServerSocket);

				// Registrar cliente con ambos sockets (texto y audio)
				server.registerClient(textClientSocket, audioClientSocket);
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private Socket acceptNewClient(ServerSocket serverSocket) throws IOException {
		Socket socket = serverSocket.accept();
		System.out.println("Un nuevo cliente se ha conectado desde la IP: '" + socket.getInetAddress().getHostAddress()
				+ "' con el Puerto: " + socket.getPort());
		return socket;
	}

	// Registrar un cliente con su ID único y handler
	// Registrar un cliente con su ID único y dos sockets (texto y audio)
	public synchronized void registerClient(Socket textClientSocket, Socket audioClientSocket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(textClientSocket.getInputStream()));
		PrintWriter writer = new PrintWriter(textClientSocket.getOutputStream(), true);

		// Leer nombre de usuario desde el socket de texto
		String username = reader.readLine();

		// Comprobar si el nombre de usuario ya está en uso
		while (this.chatManager.clientExists(username)) {
			writer.println("El nombre de usuario ya está en uso.");
			username = reader.readLine();
		}

		// Confirmación de bienvenida al usuario
		writer.println("Bienvenido/a al chat " + username + "!");

		// Crear un nuevo `ClientHandler` con ambos sockets
		ClientHandler clientHandler = new ClientHandler(username, reader, writer, textClientSocket, audioClientSocket);
		this.chatManager.registerClient(username, clientHandler);

		// Ejecutar el manejo del cliente en un hilo separado
		pool.execute(clientHandler);
	}

}
