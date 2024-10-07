package util.call;

import javax.sound.sampled.*;
import java.net.DatagramSocket;

public class CallAudioPlayer {
	SourceDataLine speaker;

	public CallAudioPlayer() throws LineUnavailableException {
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
		DataLine.Info infoSpeaker = new DataLine.Info(SourceDataLine.class, format);
		this.speaker = (SourceDataLine) AudioSystem.getLine(infoSpeaker);
		this.speaker.open(format);
		this.speaker.start();
	}

	public void startPlayingReceivedVoice(DatagramSocket datagramSocket) {
		new Thread(() -> {
			CallAudioReceiver receiver = new CallAudioReceiver(datagramSocket);
			while (true) {
				byte[] buffer = receiver.receiveAudio();
				this.speaker.write(buffer, 0, buffer.length);
			}
		}).start();
	}
}
