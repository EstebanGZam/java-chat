package model.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class MessagePersistence {

    // Ruta relativa para el archivo único dentro de la carpeta History del proyecto
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
}
