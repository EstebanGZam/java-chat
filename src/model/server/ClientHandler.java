package model.server;

import model.audio.AudioRecorder;
import model.manager.ChatManager;
import model.messages.Audio;
import model.messages.Message;

import java.io.*;
import java.util.List;

import javax.sound.sampled.*;

import java.net.Socket;

public class ClientHandler implements Runnable {
	private final ChatManager chatManager = ChatManager.getInstance();
	private final String username;
	private final PrintWriter writer;
	private final BufferedReader reader;
	private final AudioRecorder audioRecorder = new AudioRecorder();
	private final Socket clientSocket;

	public ClientHandler(String username, BufferedReader reader, PrintWriter writer, Socket clientSocket) {
		this.username = username;
		this.reader = reader;
		this.writer = writer;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		receiveMessage();
	}

	public void receiveMessage() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				processMessage(message);
			}
		} catch (IOException e) {
			System.out.println("'" + this.username + "' se ha desconectado del chat.");
			chatManager.unregisterClient(this.username);
		}
	}

	private void processMessage(String message) {
		if (message.startsWith("/msg")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendMessageToAnotherClient(sender, instruction);
		} else if (message.equals("/getHistory")) {
			showHistory();
		} else if (message.startsWith("/record")) {
			String[] parts = message.split(" ");
			String audioName = parts[1];
			startAudioRecording(audioName);
		} else if (message.startsWith("/stop-audio")) {
			stopAudioRecording();
		} else if (message.startsWith("/send-audio")) {
			String[] parts = message.split("<<<<<");
			String instruction = parts[0];
			String sender = parts[1];
			sendAudio(sender, instruction);
		}
	}

	public void sendResponse(String message) {
		writer.println(message);
	}

	private void sendMessageToAnotherClient(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		String receiver = parts[1];
		String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
		if (!chatManager.clientExists(receiver)) {
			sendResponse("El usuario '" + receiver + "' no existe.");
		} else if (receiver.equals(sender)) {
			sendResponse("No puedes enviarte mensajes a ti mismo.");
		} else {
			ClientHandler receiverClientHandler = chatManager.getClient(receiver);
			receiverClientHandler.sendResponse(sender + " >>>  " + message);
			sendResponse("Mensaje enviado a '" + receiver + "'.");
			chatManager.saveMessage(sender, receiver, message);
		}
	}

	private void showHistory() {
		List<Message> messages = chatManager.getMessageHistory();
		for (Message savedMessage : messages) {
			sendResponse(savedMessage.toString());
		}
	}

	private void startAudioRecording(String audioName) {
		if (audioName == null || audioName.isEmpty()) {
			sendResponse("Por favor, ingrese un nombre para el archivo de audio.");
			return;
		}

		if (audioRecorder.isRecording()) {
			sendResponse("Ya se está grabando un audio.");
			return;
		}

		try {
			audioRecorder.startRecording(audioName);
		} catch (LineUnavailableException e) {
			System.out.println("Error al iniciar la grabación de audio.");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Error al guardar el archivo de audio.");
			System.out.println(e.getMessage());
		}
	}

	private void stopAudioRecording() {
		if (!audioRecorder.isRecording()) {
			sendResponse("No se está grabando ningún audio.");
			return;
		}
		audioRecorder.stopRecording();
	}

	private void sendAudio(String sender, String instruction) {
		String[] parts = instruction.split(" ");
		String audioName = parts[1];
		String receiver = parts[2];
		if (!chatManager.clientExists(receiver)) {
			sendResponse("El usuario '" + receiver + "' no existe.");
		} else if (receiver.equals(sender)) {
			sendResponse("No puedes enviarte mensajes a ti mismo.");
		} else {
			try {
				audioRecorder.sendAudio(audioName, clientSocket);
				Audio audio = audioRecorder.saveAudio();
				chatManager.saveAudio(sender, receiver, audio);
			} catch (IOException e) {
				System.out.println("Error al guardar el archivo de audio.");
				System.out.println(e.getMessage());
			}
		}
	}

}