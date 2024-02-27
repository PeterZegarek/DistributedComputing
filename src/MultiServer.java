package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultiServer
{
    private ServerSocket serverSocket;

    public MultiServer(int port)
    {
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running");
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    public void startServer(int clients)
    {
        try
        {
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                //Starting the threads to handle clients
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    private static String[] fileTrim(String path, int clients) throws FileNotFoundException
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


        //calculate midpoint
        int division = lines.size() / clients;

        //Return appropriate part based on client's order

        if (Thread.currentThread().getName().equals("Thread-0"))
        {
            return lines.subList(0, division).toArray(new String[0]);
        }
        else if (Thread.currentThread().getName().equals("Thread-1"))
        {
            return lines.subList(division, 2 * division).toArray(new String[0]);
        }
        else if (Thread.currentThread().getName().equals("Thread-2"))
        {
            return lines.subList(2 * division, 3 * division).toArray(new String[0]);
        }
        else if (Thread.currentThread().getName().equals("Thread-3"))
        {
            return lines.subList(3 * division, 4 * division).toArray(new String[0]);
        }
        else if (Thread.currentThread().getName().equals("Thread-4"))
        {
            return lines.subList(4 * division, lines.size()).toArray(new String[0]);
        }

        String[] failure = new String[2];
        failure[0] = "You failed";
        failure[1] = "Darn";
        return failure;


        //Using two HARDCODED
//        if (Thread.currentThread().getName().equals("Thread-0"))
//        {
//            return lines.subList(0, midPoint).toArray(new String[0]);
//        }
//        else
//        {
//            return lines.subList(midPoint, lines.size()).toArray(new String[0]);
//        }

    }

    private static class ClientHandler implements Runnable
    {
        private Socket clientSocket;
        private int clients;

        public ClientHandler(Socket clientSocket, int clients)
        {
            this.clientSocket = clientSocket;
            this.clients = clients;
        }
        @Override
        public void run()
        {
            try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 DataInputStream in = new DataInputStream(clientSocket.getInputStream());)
            {
                String[] list = fileTrim("Job.txt", clients);

                //Dummy for testing
//                List<String> dummy = new ArrayList<>();
//                dummy.add(list.get(0));
//                dummy.add(list.get(1));


                //Sending the list
                out.writeObject(list);
                out.flush();

                //Read the data from the client
                int words = in.readInt();
                System.out.println("Words: " + words);

                //Closing socket
                clientSocket.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    // command line argument of how many clients you will have
    public static void main(String[] args)
    {
        //int numOfClients = args[0];

        //Hardcoding
        int numOfClients = 5;

        MultiServer server = new MultiServer(5000);
        server.startServer(numOfClients);
    }
}
