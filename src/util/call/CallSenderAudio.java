package util.call;

import javax.sound.sampled.*;
import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class CallSenderAudio {

    private boolean running = true;
    private DatagramSocket socket;

    public CallSenderAudio(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (IOException e) {
            System.out.println("Error al crear el socket para enviar audio.");
            System.out.println(e.getMessage());
        }
    }

    public void sendAudio(String remoteHost, int sendingPort, TargetDataLine microphone)
            throws IOException {
        InetAddress address = InetAddress.getByName(remoteHost);
        byte[] buffer = new byte[10240];

        while (running) {
            int byteRead = microphone.read(buffer, 0, buffer.length);
            DatagramPacket packet = new DatagramPacket(buffer, byteRead, address, sendingPort);
            socket.send(packet);
        }

        microphone.close();
    }

}