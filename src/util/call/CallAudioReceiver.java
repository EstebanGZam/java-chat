package util.call;

import javax.sound.sampled.*;
import java.net.DatagramSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public class CallAudioReceiver {

	private boolean running = false;
	private DatagramSocket socket;

	public CallAudioReceiver(DatagramSocket socket) {
		this.socket = socket;
	}

	public void startReceiving() {
		running = true;
		new Thread(this::receiveAudio).start();
	}

	public void stopReceiving() {
		running = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	public void receiveAudio() {
		try {
			AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
			DataLine.Info infoSpeaker = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(infoSpeaker);
			speaker.open(format);
			speaker.start();

			byte[] buffer = new byte[10240];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while (running) {
				socket.receive(packet);
				speaker.write(packet.getData(), 0, packet.getLength());
			}

			speaker.drain();
			speaker.close();
		} catch (IOException | LineUnavailableException e) {
			System.err.println("Error al recibir audio: " + e.getMessage());
		} finally {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
	}

//	public static byte[] receiveBytesRead(InputStream is) {
//		byte[] buffer = new byte[10240];
//		try {
//			is.read(buffer);
//		} catch (IOException e) {
//			System.err.println("Error al enviar el paquete de audio: " + e.getMessage());
//		}
//		return buffer;
//	}
}
