package util.communication;

import util.audio.AudioReceiver;
import util.audio.AudioSender;
import util.call.CallAudioRecorder;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

import static model.client.Client.RECEIVED_AUDIO_PATH;

public class CommunicationBrokerI implements CommunicationBroker {
	private final Socket clientSocket;
	private final BufferedReader socketReader;
	private final PrintWriter writer;

	// Puerto por donde se enviará y recibirá el audio en las llamadas
	// private int callPort;

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
				hearInCall(port);
				return "Call established.";
			default:
				return "Tipo de mensaje no reconocido.";
		}
	}

	@Override
	public void endCall() {
		writer.println("TEXT");
		writer.println("/endCall");
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
		} else if (instruction.startsWith("/call")) {
			startCallProcess(instruction);
		} else if (instruction.startsWith("/acceptCall")) {
			acceptCall(instruction + "<<<<<" + sourceUser);
		} else if (instruction.equals("/endCall")) {
			endCall();
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

	public void acceptCall(String instruction) {
		writer.println("TEXT");
		writer.println(instruction);
	}

	/**
	 * Método mejorado para grabar y enviar el audio en tiempo real durante la
	 * llamada.
	 */
	public void talkInCall(int port) {
		try {
			DatagramSocket audioSocket = new DatagramSocket();
			InetAddress receiverAddress = InetAddress.getByName("localhost"); // Dirección del servidor o cliente

			CallAudioRecorder recorder = new CallAudioRecorder();
			recorder.startRecording();

			while (true) {
				byte[] audioData = recorder.getRecordedData(); // Obtener fragmento de audio
				DatagramPacket packet = new DatagramPacket(audioData, audioData.length, receiverAddress, port);
				audioSocket.send(packet); // Enviar el fragmento de audio

				// Verificar si la llamada terminó
				if (socketReader.ready() && socketReader.readLine().equals("/endCall")) {
					recorder.stopRecording();
					break;
				}
			}
			audioSocket.close();
		} catch (IOException e) {
			System.err.println("Error durante la transmisión de la llamada: " + e.getMessage());
		}
	}

	/**
	 * Método mejorado para recibir el audio en tiempo real durante la llamada.
	 */
	public void hearInCall(int port) throws IOException {
		DatagramSocket audioSocket = new DatagramSocket(port);
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
		SourceDataLine speaker;

		try {
			DataLine.Info infoSpeaker = new DataLine.Info(SourceDataLine.class, format);
			speaker = (SourceDataLine) AudioSystem.getLine(infoSpeaker);
			speaker.open(format);
			speaker.start();

			byte[] buffer = new byte[10240]; // Tamaño del buffer para los paquetes de audio

			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				audioSocket.receive(packet); // Recibir fragmento de audio
				speaker.write(packet.getData(), 0, packet.getLength()); // Reproducir audio

				// Verificar si la llamada terminó
				if (socketReader.ready() && socketReader.readLine().equals("/endCall")) {
					break;
				}
			}

			speaker.drain();
			speaker.close();
		} catch (LineUnavailableException e) {
			System.err.println("Error al inicializar el altavoz: " + e.getMessage());
		} finally {
			audioSocket.close();
		}
	}
}
