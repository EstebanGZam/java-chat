package model.calls;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class CallMember {

    private String username;
    private AudioFormat format;
    private DataLine.Info infoMicrophone;
    private DataLine.Info infoSpeaker;
    private TargetDataLine microphone;
    private SourceDataLine speaker;
    private byte[] buffer;
    private Socket socket;

    public CallMember(String username, Socket socket) {
        this.username = username;
        this.format = new AudioFormat(44100, 16, 1, true, true);
        this.infoMicrophone = new DataLine.Info(TargetDataLine.class, format);
        this.buffer = new byte[1024];
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void startMicrophone() {
        try {
            microphone = (TargetDataLine) AudioSystem.getLine(infoMicrophone);
            microphone.open(format);
            microphone.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMicrophone() {
        microphone.stop();
        microphone.close();
    }

    public void startSpeaker() {
        try {
            infoSpeaker = new DataLine.Info(SourceDataLine.class, format);
            speaker = (SourceDataLine) AudioSystem.getLine(infoSpeaker);
            speaker.open(format);
            speaker.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopSpeaker() {
        speaker.stop();
        speaker.close();
    }

    public void talk(Socket socket) throws IOException {
        startMicrophone();

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);

        while (true) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);

            bos.write(buffer, 0, bytesRead);
            bos.flush();
        }
    }

    public void listen(Socket socket) throws IOException {
        startSpeaker();

        InputStream is = socket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);

        while (true) {
            int bytesRead = bis.read(buffer, 0, buffer.length);

            speaker.write(buffer, 0, bytesRead);
        }
    }

}