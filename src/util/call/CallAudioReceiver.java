package util.call;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;

public class CallAudioReceiver {

    private final DatagramSocket socket;

    public CallAudioReceiver(DatagramSocket socket) {
        this.socket = socket;
    }

    public byte[] receiveAudio() {
        byte[] buffer = new byte[10240];
        try {
            System.out.println("Receiving audio...");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);
            System.out.println("Audio received.");
        } catch (IOException e) {
            System.err.println("Error al recibir audio: " + e.getMessage());
        }

        return buffer;
    }

}
