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
		} catch (IOException _) {
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
		String clientId = reader.readLine();

		while (this.chatManager.clientExists(clientId)) {
			writer.println("El nombre de usuario ya está en uso.");
			clientId = reader.readLine();
		}
		writer.println("Bienvenido/a al chat " + clientId + "!");
		ClientHandler clientHandler = new ClientHandler(clientSocket, reader, writer);
		this.chatManager.registerClient(clientId, clientHandler);
		pool.execute(clientHandler);
	}

}
