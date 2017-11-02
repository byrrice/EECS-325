import java.io.*;
import java.net.*;
import java.util.HashMap;

//Project 1 Proxy
//Author: Isaac Ng
public class proxyd {

    //default port number is set to 5030 as well as port number
    private static int default_port_number = 5030;
    private static int port_number;
    public static HashMap<String, DNSCacheTableEntry> cache = new HashMap<>();

    //Main method takes in form: java proxyd -port NUMBER
    public static void main(String[] args)
    {
        //Try to set the port number to the argument as specified in main method entry
        try {
            port_number = Integer.parseInt(args[1]);
            System.out.println("Starting on port " + port_number);
        }
        //Otherwise, set port number to default port number as well as print out error message
        catch (Exception e) {
            port_number = default_port_number;
            System.out.println("Error Raised! Switching to default port " + port_number);
            e.printStackTrace();
        }
        //Try to set the serversocket to the desired port number
        try
        {
            ServerSocket server = new ServerSocket(port_number);

            while(true)
            {
                //Accept the connection
                Socket client = server.accept();
                //Print out confirmation message
                if(client.isConnected())
                {
                    System.out.println("Proxy is currently listening to client on port " + port_number);
                }
                //Now send the request to the ProxyRequestThread
                (new Thread(new ProxyRequestThread(client))).start();
            }
        }
        //Catch error and print out the error message
        catch(IOException io)
        {
            io.printStackTrace();
        }
    }
}