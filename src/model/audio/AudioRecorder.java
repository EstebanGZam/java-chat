package model.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import model.messages.Audio;

public class AudioRecorder {
    private TargetDataLine microphone;
    private AudioFormat format;
    private boolean isRecording;
    private boolean isHearing;
    private File audioFile;

    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    private static final String RECORDED_AUDIO_PATH = "./resources/audio/recorded/";
    private static final String RECEIVED_AUDIO_PATH = "./resources/audio/received/";

    private final Socket audioSocket;

    public AudioRecorder(Socket audioSocket) {
        this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        this.isRecording = false;
        this.isHearing = false;
        this.audioSocket = audioSocket;
        initDirectory(RECORDED_AUDIO_PATH);
        initDirectory(RECEIVED_AUDIO_PATH);
    }

    public void initDirectory(String dir) {
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void startRecording(String audioName) throws LineUnavailableException, IOException {
        DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
        microphone.open(format);
        microphone.start();
        isRecording = true;

        audioFile = new File(RECORDED_AUDIO_PATH + audioName + ".wav");
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
        audioFile = searchAudio(audioName);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }

    public void sendAudio(String audioName) throws IOException {
        audioFile = searchAudio(audioName);
        FileInputStream fis = new FileInputStream(audioFile);
        BufferedOutputStream bos = new BufferedOutputStream(audioSocket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(bos);

        System.out.println("Inicializando outputs" + dos.toString());

        long fileSize = audioFile.length();
        dos.writeLong(fileSize);
        dos.flush();

        System.out.println("Tamaño del archivo sendAudio: " + fileSize);

        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            System.out.println("entre al while");
            bos.write(buffer, 0, bytes);
        }

        bos.flush();
        fis.close();
        System.out.println("Audio enviado completamente.");
    }

    public void receiveAudio(String audioName) throws IOException {
        audioFile = new File(RECEIVED_AUDIO_PATH + audioName + ".wav");
        if (audioFile.exists()) {
            audioFile.delete();
        }

        System.out.println("Entre al metodo receiveAudio de AR");

        BufferedInputStream bis = new BufferedInputStream(audioSocket.getInputStream());
        FileOutputStream fos = new FileOutputStream(audioFile);
        DataInputStream dis = new DataInputStream(bis);
        isHearing = true;

        System.out.println("Inicializando inputs" + dis.toString());

        long fileSize = dis.readLong(); // Recibir el tamaño del archivo como long
        long totalBytesRead = 0;

        System.out.println("Tamaño del archivo receiveAudio: " + fileSize);

        byte[] buffer = new byte[1024];
        int bytesRead;

        System.out.println("Recibiendo audio: " + isHearing);

        // Recibir datos hasta que se alcancen los bytes del tamaño del archivo
        while (totalBytesRead < fileSize && (bytesRead = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            System.out.println("Recibiendo audio... bytes recibidos: " + totalBytesRead);
        }

        fos.close();
        isHearing = false;
        System.out.println("Audio recibido completamente.");
    }

    // private void receiveAudio(DataInputStream dis, FileOutputStream fos) throws
    // IOException {
    // System.out.println("procesando audio");
    // int bytes = 0;
    // byte[] buffer = new byte[1024];

    // long fileSize = dis.readLong();
    // long totalBytesRead = 0;

    // System.out.println("procesando audio2");
    // while (totalBytesRead < fileSize && (bytes = dis.read(buffer, 0,
    // buffer.length)) != -1) {
    // System.out.println("Recibiendo audio...");
    // fos.write(buffer, 0, bytes);
    // totalBytesRead += bytes;
    // }
    // fos.close();
    // isHearing = false;
    // System.out.println("Audio recibido completamente.");
    // }

    public Audio saveAudio() {
        return new Audio(audioFile);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isHearing() {
        return isHearing;
    }

    public boolean audioExists(String audioName) {
        // buscar audio en ambas carpetas
        File recordedAudio = new File(RECORDED_AUDIO_PATH + audioName + ".wav");
        File receivedAudio = new File(RECEIVED_AUDIO_PATH + audioName + ".wav");
        return recordedAudio.exists() || receivedAudio.exists();
    }

    public File searchAudio(String audioName) {
        File recordedAudio = new File(RECORDED_AUDIO_PATH + audioName + ".wav");
        File receivedAudio = new File(RECEIVED_AUDIO_PATH + audioName + ".wav");
        if (recordedAudio.exists()) {
            return recordedAudio;
        } else if (receivedAudio.exists()) {
            return receivedAudio;
        } else {
            return null;
        }
    }

}
