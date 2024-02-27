package src;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private ObjectInputStream input = null;
    private DataOutputStream out = null;

    public Client(String address, int port)
    {
        //establishing connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");


            List<String> list = new ArrayList<>();

            //Waiting for the input of data
            while(list.isEmpty())
            {
                if (input != null)
                {
                    list = (List<String>) input.readObject();
                }
            }

            //Count words
            out.write(sendWordCount(list));

            //getting input
            //input = new DataInputStream(System.in);

            //Sends output to the socket
            //out = new DataOutputStream(socket.getOutputStream());
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

    public int sendWordCount(List<String> list)
    {
        int wordCount = list.stream()
                                .mapToInt(line -> line.split("\\s+").length)
                                    .sum();
        return  wordCount;
    }
    public static int wordCount(String path) throws FileNotFoundException
    {
        // File object
        File file = new File(path);

        // file existence check
        if(!file.exists())
            throw new FileNotFoundException();

        Scanner reader = new Scanner(file);

        int wordCount = 0;

        // 1. read file line by line, count # of words, accumulate result
        // 2. this approach is faster for large file, limits stack overflow error
        while(reader.hasNext())
            wordCount += reader.nextLine().trim().split("\\s+").length;

        reader.close();
        return wordCount;
    }

	public static void main(String[] args)
	{
        Client client = new Client("127.0.0.1", 5000);


//		try
//		{
//			System.out.println(wordCount("Job.txt"));
//		}
//		catch (FileNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}