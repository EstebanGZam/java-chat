package util.call;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class CallSenderAudio {

	//	private boolean running = true;
	private final DatagramSocket socket;

	public CallSenderAudio(DatagramSocket socket) {
		this.socket = socket;
	}

	public void sendAudio(String remoteHost, int sendingPort, byte[] buffer) throws IOException {
		InetAddress address = InetAddress.getByName(remoteHost);

		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sendingPort);
			this.socket.send(packet);
		} catch (IOException e) {
			System.err.println("Error al enviar el paquete de audio: " + e.getMessage());
		}
	}
}
