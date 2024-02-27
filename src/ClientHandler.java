package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

class ClientHandler extends Thread {
    private Socket clientSocket;

    // need to have the data that will be sent to this specific client
    private List<String> clientData;

    public ClientHandler(Socket clientSocket, List<String> clientData) {
        this.clientSocket = clientSocket;
        this.clientData = clientData;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            // Handle communication with the client
            // Read from 'in', process data, and send responses using 'out'

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
    