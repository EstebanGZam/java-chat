package util.communication;

import util.audio.AudioReceiver;
import util.audio.AudioSender;
import util.call.CallAudioPlayer;
import util.call.CallAudioRecorder;

import javax.sound.sampled.LineUnavailableException;

import model.persistence.MessagePersistence;

import java.io.*;
import java.net.*;

import static model.client.Client.RECEIVED_AUDIO_PATH;

public class CommunicationBrokerI implements CommunicationBroker {
	private final Socket clientSocket;
	private final BufferedReader socketReader;
	private final PrintWriter writer;
	private CallAudioRecorder recorder;
	private CallAudioPlayer player;
	private DatagramSocket udpSocket;

	public CommunicationBrokerI(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
		socketReader = initReader(clientSocket);
		writer = initWriter(clientSocket);
	}

	private BufferedReader initReader(Socket clientSocket) throws IOException {
		return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	private PrintWriter initWriter(Socket clientSocket) throws IOException {
		return new PrintWriter(clientSocket.getOutputStream(), true);
	}

	@Override
	public String registerClient(String username) throws IOException {
		writer.println(username);
		return socketReader.readLine();
	}

	@Override
	public String receiveMessage() throws IOException {
		String header = socketReader.readLine();

		switch (header) {
			case "TEXT":
				return socketReader.readLine();
			case "AUDIO":
				receiveAudio();
				return "Audio received.";
			case "CALL":
				int port = Integer.parseInt(socketReader.readLine());
				this.udpSocket = new DatagramSocket(0);
				hearInCall(this.udpSocket);
				talkInCall(this.udpSocket, port);
				writer.println(this.udpSocket.getLocalPort());
				writer.println(InetAddress.getLocalHost().getHostAddress());
				return "Call established.";
			default:
				return "Tipo de mensaje no reconocido.";
		}
	}

	@Override
	public void processInstruction(String sourceUser, String instruction) {
		if (instruction.startsWith("/msg")) {
			sendMessageToAnotherClient(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/getHistory")) {
			showHistory(instruction);
		} else if (instruction.startsWith("/createGroup")) {
			createGroup(instruction);
		} else if (instruction.startsWith("/joinGroup")) {
			joinGroup(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/listGroups")) {
			listGroups(instruction);
		} else if (instruction.startsWith("/groupMsg")) {
			sendGroupMessage(instruction);
		} else if (instruction.startsWith("/groupCall")) {
			startCallProcess(instruction + "<<<<<" + sourceUser);
		} else if (instruction.startsWith("/acceptCall")) {
			sendCallResponse(instruction + "<<<<<" + sourceUser);
		} else if (instruction.startsWith("/rejectCall")) {
			sendCallResponse(instruction + "<<<<<" + sourceUser);
		} else if (instruction.startsWith("/endCall")) {
			endCall(instruction + "<<<<<" + sourceUser);
		} else if (instruction.startsWith("/call")) {
			startCallProcess(instruction + "<<<<<" + sourceUser);
		}
	}

	@Override
	public void sendMessageToAnotherClient(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void sendAudio(String sourceUser, String targetUser, File audioFile) throws IOException {
		writer.println("AUDIO");
		writer.println(sourceUser + ":::" + targetUser);
		writer.println(audioFile.getName());

		AudioSender audioSender = new AudioSender();
		audioSender.sendAudio(writer, audioFile);
		String audioName = audioFile.getName();
		MessagePersistence.saveAudio(sourceUser, targetUser, audioName);

	}

	private void receiveAudio() {
		try {
			String sourceUser = socketReader.readLine();
			String audioFileName = socketReader.readLine();
			AudioReceiver audioReceiver = new AudioReceiver();
			File audioFile = audioReceiver.receiveAudio(audioFileName, RECEIVED_AUDIO_PATH, socketReader);
			if (audioFile != null) {
				System.out.println("Audio recibido de '" + sourceUser
						+ "'. Para reproducirlo, escriba el comando: '/play " + audioFileName + "'");
			}
		} catch (IOException e) {
			System.err.println("Error al recibir audio: " + e.getMessage());
		}
	}

	@Override
	public void createGroup(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void listGroups(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void joinGroup(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void sendGroupMessage(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	@Override
	public void closeConnection() throws IOException {
		writer.close();
		socketReader.close();
		clientSocket.close();
	}

	@Override
	public void showHistory(String historialRequest) {
		writer.println("TEXT");
		writer.println(historialRequest);
	}

	public void startCallProcess(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	public void sendCallResponse(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	/**
	 * Método mejorado para recibir el audio en tiempo real durante la llamada.
	 */
	public void hearInCall(DatagramSocket socket) {
		try {
			this.player = new CallAudioPlayer();
			this.player.startPlayingReceivedVoice(socket);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método mejorado para grabar y enviar el audio en tiempo real durante la
	 * llamada.
	 */
	public void talkInCall(DatagramSocket datagramSocket, int serverPort) {
		this.recorder = new CallAudioRecorder();
		this.recorder.startSendingOfVoice(datagramSocket, serverPort);
	}

	@Override
	public void endCall(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
		this.recorder.stopRecording();
		this.player.stopPlaying();
		this.udpSocket.close();
	}
}
