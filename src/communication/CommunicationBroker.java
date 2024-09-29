package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationBroker implements CommunicationBrokerI {
	// Lee la información que llega del socket
	private final BufferedReader socketReader;
	// Escribe información en el socket
	private final PrintWriter writer;


	public CommunicationBroker(Socket clientSocket) throws IOException {
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
		return socketReader.readLine();
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
		}else if(instruction.startsWith("/groupMsg")){
			sendGroupMessage(instruction);
		}
	}

	@Override
	public void sendMessageToAnotherClient(String instruction) {
		writer.println(instruction);
	}

	@Override
	public void showHistory(String instruction) {
		writer.println(instruction);
	}

	@Override
	public void createGroup(String instruction) { writer.println(instruction); }

	@Override
	public void listGroups(String instruction) { writer.println(instruction); }

	@Override
	public void joinGroup(String instruction) { writer.println(instruction); }

	public void sendGroupMessage(String instruction){writer.println(instruction);}
}