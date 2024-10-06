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
            System.err.println("Error al crear el socket para enviar audio: " + e.getMessage());
        }
    }

    public void sendAudio(String remoteHost, int sendingPort, TargetDataLine microphone) throws IOException {
        InetAddress address = InetAddress.getByName(remoteHost);
        byte[] buffer = new byte[2048]; // Tamaño del buffer optimizado

        try {
            while (running) {
                int byteRead = microphone.read(buffer, 0, buffer.length);
                if (byteRead == -1)
                    break; // Finalizar si no se leen más datos

                DatagramPacket packet = new DatagramPacket(buffer, byteRead, address, sendingPort);
                socket.send(packet);
            }
        } catch (IOException e) {
            System.err.println("Error al enviar el paquete de audio: " + e.getMessage());
        } finally {
            microphone.close();
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Cerrar el socket
        }
    }

    public void startSendingAudio(String remoteHost, int sendingPort, TargetDataLine microphone) {
        new Thread(() -> {
            try {
                sendAudio(remoteHost, sendingPort, microphone);
            } catch (IOException e) {
                System.err.println("Error al enviar audio: " + e.getMessage());
            }
        }).start();
    }
}
