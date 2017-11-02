import java.io.*;
import java.net.*;

public class ProxyResponseThread extends Thread {

    //Initialize the server and client socket
    private Socket server;
    private Socket client;
    private byte[] buffer = new byte[4096];

    //Constructor which takes in the two sockets and initializes it
    public ProxyResponseThread(Socket server, Socket client){
        this.server = server;
        this.client = client;
    }

    //run method which is automatically run by .start() due to Extending Thread
    @Override
    public void run() {
        try{
            //Set up the DataInput/OutputStreams for server sending response to client
            DataInputStream from_server = new DataInputStream(server.getInputStream());
            DataOutputStream to_client = new DataOutputStream(client.getOutputStream());
            int bytes_read;

            //Write the response
            while ((bytes_read = from_server.read(buffer)) != -1){
                to_client.write(buffer, 0, bytes_read);
                to_client.flush();
            }

            //close both the server and client socket
            if(server != null) {
                server.close();
            }
            if(client != null) {
                client.close();
            }
        }
        //Catch the error and display the message
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
