package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationBroker implements CommunicationBrokerI {

	private Socket clientSocket;
	// Lee la información que llega del socket
	private BufferedReader socketReader;
	// Escribe información en el socket
	private PrintWriter writer;


	public CommunicationBroker(Socket clientSocket) throws IOException {
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
	public boolean registerClient(String clientId, Socket clientSocket) {
		return false;
	}

	@Override
	public void sendMessage(String fromClientId, String toClientId, String message) {
	}
}
