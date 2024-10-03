package model.persistence;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MessagePersistence {

    private static final String FILE_NAME = Paths.get("resources", "History", "chat_history.txt").toString();

    /**
     * Saves a message to a single text file with the sender and receiver information.
     * @param sender The username of the sender.
     * @param receiver The username of the receiver or group name.
     * @param message The message content.
     */
    public static synchronized void saveMessage(String sender, String receiver, String message) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Formato: mensaje de [emisor] a [receptor] : [mensaje]
            out.println("mensaje de [" + sender + "] a [" + receiver + "] : [" + message + "]");

        } catch (IOException e) {
            System.out.println("Error while saving message to " + FILE_NAME + ": " + e.getMessage());
        }
    }

    /**
     * Prints the last 5 messages from the chat history file to the console.
     * If there are fewer than 5 messages, it prints all available messages.
     * If the file is empty or doesn't exist, it prints a message indicating no messages are available.
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
            int start = Math.max(totalMessages - 5, 0);  // Start index for the last 5 messages

            System.out.println("Últimos " + Math.min(5, totalMessages) + " mensajes del historial:");

            for (int i = start; i < totalMessages; i++) {
                System.out.println(messages.get(i));
            }

        } catch (IOException e) {
            System.out.println("Error al leer el historial de mensajes: " + e.getMessage());
        }
    }
}
