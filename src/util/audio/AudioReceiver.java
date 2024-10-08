package util.audio;

import java.io.*;
import java.util.Base64;

public class AudioReceiver {

	/**
	 * Utility class for receiving audio files from a socket connection.
	 * This class decodes Base64 encoded strings received from a socket connection
	 * and saves the decoded bytes to a file.
	 *
	 * @param audioFileName   the name of the audio file to be saved
	 * @param destinationPath the path where the audio file will be saved
	 *                        (the directory will be created if it doesn't exist)
	 * @param in              the socket input stream
	 * @throws IOException if there's an error with network communication
	 */
	public File receiveAudio(String audioFileName, String destinationPath, BufferedReader in) throws IOException {
		File audioFile = null;

		try {
			// Leer el número de líneas que serán recibidas
			int numLines = Integer.parseInt(in.readLine());
			System.out.println("Número de líneas recibidas: " + numLines);

			// Leer la cadena Base64 que tiene `numLines` líneas
			StringBuilder encodedString = new StringBuilder();
			for (int i = 0; i < numLines; i++) {
				String line = in.readLine();
				encodedString.append(line);
			}

			// Decodificar la cadena Base64 a bytes
			byte[] fileBytes = Base64.getDecoder().decode(encodedString.toString());
			// Crear la carpeta si no existe
			File audioFolder = new File(destinationPath);
			if (!audioFolder.exists()) {
				audioFolder.mkdirs(); // Crea la carpeta y las sub carpetas si no existen
			}
			// Crear el archivo en la ruta de destino
			audioFile = new File(audioFolder, audioFileName);
			FileOutputStream fileOutputStream = new FileOutputStream(audioFile);
			fileOutputStream.write(fileBytes);  // Escribir los bytes en el archivo

			// Cerrar los flujos
			fileOutputStream.close();

			System.out.println("Audio recibido correctamente y guardado en: " + audioFile.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return audioFile;  // Devolver el archivo creado
	}
}
