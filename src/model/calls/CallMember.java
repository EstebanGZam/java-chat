package model.calls;

import java.net.DatagramSocket;

public class CallMember {

    private String username;
    private DatagramSocket socket;
    private int port;

    public CallMember(String username, DatagramSocket socket) {
        this.username = username;
        this.socket = socket;
        this.port = socket.getLocalPort();
    }

    public String getUsername() {
        return username;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public int getPort() {
        return port;
    }

}