package model.audio;

import java.io.*;
import java.net.Socket;

public class AudioSender {

	public void sendAudio(Socket clientSocket, File audioFile) throws IOException {
		FileInputStream fis = new FileInputStream(audioFile);
		BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
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
	}
}
