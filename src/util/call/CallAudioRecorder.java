package util.call;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CallAudioRecorder {
    // Atributos de la clase
    private TargetDataLine microphone;
    private ByteArrayOutputStream audioOutputStream; // Se asegura que esté accesible en todos los métodos
    private boolean isRecording;

    // Método para iniciar la grabación
    public void startRecording() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            audioOutputStream = new ByteArrayOutputStream(); // Inicializar el stream aquí
            isRecording = true;

            // Iniciar un hilo para capturar el audio
            Thread captureThread = new Thread(() -> {
                byte[] buffer = new byte[1024]; // Buffer de grabación
                while (isRecording) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            });

            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Error al iniciar la grabación: " + e.getMessage());
        }
    }

    // Método para obtener los datos grabados
    public byte[] getRecordedData() {
        byte[] audioData = audioOutputStream.toByteArray(); // Obtener los datos del audio grabado
        audioOutputStream.reset(); // Limpiar el stream después de leer
        return audioData;
    }

    // Método para detener la grabación
    public void stopRecording() {
        isRecording = false;
        microphone.stop();
        microphone.close();
        try {
            audioOutputStream.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el stream de audio: " + e.getMessage());
        }
    }
}
