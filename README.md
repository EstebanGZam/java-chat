# Chat Application

## Team Members

- Esteban Gaviria Zambrano - A00396019
- Jose Manuel Cardona - A00399980
- Juan Manuel Díaz - A00394477
- Juan Camilo Muñoz Barco - A00399199

## Introduction

This is a Java chat application that allows multiple clients to connect to a server and communicate with each other. The
application supports private messaging, group chats, audio messages, and voice calls.

## Features

- Private messaging
- Group chat creation and messaging
- Audio recording and playback (for individual users and groups)
- Voice calls between two users
- Group voice calls
- Message history

## Prerequisites

- Java Development Kit (JDK) 23
- A computer network for multiple devices to connect
- Microphone and speakers for audio and call functions

### Server Setup

1. Navigate to the Server class in the model.server package.
2. Modify the IP constant to match the IP address of the device that will act as the server:
    ```java
    public static final String IP = "192.168.1.100"; // Replace with your server's IP address
    ```

### Client Setup

Clients don't need to modify any code. They will use the IP address and port specified in the Server class.

## Running the Application

### Starting the Server

1. Run the main method in the Server class.
2. You should see a message indicating that the server has started and is listening on the specified port.

### Starting a Client

1. Run the main method in the Client class.
2. The client will attempt to connect to the server.
3. If successful, you'll be prompted to enter a username.

## Using the Chat Application

Once connected, you can use the following commands:

- *Send a message to everyone:* Just type your message and press Enter.
- *Create a new group:* /createGroup <group_name>
- *List existing groups:* /listGroups
- *Join a group:* /joinGroup <group_name>
- *Send a private message:* /msg <username> <message>
- *Send a message to a group:* /groupMsg <group_name> <message>
- *Record an audio message for a user:* /record <username>
- *Record an audio message for a group:* /record-group <group_name>
- *Stop recording audio:* /stop-audio
- *Play an audio message:* /play <audio_name>
- *Start a call with a user:* /call <username>
- *Start a group call:* /groupCall <group_name>
- *Exit the chat:* exit

## Troubleshooting

- Ensure all devices are on the same network.
- Check firewall settings if clients can't connect to the server.
- Verify that the correct IP address is used in the Server class.