package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

	private final Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

			String inetAddress = socket.getInetAddress().getHostAddress();
			String message;
			while ((message = reader.readLine()) != null) {
				System.out.println("Cliente:" + inetAddress + " >> " + message + " ");
				writer.println("ECHO >> " + message + " IP cliente: " + inetAddress);
			}
			System.out.println("Conexión del cliente finalizada");
			writer.close();
			reader.close();
			socket.close();
		} catch (SocketException e) {
			System.out.println("Conexión finalizada.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}