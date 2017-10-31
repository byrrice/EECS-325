import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProxyRequestThread extends Thread {

    private static Hashtable<String, InetAddress> cache = new Hashtable<String, InetAddress>();
    private Socket server = null;
    private Socket client = null;
    private byte[] request = new byte[2048];

    public ProxyRequestThread(Socket client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        try
        {
            //Writing from client to server
            DataInputStream from_client = new DataInputStream(client.getInputStream());
            from_client.read(request, 0, 2048);

            request = closeConnection(request);
            //get serverName to connect to said host
            String serverName = getServerName(request);
            //InetAddress address = getHostName(serverName);
            //System.out.println(address);
            //InetAddress hostname = getHostName(serverName);


            server = new Socket(serverName, 80);

            //write to server
            DataOutputStream to_server = new DataOutputStream(server.getOutputStream());
            to_server.write(request);
            to_server.flush();

            // Send response from server to client
            (new Thread(new ProxyResponseThread(server, client))).start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getServerName(byte[] request) {
        String serverName = "";
        String string = new String(request, StandardCharsets.UTF_8);
        String[] parts = string.split("\r\n");
        int i = 0;
        while (i < parts.length) {
            if (parts[i].contains("Host: ")){
                serverName = parts[i].replaceAll("Host: ", "");
                System.out.println(serverName);
            }
            i++;
        }
        return serverName;
    }






    //shoutout to https://stackoverflow.com/questions/18571223/how-to-convert-java-string-into-byte
    private byte[] closeConnection(byte[] message){
        //convert from byte to String
        String output = new String(message, StandardCharsets.UTF_8);
        output.replaceAll("Connection: keep-alive", "Connection: close");
        output.replaceAll("Proxy-connection: keep-alive","Connection: close" );
        //back to bytes
        return output.getBytes(StandardCharsets.UTF_8);
    }


}