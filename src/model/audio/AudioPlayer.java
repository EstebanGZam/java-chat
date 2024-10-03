package model.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static model.client.Client.RECEIVED_AUDIO_PATH;
import static model.client.Client.RECORDED_AUDIO_PATH;

public class AudioPlayer {
	public String playAudio(String audioName)
			throws LineUnavailableException, IOException, UnsupportedAudioFileException {

		File audioFile = searchAudio(audioName);
		if (audioFile == null) {
			return "El archivo de audio '" + audioName + "' no existe.";
		}
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();

		return "Reproduciendo " + audioName + "...";
	}

	public File searchAudio(String audioName) {
		File recordedAudio = new File(RECORDED_AUDIO_PATH + audioName);
		File receivedAudio = new File(RECEIVED_AUDIO_PATH + audioName);
		if (recordedAudio.exists()) {
			return recordedAudio;
		} else if (receivedAudio.exists()) {
			return receivedAudio;
		} else {
			return null;
		}
	}

	public boolean audioExists(String audioName) {
		return searchAudio(audioName) != null;
	}
}
