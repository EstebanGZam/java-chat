package client;

import server.TCPServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	public static void main(String[] args) {
		try {
			System.out.println("Intentando conectar al servidor...");
			Socket socket = null;
			while (socket == null) {
				try {
					socket = new Socket(TCPServer.IP, 10000); // Crear el socket
				} catch (UnknownHostException e) {
					System.out.println("Error: Host desconocido.");
				} catch (IOException e) {
					System.out.println("Servidor no disponible. Reintentando en 5 segundos...");
					try {
						Thread.sleep(5000); // Espera 5 segundos antes de reintentar
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt(); // Vuelve a establecer el estado de interrupción
					}
				}
			}
			System.out.println("\n" + "Conexión establecida.");

			// Entrada de información (por consola)
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(socket);

			// Lee la información que llega del socket
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Escribe la información en el socket
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

			String message = "";
			while (message != null && !message.equalsIgnoreCase("exit")) {
				System.out.print("Escribe un mensaje para el servidor: ");
				message = reader.readLine();
				writer.println(message);
				String response = socketReader.readLine();
				System.out.println(response);
			}

			reader.close();
			writer.close();
			socket.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}