package util.call;

import model.server.Server;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;

public class CallAudioRecorder {
	// Atributos de la clase
	private TargetDataLine microphone;
	private ByteArrayOutputStream audioOutputStream; // Se asegura que esté accesible en todos los métodos
	private boolean isRecording;
	private byte[] buffer;
	private volatile int bytesRead;

	// Método para iniciar la grabación
	public void startSendingOfVoice(DatagramSocket datagramSocket, int serverPort) {
		initRecorder();
		// Iniciar un hilo para capturar el audio
		Thread captureThread = new Thread(() -> {
			CallSenderAudio callSenderAudio = new CallSenderAudio(datagramSocket);

			this.buffer = new byte[1024]; // Buffer de grabación
			while (isRecording) {
				this.bytesRead = microphone.read(buffer, 0, buffer.length);
				try {
					callSenderAudio.sendAudio(Server.IP, serverPort, buffer);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		captureThread.start();

	}

	// Método para obtener los datos grabados
	public byte[] getRecordedData() {
		byte[] audioData = audioOutputStream.toByteArray(); // Obtener los datos del audio grabado
		audioOutputStream.reset(); // Limpiar el stream después de leer
		return audioData;
	}

	public int getBytesRead() {
		return bytesRead;
	}

	public TargetDataLine getMicrophone() {
		return microphone;
	}

	private void initRecorder() {
		try {
			AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			microphone.start();
		} catch (LineUnavailableException e) {
			System.err.println("Error al iniciar la grabación: " + e.getMessage());
		}
	}

	// Método para detener la grabación
	public void stopRecording() {
		isRecording = false;
		microphone.stop();
		microphone.close();
		try {
			audioOutputStream.close();
		} catch (IOException e) {
			System.err.println("Error al cerrar el stream de audio: " + e.getMessage());
		}
	}

//    public void sendBytesRead(PrintWriter writer, String callID, String sourceUser, OutputStream os) {
//        new Thread(() -> {
//            while (isRecording) {
//                writer.println("CALL");
//                writer.println(callID + ":::" + sourceUser);
//                try {
//                    os.write(buffer, 0, bytesRead);
//                    os.flush();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}
