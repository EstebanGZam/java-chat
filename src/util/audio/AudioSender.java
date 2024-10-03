package util.audio;

import java.io.*;
import java.util.Base64;

public class AudioSender {

	/**
	 * Class to send an audio file over a socket.
	 * <p>
	 * This class uses the Base64 encoding algorithm to send the audio file over the socket.
	 *
	 * @param out       PrintWriter to write to the socket.
	 * @param audioFile File containing the audio to send.
	 * @throws IOException If there's an issue with network communication.
	 */
	public void sendAudio(PrintWriter out, File audioFile) throws IOException {
		try {
			// Leer archivo binario
			FileInputStream fileInputStream = new FileInputStream(audioFile);
			byte[] fileBytes = new byte[(int) audioFile.length()];
			int bytesRead = fileInputStream.read(fileBytes);
			if (bytesRead == -1) {
				throw new IOException("Error al leer el archivo de audio.");
			}
			fileInputStream.close();

			// Codificar a Base64
			String encodedString = Base64.getEncoder().encodeToString(fileBytes);

			String[] lines = encodedString.split("\r?\n");
			out.println(lines.length);

			// Enviar la cadena codificada a través del socket
			out.println(encodedString);  // Enviar datos codificados en Base64

			System.out.println("Audio enviado correctamente.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
