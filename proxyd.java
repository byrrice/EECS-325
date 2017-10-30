import java.io.*;
import java.net.*;


public class proxyd {

    private static int default_port_number = 5030;
    private static int port_number;

    public static void main(String[] args)
    {
        try {
            port_number = Integer.parseInt(args[1]);
            System.out.println("Starting on port " + port_number);
        }
        catch (Exception e) {
            port_number = default_port_number;
            System.out.println("Error Raised! Switching to default port " + port_number);
            e.printStackTrace();
        }
        try
        {
            ServerSocket server = new ServerSocket(port_number);

            while(true)
            {
                Socket client = server.accept();
                if(client.isConnected())
                {
                    System.out.println("Proxy is currently listening to client on port " + port_number);
                }
                (new Thread(new ProxyRequestThread(client))).start();
            }
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
    }
}