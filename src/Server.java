package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    // needs to be running while all answers have not been received

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private ObjectOutputStream out = null;



    public Server(int port)
    {
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server is starting");


            System.out.println("Waiting for connection");

            //Accepts client connection based on port number
            socket = server.accept();
            System.out.println("Client Accepted");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());


        } catch (IOException ioException)
        {
            System.out.println(ioException);
        }
    }

    // this converts the file to a list
    private static ArrayList<List<String>> fileTrim(String path, int numberOfClients) throws FileNotFoundException
    {
        // File object
        File file = new File(path);

        // file existence check
        if(!file.exists())
            throw new FileNotFoundException();

        Scanner reader = new Scanner(file);

        //Creating a list of lines
        List<String> lines = new ArrayList<>();

        //Reading each line and adding them to a List
        while(reader.hasNextLine())
        {
            String line = reader.nextLine();
            lines.add(line);
        }


        // Calculate the size of each portion
        int portionSize = lines.size() / numberOfClients;
        int remainder = lines.size() % numberOfClients; // Handle remainder
        
        // Initialize the list of portions
        ArrayList<List<String>> portions = new ArrayList<>();

        // Create portions
        int fromIndex = 0;
        for (int counter = 0; counter < numberOfClients; counter++) {
            int toIndex = fromIndex + portionSize + (counter < remainder ? 1 : 0);
            List<String> portion = lines.subList(fromIndex, toIndex);
            portions.add(portion);
            fromIndex = toIndex;
        }

        return portions;

    }

    // this writes to the output stream so the client can get it
    public void sendData(List<String> list)
    {
        try
        {
            //Sends the list to the client
            out.writeObject(list);
            out.flush();

            //Read the response
            int words = in.readInt();
            System.out.println("Words received: " + words);

            socket.close();

        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    // need to provide command line argument of how many clients are expected
    public static void main(int args[])
    {
        int expectedClients = args[0];

        // this will increase as clients connect
        int connectedClients = 0;

        //before we even look for connections, the file will be split into an even number of jobs for each client to take on
        ArrayList<List<String>> jobs = new ArrayList<List<String>>(expectedClients);
        try {
            jobs = fileTrim("Job.txt", expectedClients);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // make sure port numbers are consistent
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (connectedClients < expectedClients) {
                

                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle the client connection
                Thread clientThread = new ClientHandler(clientSocket, jobs.get(connectedClients));
                clientThread.start();
                connectedClients++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //old code
        //Starting Server
        // Server server1 = new Server(5000);


        // List<String> list = new ArrayList<>();

        // try
        // {
        //     list = server1.fileTrim("Job.txt");
        // } catch (FileNotFoundException fileNotFoundException)
        // {
        //     System.out.println(fileNotFoundException);
        // }


        // if(!list.isEmpty())
        // {
        //     server1.sendData(list);


        //     //Wait for response
        //     //TODO get data back
        // }
    }
}
