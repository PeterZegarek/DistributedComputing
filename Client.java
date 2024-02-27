import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(); // Close resources in case of an exception
        }
    }

    public int receiveSubJobAndProcess() {
        try {
            String subJob = bufferedReader.readLine();
            int wordCount = countWords(subJob);
            sendWordCount(wordCount);
            return wordCount;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Indicates error
        } finally {
            closeEverything();
        }
    }

    private int countWords(String subJob) {
        try (Scanner scanner = new Scanner(subJob)) {
            int wordCount = 0;
            while (scanner.hasNext()) {
                scanner.next(); // Consume each word
                wordCount++;
            }
            return wordCount;
        }
    }

    private void sendWordCount(int wordCount) throws IOException {
        bufferedWriter.write(Integer.toString(wordCount));
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void closeEverything() {
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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Client <server_address>");
            return;
        }

        String serverAddress = args[0];

        try (Socket socket = new Socket(serverAddress, 1234)) {
            Client client = new Client(socket);
            int wordCount = client.receiveSubJobAndProcess();
            System.out.println("Received word count from server: " + wordCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
