package model.calls;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CallMember {

    private String username;
    private DatagramSocket socket;
    private int port;
    private String ip;
    private boolean isHost;

    public CallMember(String username, DatagramSocket socket, boolean isHost) {
        this.username = username;
        this.socket = socket;
        this.port = socket.getLocalPort();
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public String getIp() {
        return ip;
    }

    public boolean isHost() {
        return isHost;
    }

}