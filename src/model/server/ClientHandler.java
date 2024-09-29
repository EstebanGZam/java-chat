package model.server;

import model.manager.ChatManager;
import model.messages.Message;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClientHandler implements Runnable {

    /** Singleton instance of ChatManager that handles the core chat logic. */
    private final ChatManager chatManager = ChatManager.getInstance();
    
    /** Username of the connected client. */
    private final String username;
    
    /** Output stream to send messages to the client. */
    private final PrintWriter writer;
    
    /** Input stream to receive messages from the client. */
    private final BufferedReader reader;
    
    /** Current group that the user has joined. */
    private String currentGroup = null;

  
    public ClientHandler(String username, BufferedReader reader, PrintWriter writer) {
        this.username = username;
        this.reader = reader;
        this.writer = writer;
    }

   
    @Override
    public void run() {
        receiveMessage();
    }

   
    public void receiveMessage() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.out.println("'" + this.username + "' se ha desconectado del chat.");
            chatManager.unregisterClient(this.username);
        }
    }

   
    private void processMessage(String message) {
        if (message.startsWith("/msg")) {
            // Mensajería privada
            String[] parts = message.split("<<<<<");
            String instruction = parts[0];
            String sender = parts[1];
            sendMessageToAnotherClient(sender, instruction);
        } else if (message.equals("/getHistory")) {
            showHistory();
        } else if (message.startsWith("/createGroup")) {
            createGroup(message);
        } else if (message.startsWith("/joinGroup")) {
            String[] parts = message.split("<<<<<");
            String instruction = parts[0];
            String sender = parts[1];
            joinGroup(sender, instruction);
        } else if (message.startsWith("/groupMsg")) {
            if (currentGroup != null) {
                String[] parts = message.split(" ", 2);
                String groupMessage = parts[1];
                sendGroupMessage(groupMessage);
            } else {
                sendResponse("No estás en un grupo. Únete a un grupo con el comando /joinGroup.");
            }
        } else if (message.equals("/listGroups")) {
            listGroups();
        }
    }

  
    private void sendMessageToAnotherClient(String sender, String instruction) {
        String[] parts = instruction.split(" ");
        String receiver = parts[1];
        String message = instruction.substring(parts[0].length() + parts[1].length() + 2);
        
        if (!chatManager.clientExists(receiver)) {
            sendResponse("El usuario '" + receiver + "' no existe.");
        } else if (receiver.equals(sender)) {
            sendResponse("No puedes enviarte mensajes a ti mismo.");
        } else {
            ClientHandler receiverClientHandler = chatManager.getClient(receiver);
            receiverClientHandler.sendResponse(sender + " >>>  " + message);
            sendResponse("Mensaje enviado a '" + receiver + "'.");
            chatManager.saveMessage(sender, receiver, message);
        }
    }

    
    private void showHistory() {
        List<Message> messages = chatManager.getMessageHistory();
        for (Message savedMessage : messages) {
            sendResponse(savedMessage.toString());
        }
    }

    /**
     * Creates a new chat group.
     * Checks if the group name already exists and creates it if valid.
     *
     * @param instruction The command received that contains the group name.
     */
    private void createGroup(String instruction) {
        String[] parts = instruction.split(" ");
        if (parts.length != 2) {
            sendResponse("Comando inválido. Use: /createGroup nombreDelGrupo");
            return;
        }
        String groupName = parts[1];
        if (chatManager.groupExists(groupName)) {
            sendResponse("El grupo '" + groupName + "' ya existe.");
        } else {
            chatManager.createGroup(groupName);
            sendResponse("Grupo '" + groupName + "' creado exitosamente.");
        }
    }

     /**
     * Allows a user to join an existing chat group.
     * If the group exists, the user joins, otherwise an error is notified.
     *
     * @param sender The username of the client attempting to join the group.
     * @param instruction The command received that contains the group name.
     */
    private void joinGroup(String sender, String instruction) {
        String[] parts = instruction.split(" ");
        if (parts.length != 2) {
            sendResponse("Comando inválido. Use: /joinGroup nombreDelGrupo");
            return;
        }
        String groupName = parts[1];
        if (chatManager.joinGroup(groupName, sender)) {
            currentGroup = groupName;
            sendResponse("Te has unido al grupo '" + groupName + "' exitosamente.");
        } else {
            sendResponse("No se pudo unir al grupo '" + groupName + "'. El grupo no existe.");
        }
    }

  /**
     * Lists all existing groups and their members.
     * Sends a list of created groups and the users in each group to the client.
     */
    private void listGroups() {
        Map<String, Set<String>> groupsInfo = chatManager.getGroupsWithMembers();
        if (groupsInfo.isEmpty()) {
            sendResponse("No hay grupos creados actualmente.");
        } else {
            StringBuilder response = new StringBuilder("Grupos existentes y sus miembros:\n");
            for (Map.Entry<String, Set<String>> entry : groupsInfo.entrySet()) {
                response.append("- ").append(entry.getKey()).append(":\n");
                if (entry.getValue().isEmpty()) {
                    response.append("  (No hay miembros)\n");
                } else {
                    for (String member : entry.getValue()) {
                        response.append("  • ").append(member).append("\n");
                    }
                }
            }
            sendResponse(response.toString());
        }
    }

     /**
     * Sends a message to the group that the user is currently joined to.
     *
     * @param message The message to be sent to the group.
     */
    private void sendGroupMessage(String message) {
        chatManager.sendGroupMessage(currentGroup, username, message);
    }

   
    public void sendResponse(String message) {
        writer.println(message);
    }
}
