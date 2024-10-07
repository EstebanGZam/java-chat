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
	public static final String IP = "192.168.43.92";
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

	/**
	 * Accepts a new client connection.
	 *
	 * @param serverSocket the server socket to accept new clients
	 * @throws IOException If an I/O error occurs while accepting the new client
	 */
	private Socket acceptNewClient(ServerSocket serverSocket) throws IOException {
		Socket socket = serverSocket.accept();
		System.out.println("Un nuevo cliente se ha conectado desde la IP: '" + socket.getInetAddress().getHostAddress()
				+ "' con el Puerto: " + socket.getPort());
		return socket;
	}

	/**
	 * Registers a client with its corresponding socket.
	 * <p>
	 * This method associates a client with its unique ID and a handler that will be
	 * used to communicate with it.
	 *
	 * @param clientSocket the socket of the client to register
	 * @throws IOException If an I/O error occurs while registering the client
	 */
	public synchronized void registerClient(Socket clientSocket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
		String username = reader.readLine();

		while (this.chatManager.clientExists(username)) {
			writer.println("El nombre de usuario ya está en uso.");
			username = reader.readLine();
		}
		writer.println("Bienvenido/a al chat " + username + "!");
		ClientHandler clientHandler = new ClientHandler(username, reader, writer);
		this.chatManager.registerClient(username, clientHandler);
		pool.execute(clientHandler);
	}

}