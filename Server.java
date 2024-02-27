import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;
    private List<ClientHandler> connectedClients;
    private int totalClientsExpected;
    private String filePath; // Add filePath variable
    private int finishedClients; // Counter for finished clients

    public Server(ServerSocket serverSocket, int totalClientsExpected, String filePath) {
        this.serverSocket = serverSocket;
        this.totalClientsExpected = totalClientsExpected;
        this.connectedClients = new ArrayList<>();
        this.filePath = filePath; // Initialize filePath variable
        this.finishedClients = 0;
    }

    public void startServer() {
        try {
            List<String> subJobs = splitFileIntoSubJobs(filePath, totalClientsExpected);

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                if (connectedClients.size() < totalClientsExpected && !subJobs.isEmpty()) {
                    // Assign a sub-job to each client
                    String subJob = subJobs.remove(0); // Remove the first sub-job
                    ClientHandler clientHandler = new ClientHandler(socket, subJob, this);
                    connectedClients.add(clientHandler);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } else {
                    System.out.println("Maximum number of clients reached or no more sub-jobs. Ignoring connection.");
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void clientFinished() {
        finishedClients++;
        if (finishedClients == totalClientsExpected) {
            // All clients have finished processing their sub-jobs
            collectWordCounts();
        }
    }

    private void collectWordCounts() {
        // Implementation to collect word counts from all clients
        int totalWordCount = 0;
        for (ClientHandler client : connectedClients) {
            totalWordCount += client.getWordCount();
        }
        System.out.println("Total word count from all clients: " + totalWordCount);
    }

    private List<String> splitFileIntoSubJobs(String filePath, int totalClients) throws IOException {
        List<String> subJobs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder subJobBuilder = new StringBuilder();
            int clientCount = 0;
            while ((line = br.readLine()) != null) {
                subJobBuilder.append(line).append("\n");
                if (subJobBuilder.length() > 100 || !br.ready()) { // Adjust the size of sub-jobs as needed
                    subJobs.add(subJobBuilder.toString());
                    subJobBuilder.setLength(0); // Clear the StringBuilder for the next sub-job
                    clientCount++;
                    if (clientCount >= totalClients) {
                        break; // Break if we have enough sub-jobs for all clients
                    }
                }
            }
        }
        return subJobs;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            int totalClientsExpected = 2; // Adjust according to your requirements
            String filePath = "C:\\Users\\Hunte\\P1\\src\\Job.txt"; // Specify the file path
            Server server = new Server(serverSocket, totalClientsExpected, filePath);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
