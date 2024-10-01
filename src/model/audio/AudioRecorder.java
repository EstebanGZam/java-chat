package model.audio;

import javax.sound.sampled.*;
import java.io.*;

import model.messages.Audio;

import static model.client.Client.RECEIVED_AUDIO_PATH;
import static model.client.Client.RECORDED_AUDIO_PATH;

public class AudioRecorder {
	private TargetDataLine microphone;
	private final AudioFormat format;
	private boolean isRecording;
	//	private final boolean isHearing;
	private File audioFile;

	public static final int SAMPLE_RATE = 44100;
	public static final int SAMPLE_SIZE_IN_BITS = 16;
	public static final int CHANNELS = 1;
	public static final boolean SIGNED = true;
	public static final boolean BIG_ENDIAN = true;

	public AudioRecorder() {
		this.format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
		this.isRecording = false;
//		this.isHearing = false;
		initDirectory(RECORDED_AUDIO_PATH);
		initDirectory(RECEIVED_AUDIO_PATH);
	}

	public void initDirectory(String dir) {
		File directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public void startRecording(String audioName) {
		new Thread(() -> {
			try {
				DataLine.Info infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
				microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
				microphone.open(format);
				microphone.start();
				isRecording = true;
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}

			audioFile = new File(RECORDED_AUDIO_PATH + audioName + ".wav");
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

	public Audio saveAudio() {
		return new Audio(audioFile);
	}

	public boolean isRecording() {
		return isRecording;
	}

}
