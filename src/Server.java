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

    private List<String> fileTrim(String path) throws FileNotFoundException
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


        return lines;


    }

    public void sendData(List<String> list)
    {
        List<String> dummy = new ArrayList<>();
        dummy.add(list.get(0));
        dummy.add(list.get(1));

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
    public static void main(String args[])
    {
        //Starting Server
        Server server1 = new Server(5000);

        //Using Scanner to do it in steps
        Scanner scanner = new Scanner(System.in);

        List<String> list = new ArrayList<>();

        try
        {
            list = server1.fileTrim("Job.txt");
        } catch (FileNotFoundException fileNotFoundException)
        {
            System.out.println(fileNotFoundException);
        }

        System.out.println("Send data?");
        scanner.next();

        if(!list.isEmpty())
        {
            server1.sendData(list);


            //Wait for response
            //TODO get data back
        }
        // how many responses we have gotten
        //int answers = 0;

        // hard coded for now
//        while (answers < 6) {
//
//            // hard coding port number
//            int portNumber = 4444;
//
//
//
//            // establish connections with clients if haven't been made
//
//            // send out the job if it hasn't been sent
//
//            // receive the job
//
//            // return result after
//        }
    }
}
