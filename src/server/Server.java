package server;

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
	public static final int PORT = 10000;

	private Server() {
		this.pool = java.util.concurrent.Executors.newFixedThreadPool(10);
		this.chatManager = ChatManager.getInstance();
	}

	public static void main(String[] args) {
		Server server = new Server();
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("Servidor iniciado en el puerto " + PORT);
			while (!serverSocket.isClosed()) {
				Socket clientSocket = server.acceptNewClient(serverSocket);
				server.registerClient(clientSocket);
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
	public synchronized void registerClient(Socket clientSocket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
		String username = reader.readLine();

		while (this.chatManager.clientExists(username)) {
			writer.println("El nombre de usuario ya está en uso.");
			username = reader.readLine();
		}
		writer.println("Bienvenido/a al chat " + username + "!");
		ClientHandler clientHandler = new ClientHandler(username, clientSocket, reader, writer);
		this.chatManager.registerClient(username, clientHandler);
		pool.execute(clientHandler);
	}

}
