package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class ClientHandler implements Runnable {

	private Socket socket;
	private final ChatManager chatManager = ChatManager.getInstance();
	private PrintWriter writer;
	private BufferedReader reader;

	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException io) {
			closeEveryThing(this.socket, this.reader, this.writer);
		}
	}

	@Override
	public void run() {
		try {
			String username = registerClient();

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

	private String registerClient() throws IOException {
		String username = reader.readLine();

		while (chatManager.clientExists(username)) {
			writer.println("El nombre de usuario ya está en uso. Por favor, elige otro.");
			username = reader.readLine();
		}

		writer.println("Bienvenido/a al chat " + username + "!");

		chatManager.registerClient(username, this.socket);

		return username;
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