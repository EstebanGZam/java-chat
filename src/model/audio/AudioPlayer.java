package model.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static model.client.Client.RECEIVED_AUDIO_PATH;
import static model.client.Client.RECORDED_AUDIO_PATH;

public class AudioPlayer {
	public void playAudio(String audioName)
			throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		File audioFile = searchAudio(audioName);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();

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
