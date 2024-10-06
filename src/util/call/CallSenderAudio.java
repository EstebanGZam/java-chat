package util.call;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class CallSenderAudio {

    private boolean running = true;
    private DatagramSocket socket;

    public CallSenderAudio() {
        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            System.err.println("Error al crear el socket para enviar audio: " + e.getMessage());
        }
    }

    public void sendAudio(String remoteHost, int sendingPort, int bytesRead) throws IOException {
        InetAddress address = InetAddress.getByName(remoteHost);
        byte[] buffer = new byte[10240]; // Tamaño del buffer optimizado

        try {
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, sendingPort);
                socket.send(packet);
            }
        } catch (IOException e) {
            System.err.println("Error al enviar el paquete de audio: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Cerrar el socket
        }
    }

    public void startSendingAudio(String remoteHost, int sendingPort, int bytesRead) {
        new Thread(() -> {
            try {
                sendAudio(remoteHost, sendingPort, bytesRead);
            } catch (IOException e) {
                System.err.println("Error al enviar audio: " + e.getMessage());
            }
        }).start();
    }
}
