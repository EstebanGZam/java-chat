package util.call;

import model.server.Server;

import javax.sound.sampled.*;
import java.net.DatagramSocket;

public class CallAudioRecorder {
    // Atributos de la clase
    private TargetDataLine microphone;
    private boolean isRecording;
    private byte[] buffer;
    private volatile int bytesRead;

    // Método para iniciar la grabación
    public void startSendingOfVoice(DatagramSocket datagramSocket, int serverPort) {
        initRecorder();
        // Iniciar un hilo para capturar el audio
        Thread captureThread = new Thread(() -> {
            CallSenderAudio callSenderAudio = new CallSenderAudio(datagramSocket);

            this.buffer = new byte[10240]; // Buffer de grabación
            while (isRecording) {
                this.bytesRead = microphone.read(buffer, 0, buffer.length);
                callSenderAudio.sendAudio(Server.IP, serverPort, this.bytesRead, buffer);
            }
        });

        captureThread.start();

    }

    private void initRecorder() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
        } catch (LineUnavailableException e) {
            System.err.println("Error al iniciar la grabación: " + e.getMessage());
        }
    }

    // Método para detener la grabación
    public void stopRecording() {
        isRecording = false;
        microphone.stop();
        microphone.close();
    }

}
