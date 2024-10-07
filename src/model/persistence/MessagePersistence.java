package model.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MessagePersistence {

    // Nueva ruta del archivo de historial
    private static final String FILE_NAME = Paths.get("resources", "History", "chat_history.txt").toString();

    /**
     * Asegura que el directorio 'resources/History' exista antes de guardar o leer mensajes.
     */
    private static void createDirectoriesIfNotExist() {
        Path directoryPath = Paths.get("resources", "History");
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);  // Crear directorios si no existen
            } catch (IOException e) {
                System.out.println("Error al crear directorios: " + e.getMessage());
            }
        }
    }

    /**
     * Guarda un mensaje en el archivo de texto con la información del emisor y receptor.
     * @param sender El nombre de usuario del emisor.
     * @param receiver El nombre de usuario del receptor o grupo.
     * @param message El contenido del mensaje.
     */
    public static synchronized void saveMessage(String sender, String receiver, String message) {
        createDirectoriesIfNotExist();  // Asegurarse de que las carpetas existan antes de guardar

        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Formato: mensaje de [emisor] a [receptor] : [mensaje]
            out.println("mensaje de [" + sender + "] a [" + receiver + "] : [" + message + "]");

        } catch (IOException e) {
            System.out.println("Error al guardar el mensaje en " + FILE_NAME + ": " + e.getMessage());
        }
    }

    /**
     * Guarda un archivo de audio en el historial.
     * @param sender El nombre de usuario del emisor.
     * @param receiver El nombre de usuario del receptor o grupo.
     * @param nameAudio El nombre del archivo de audio.
     */
    public static synchronized void saveAudio(String sender, String receiver, String nameAudio) {
        createDirectoriesIfNotExist();  // Asegurarse de que las carpetas existan antes de guardar

        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Formato: audio de [emisor] a [receptor] : [nombre del archivo de audio]
            out.println("audio de [" + sender + "] a [" + receiver + "] : [" + nameAudio + "]");

        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de audio en " + FILE_NAME + ": " + e.getMessage());
        }
    }

    /**
     * Imprime los últimos 5 mensajes del historial de chat en la consola.
     * Si hay menos de 5 mensajes, imprime todos los disponibles.
     * Si el archivo está vacío o no existe, imprime un mensaje indicando que no hay mensajes disponibles.
     */
    public static void printLastMessages() {
        File file = new File(FILE_NAME);

        if (!file.exists() || file.length() == 0) {
            System.out.println("No hay mensajes en el historial.");
            return;
        }

        List<String> messages = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                messages.add(line);
            }

            int totalMessages = messages.size();
            int start = Math.max(totalMessages - 5, 0);  // Índice de inicio para los últimos 5 mensajes

            System.out.println("Últimos " + Math.min(5, totalMessages) + " mensajes del historial:");

            for (int i = start; i < totalMessages; i++) {
                System.out.println(messages.get(i));
            }

        } catch (IOException e) {
            System.out.println("Error al leer el historial de mensajes: " + e.getMessage());
        }
    }
}
