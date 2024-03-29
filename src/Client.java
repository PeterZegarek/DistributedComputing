package src;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/*
 * Peter Zegarek, Christopher Anzilotti, Hunter Yocum
 */

public class Client
{
    /*
     * @param path path to the file
     * @return total number of words in the file references by path
     * @throws FileNotFoundException if file doesn't exist
     * @precondition file must be a valid text file
     * @postcondition wordCount represents total number of words separated by space
     */

    private Socket socket = null;
    private static ObjectInputStream input = null;
    private static DataOutputStream out = null;

    public Client(String address, int port)
    {
        //establishing connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            //Initialize Input and Output
            input = new ObjectInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String[] list = (String[]) input.readObject();

            //Calculate word count
            int wordCount = calculateWordCount(list);
            out.writeInt(wordCount);
            out.flush();

            //Close connection
            socket.close();

        } catch (UnknownHostException unknownHostException)
        {
            System.out.println(unknownHostException);
            return;
        } catch (IOException ioException)
        {
            System.out.println(ioException);
            return;
        } catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }


    private int calculateWordCount(String[] list)
    {
        int wordCount = 0;
        for(String line : list)
        {
            String[] words = line.trim().split("\\s+");
            wordCount += words.length;
        }
        return wordCount;
    }

	public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        System.out.println("What IP address are you connecting to? Please enter it.");
        Scanner sc = new Scanner(System.in);
        String ip = sc.nextLine();
        Client client = new Client(ip, 5000);
        sc.close();

        // Client client = new Client("localhost", 5000);

    
	}

}