package util.call;

import javax.sound.sampled.*;

public class CallAudioRecorder {

    private TargetDataLine microphone;
    private final AudioFormat format;
    private Thread recordingThread;

    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    public CallAudioRecorder() {
        this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
    }

    public void startRecording() {
        recordingThread = new Thread(() -> {
            try {
                DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
                microphone.open(format);
                microphone.start();
            } catch (LineUnavailableException e) {
                System.out.println("Error al iniciar la grabación de audio.");
                System.out.println(e.getMessage());
            }
        });
        recordingThread.start();
    }

    public void stopRecording() {
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        recordingThread.interrupt();
    }

    public TargetDataLine getMicrophone() {
        return microphone;
    }
}
