package model.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import model.messages.Audio;

public class AudioRecorder {
    private TargetDataLine microphone;
    private AudioFormat format;
    private boolean isRecording;
    private File audioFile;

    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    private final Socket audioSocket;

    public AudioRecorder(Socket audioSocket) {
        format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        isRecording = false;
        // crear directorio para almacenar los audios
        File audioDirectory = new File("./resources/audio");
        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs();
        }
        this.audioSocket = audioSocket;
    }

    public void startRecording(String audioName) throws LineUnavailableException, IOException {
        DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
        microphone.open(format);
        microphone.start();
        isRecording = true;

        audioFile = new File("./resources/audio/" + audioName + ".wav");
        new Thread(() -> {
            try (AudioInputStream audioStream = new AudioInputStream(microphone)) {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopRecording() {
        isRecording = false;
        microphone.stop();
        microphone.close();
    }

    public void playAudio(String audioName)
            throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        audioFile = new File("./resources/audio/" + audioName + ".wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }

    public void sendAudio(String audioName) throws IOException {
        audioFile = new File("./resources/audio/" + audioName + ".wav");
        byte[] audioBytes = new byte[(int) audioFile.length()];

        System.out.println("Enviando audio: " + audioName);
        FileInputStream fis = new FileInputStream(audioFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(audioBytes, 0, audioBytes.length);
        bis.close();

        // Crear el flujo de salida para enviar los datos
        DataOutputStream dos = new DataOutputStream(audioSocket.getOutputStream());
        dos.writeLong(audioBytes.length); // Tamaño del archivo
        System.out.println("Enviando audio al servidor...");

        // Enviar los datos de audio en chunks
        dos.write(audioBytes, 0, audioBytes.length);
        dos.flush(); // Asegurarse de vaciar el buffer

        System.out.println("Audio enviado completamente.");
    }

    public void receiveAudio(String audioName) throws IOException {
        audioFile = new File("./resources/audio/" + audioName + ".wav");

        // Recibir el tamaño del archivo
        DataInputStream dis = new DataInputStream(audioSocket.getInputStream());
        long fileSize = dis.readLong();
        System.out.println("Recibiendo audio... Tamaño: " + fileSize + " bytes.");

        // Preparar el archivo para la escritura
        FileOutputStream fos = new FileOutputStream(audioFile);
        byte[] buffer = new byte[4096]; // Búfer de tamaño adecuado
        int bytesRead;
        long totalBytesRead = 0;

        // Leer en bloques el archivo
        while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }

        fos.flush();
        fos.close();

        System.out.println("Audio recibido completamente.");
    }

    public Audio saveAudio() {
        return new Audio(audioFile);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean audioExists(String audioName) {
        audioFile = new File("./resources/audio/" + audioName + ".wav");
        return audioFile.exists();
    }
}
