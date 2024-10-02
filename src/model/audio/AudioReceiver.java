package model.audio;

import java.io.*;
import java.net.Socket;

public class AudioReceiver {

	public File receiveAudio(String audioFileName, String destinationPath, Socket clientSocket) throws IOException {
		DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
		long fileSize = dis.readLong(); // Leer el tamaño del archivo
		byte[] buffer = new byte[1024];
		int bytesRead;
		long totalBytesRead = 0;
		// Crear la carpeta si no existe
		File audioFolder = new File(destinationPath);
		if (!audioFolder.exists()) {
			audioFolder.mkdirs(); // Crea la carpeta y las sub carpetas si no existen
		}
		File audioFile = new File(destinationPath + audioFileName); // Guardar el archivo con un nombre
		FileOutputStream fos = new FileOutputStream(audioFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
			bos.write(buffer, 0, bytesRead);
			totalBytesRead += bytesRead;
		}

		bos.flush();
		bos.close();

		return audioFile;

	}
}
