package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class TCPServer {
	public static final String IP = "127.0.0.1";
	public static final int PORT = 10000;

	public static void main(String[] args) {
		ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(10);
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("Servidor iniciado en el puerto " + PORT);
			while (!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println("Un nuevo cliente se ha conectado desde la IP: '" + socket.getInetAddress().getHostAddress()
						+ "' con el Puerto: " + socket.getPort());
				pool.execute(new ClientHandler(socket));
			}
		} catch (IOException _) {
		}
	}
}
