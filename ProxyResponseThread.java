import java.io.*;
import java.net.*;

public class ProxyResponseThread extends Thread {

    private Socket server;
    private Socket client;

    public ProxyResponseThread(Socket server, Socket client){
        this.server = server;
        this.client = client;
    }

    /**
     * Gets the response from the server and passes it back to the client.
     */
    @Override
    public void run() {
        try{
            //response from server
            DataInputStream from_server = new DataInputStream(server.getInputStream());
            int bytes_read;
            DataOutputStream to_client = new DataOutputStream(client.getOutputStream());
            while ((bytes_read = from_server.read()) != -1){
                to_client.write((byte)bytes_read);
                to_client.flush();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
