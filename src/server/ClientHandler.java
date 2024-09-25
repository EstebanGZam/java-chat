package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class ClientHandler implements Runnable {
	private final ChatManager chatManager = ChatManager.getInstance();
	private final Socket socket;
	private final PrintWriter writer;
	private final BufferedReader reader;
	private final String username;

	public ClientHandler(String username, Socket socket, BufferedReader reader, PrintWriter writer) {
		this.username = username;
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
	}

	@Override
	public void run() {
		try {
			showInstructions();

			String instruction;
			while ((instruction = reader.readLine()) != null) {
				if (instruction.startsWith("/msg")) {
					String[] parts = instruction.split(" ");
					String receiver = parts[1];
					String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
					chatManager.sendTextMessage(username, receiver, message);
					writer.println(message);
				}
			}
			writer.println("Conexión finalizada");

			closeEveryThing(this.socket, this.reader, this.writer);

		} catch (SocketException e) {
			closeEveryThing(this.socket, this.reader, this.writer);
			writer.println("Conexión finalizada.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void showInstructions() {
		writer.println("----------------------------------------------------------------------------------------------");
		writer.println("Para enviar un mensaje a todos, solo escribe el mensaje y presiona Enter.");
		writer.println("Para enviar un mensaje privado a otro cliente, escribe: /msg <usuario_destino> <mensaje>");
		writer.println("----------------------------------------------------------------------------------------------");
		writer.flush();
	}

	public void closeEveryThing(Socket clientSocket, BufferedReader reader, PrintWriter writer) {
		try {
			if (reader != null) reader.close();
			if (writer != null) writer.close();
			if (clientSocket != null) clientSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}