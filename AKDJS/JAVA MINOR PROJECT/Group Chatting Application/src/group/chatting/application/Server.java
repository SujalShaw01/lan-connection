package group.chatting.application;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable {

    Socket socket;
    public static Vector<BufferedWriter> clients = new Vector<>();  // Use Vector to store client writers

    public Server(Socket socket) {
        try {
            this.socket = socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ...

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Add the client writer to the vector
            clients.add(writer);

            // Display a message when a client connects
            System.out.println("Client connected: " + socket.getInetAddress());

            // Send a welcome message to the connected client
            writer.write("Welcome to the AKDJS chat!\r\n");
            writer.flush();

            while (true) {
                String data;
                try {
                    data = reader.readLine();

                    // Check if the data is null or empty, indicating that the client has disconnected
                    if (data == null || data.isEmpty()) {
                        // Display a message when a client disconnects
                        System.out.println("Client disconnected: " + socket.getInetAddress());

                        // Remove the writer from the client list
                        clients.remove(writer);

                        // Notify other clients about the disconnection
                        broadcast("Client disconnected: " + socket.getInetAddress());

                        // Exit the loop as the client has disconnected
                        break;
                    }

                    System.out.println("Received from " + socket.getInetAddress() + ": " + data);

                    // Broadcast the message to all connected clients
                    broadcast(data);
                } catch (SocketException se) {
                    // Handle the SocketException (Connection reset) here
                    // Display a message or perform any necessary cleanup
                    System.out.println("Client disconnected : " + socket.getInetAddress());

                    // Remove the writer from the client list
                    clients.remove(writer);


                    // Exit the loop as the client has disconnected abruptly
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// ...


    // Broadcast a message to all connected clients
    public static void broadcast(String message) {
        for (BufferedWriter client : clients) {
            try {
                client.write(message + "\r\n");
                client.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2003);
        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            Thread thread = new Thread(server);
            thread.start();
        }
    }
}
