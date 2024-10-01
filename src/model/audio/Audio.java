package model.audio;

import java.io.File;

public class Audio {
	private final File audioFile; // El archivo de audio
	private final String fileName; // Nombre del archivo
	private final long fileSize; // Tamaño del archivo en bytes

	public Audio(File audioFile) {
		this.audioFile = audioFile;
		this.fileName = audioFile.getName();
		this.fileSize = audioFile.length();
	}

	public File getAudioFile() {
		return audioFile;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	@Override
	public String toString() {
		return "Audio{" +
				"fileName='" + fileName + '\'' +
				", fileSize=" + fileSize +
				'}';
	}
}