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
    private int connectedClients;
    public static int totalWords;
    public static int finishedClients;

    public static String filePath;

    public static long startTime;

    // constructor for the server
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
            boolean inputReceived = false;
            // hardcoded to finish when all clients are done
            while (finishedClients != 5)
            {

                Socket clientSocket = null;
                // if there are not 5 clients yet, accept connections
                if (connectedClients != 5){
                    clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);
                    connectedClients++;
                }

                // if there are 5 clients connected, ask if they want to start the job
                if (connectedClients == 5 && !inputReceived){
                    Scanner sc = new Scanner(System.in);
                    System.out.println("All clients connected.");
                    System.out.println("Enter file name to begin transmitting file to clients.");
                    filePath = sc.nextLine();
                    sc.close();
                    inputReceived = true;
                    startTime = System.currentTimeMillis();
                }

                //Starting the threads to handle clients
                if (clientSocket != null){
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                }

            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    // this is called by each thread to take a certain part of the file and send to the client
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

    }

    // other class to handle the client. These are spun off as threads
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
            // while there is no filepath wait
            while (filePath == null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 DataInputStream in = new DataInputStream(clientSocket.getInputStream());)
            {
                // if we get filePath working use that
                String[] list = fileTrim(filePath, clients);
                // String[] list = fileTrim("Job.txt", clients);

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

                totalWords += words;
                finishedClients += 1;

                //Closing socket
                clientSocket.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {

        //Hardcoding num of clients
        int numOfClients = 5;

        // create and start server
        MultiServer server = new MultiServer(5000);
        server.startServer(numOfClients);

        // until there are 5 finished clients wait
        while (finishedClients != 5){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // print total number of words
        System.out.println("Total number of words is: " + totalWords);
        // print total time taken
        System.out.println("Total time taken was: " + (System.currentTimeMillis() - startTime) + " ms" );



    }
}
