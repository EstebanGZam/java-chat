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

    public AudioRecorder() {
        format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        isRecording = false;
        // crear directorio para almacenar los audios
        File audioDirectory = new File("./resources/audio");
        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs();
        }
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

    public void sendAudio(String audioName, Socket audioSocket) throws IOException {
        audioFile = new File("./resources/audio/" + audioName + ".wav");
        OutputStream os = audioSocket.getOutputStream();
        FileInputStream fis = new FileInputStream(audioFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        fis.close();
    }

    public void receiveAudio(String audioName, Socket audioSocket) throws IOException, LineUnavailableException {
        InputStream is = audioSocket.getInputStream();
        FileOutputStream fos = new FileOutputStream("./resources/audio/" + audioName + ".wav");
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
    }

    public Audio saveAudio() {
        return new Audio(audioFile);
    }

    public boolean isRecording() {
        return isRecording;
    }
}
