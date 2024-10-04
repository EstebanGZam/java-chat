package util.audio;

import javax.sound.sampled.*;
import java.io.*;

import static model.client.Client.RECEIVED_AUDIO_PATH;
import static model.client.Client.RECORDED_AUDIO_PATH;

public class AudioRecorder {
	private TargetDataLine microphone;
	private final AudioFormat format;
	private boolean isRecording;
	private File audioFile;

	public static final int SAMPLE_RATE = 44100;
	public static final int SAMPLE_SIZE_IN_BITS = 16;
	public static final int CHANNELS = 1;
	public static final boolean SIGNED = true;
	public static final boolean BIG_ENDIAN = true;

	private String currentTarget;
	private String receivedAudioName;
	private String currentGroup;
	private boolean isGroupRecording;

	public AudioRecorder() {
		this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
		this.isRecording = false;
		initDirectory(RECORDED_AUDIO_PATH);
		initDirectory(RECEIVED_AUDIO_PATH);
	}

	public void initDirectory(String dir) {
		File directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public void startRecording(String targetUser, String audioName) {
		this.currentTarget = targetUser;
		this.receivedAudioName = audioName + ".wav";
		new Thread(() -> {
			try {
				DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
				microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
				microphone.open(format);
				microphone.start();
				isRecording = true;
			} catch (LineUnavailableException e) {
				System.out.println("Error al iniciar la grabación de audio.");
				System.out.println(e.getMessage());
			}

			audioFile = new File(RECORDED_AUDIO_PATH + this.receivedAudioName);
			try (AudioInputStream audioStream = new AudioInputStream(microphone)) {
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
			} catch (IOException e) {
				System.out.println("Error al guardar el archivo de audio.");
				System.out.println(e.getMessage());
			}
		}).start();
	}

	public void startGroupRecording(String groupName, String audioName) {
		this.currentGroup = groupName;
		this.isGroupRecording = true;
		this.receivedAudioName = audioName + ".wav";
		new Thread(() -> {
			try {
				DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
				microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
				microphone.open(format);
				microphone.start();
				isRecording = true;
			} catch (LineUnavailableException e) {
				System.out.println("Error al iniciar la grabación de audio.");
				System.out.println(e.getMessage());
			}

			audioFile = new File(RECORDED_AUDIO_PATH + this.receivedAudioName);
			try (AudioInputStream audioStream = new AudioInputStream(microphone)) {
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
			} catch (IOException e) {
				System.out.println("Error al guardar el archivo de audio.");
				System.out.println(e.getMessage());
			}
		}).start();
	}

	public boolean isGroupRecording() {
		return isGroupRecording;
	}

	public String getCurrentGroup() {
		return currentGroup;
	}

	public void stopRecording() {
		isRecording = false;
		microphone.stop();
		microphone.close();
	}

	public boolean isRecording() {
		return isRecording;
	}

	public String getCurrentTarget() {
		return currentTarget;
	}

	public String getReceivedAudioName() {
		return receivedAudioName;
	}
}
