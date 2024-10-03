package util.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static model.client.Client.RECEIVED_AUDIO_PATH;
import static model.client.Client.RECORDED_AUDIO_PATH;

public class AudioPlayer {


	/**
	 * Plays an audio file.
	 *
	 * @param audioName the name of the audio file to play
	 * @return a message indicating the status of the audio file
	 * @throws LineUnavailableException      if the audio device is not available
	 * @throws IOException                   if there's an error reading the audio file
	 * @throws UnsupportedAudioFileException if the audio file format is not supported
	 */
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

	/**
	 * Search for an audio file in the received and recorded paths.
	 *
	 * @param audioName the name of the audio file to search
	 * @return the file if it exists, null otherwise
	 */
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

	/**
	 * Returns whether an audio file exists in either the received or recorded paths.
	 *
	 * @param audioName the name of the audio file to search
	 * @return true if the file exists, false otherwise
	 */
	public boolean audioExists(String audioName) {
		return searchAudio(audioName) != null;
	}
}
