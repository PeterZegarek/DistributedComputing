import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String subJob;
    private int wordCount;
    private Server server; // Reference to the Server instance

    public ClientHandler(Socket socket, String subJob, Server server) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.subJob = subJob;
            this.wordCount = -1; // Initialize word count to indicate not yet calculated
            this.server = server; // Set the reference to the Server instance
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
            processSubJob();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeEverything();
        }
    }

    private void processSubJob() throws IOException {
        sendSubJob();
        int wordCount = receiveWordCount();
        System.out.println("Received word count from client: " + wordCount);
        this.wordCount = wordCount; // Set the word count
        server.clientFinished(); // Notify the server that this client has finished processing
    }

    private void sendSubJob() throws IOException {
        bufferedWriter.write(subJob);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private int receiveWordCount() throws IOException {
        String response = bufferedReader.readLine();
        return Integer.parseInt(response);
    }

    private void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Method to get the word count calculated by this client handler
    public int getWordCount() {
        return wordCount;
    }
}
