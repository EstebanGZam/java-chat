package communication;

import java.io.*;
import java.net.Socket;

public class CommunicationBroker implements CommunicationBrokerI {
	private final Socket clientSocket;
	private final BufferedReader socketReader;
	private final PrintWriter writer;
	private final DataInputStream dataInputStream; // Para leer audio o datos binarios

	public CommunicationBroker(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
		socketReader = initReader(clientSocket);
		writer = initWriter(clientSocket);
		dataInputStream = new DataInputStream(clientSocket.getInputStream());
	}

	private BufferedReader initReader(Socket clientSocket) throws IOException {
		return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	private PrintWriter initWriter(Socket clientSocket) throws IOException {
		return new PrintWriter(clientSocket.getOutputStream(), true);
	}

	@Override
	public String registerClient(String username) throws IOException {
		writer.println(username);
		return socketReader.readLine();
	}

	@Override
	public String receiveMessage() throws IOException {
		// Leer el encabezado primero para identificar el tipo de mensaje
		String header = socketReader.readLine();

		if ("TEXT".equals(header)) {
			// Recibir y procesar el texto
			return socketReader.readLine();
		} else if ("AUDIO".equals(header)) {
			// Recibir y procesar el audio
			receiveAudio();
			return "Audio ";
		} else {
			// Manejar casos donde el tipo de mensaje no es reconocido
			return "Tipo de mensaje no reconocido.";
		}
	}

	@Override
	public void processInstruction(String sourceUser, String instruction) {
		if (instruction.startsWith("/msg")) {
			sendMessageToAnotherClient(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/getHistory")) {
			showHistory(instruction);
		}
	}

	@Override
	public void sendMessageToAnotherClient(String instruction) {
		writer.println("TEXT"); // Enviar encabezado indicando que es un mensaje de texto
		writer.println(instruction); // Enviar el mensaje de texto
	}

	@Override
	public void showHistory(String instruction) {
		writer.println("TEXT"); // Enviar encabezado indicando que es un mensaje de texto
		writer.println(instruction); // Enviar el historial
	}

	@Override
	public void sendAudio(String sourceUser, String targetUser, File audioFile) {
		try {
			writer.println("AUDIO"); // Enviar encabezado indicando que es audio
			writer.println(sourceUser + ":::" + targetUser); // Enviar el nombre del usuario
			FileInputStream fis = new FileInputStream(audioFile);
			BufferedOutputStream bos = new BufferedOutputStream(this.clientSocket.getOutputStream());
			DataOutputStream dos = new DataOutputStream(bos);

			long fileSize = audioFile.length();
			dos.writeLong(fileSize); // Enviar el tamaño del archivo
			dos.flush();

			byte[] buffer = new byte[1024];
			int bytes;
			while ((bytes = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, bytes);
			}

			bos.flush();
			fis.close();
//			System.out.println("Audio enviado completamente.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Método para recibir un archivo de audio
	private void receiveAudio() {
		try {
			long fileSize = dataInputStream.readLong(); // Leer el tamaño del archivo
			byte[] buffer = new byte[1024];
			int bytesRead;
			long totalBytesRead = 0;
			File audioFile = new File("received_audio.wav"); // Guardar el archivo con un nombre
			FileOutputStream fos = new FileOutputStream(audioFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			while (totalBytesRead < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;
			}

			bos.flush();
			bos.close();
			System.out.println("Audio recibido completamente.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
