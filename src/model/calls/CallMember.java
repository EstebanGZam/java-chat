package model.calls;

import util.call.CallAudioReceiver;
import util.call.CallSenderAudio;

import java.net.DatagramSocket;

public class CallMember implements Runnable {

	private final Call call;
	private final String username;
	private final DatagramSocket socket;
	private final int clientPort;
	private final String clientInetAddress;
	private final boolean isHost;
	boolean callFinished = false;

	public CallMember(Call call, String username, DatagramSocket socket, int clientPort, String clientInetAddress, boolean isHost) {
		this.call = call;
		this.username = username;
		this.socket = socket;
		this.clientPort = clientPort;
		this.isHost = isHost;
		this.clientInetAddress = clientInetAddress;
	}

	public String getUsername() {
		return username;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public int getClientPort() {
		return clientPort;
	}

	public String getClientInetAddress() {
		return clientInetAddress;
	}

	public boolean isHost() {
		return isHost;
	}

	@Override
	public void run() {
		CallSenderAudio callSenderAudio = new CallSenderAudio(socket);
		CallAudioReceiver callAudioReceiver = new CallAudioReceiver(socket);
		while (!callFinished) {
			byte[] buffer = callAudioReceiver.receiveAudio();
			for (CallMember callMember : call.getCallMembers().values()) {
				if (!callMember.equals(this)) {
					callSenderAudio.sendAudio(callMember.getClientInetAddress(), callMember.getClientPort(), buffer.length, buffer);
				}
			}
		}
	}

	public void finishCall() {
		callFinished = true;
	}
}