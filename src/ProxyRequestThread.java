import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProxyRequestThread extends Thread {

    //Initialize server and client socket as well as byte buffer and HashMap
    private Socket server = null;
    private Socket client = null;
    private byte[] request = new byte[2048];

    //Constructor that takes in client socket
    public ProxyRequestThread(Socket client) {
        this.client = client;
    }

    //run method that automatically intializes with .start() due to extending Thread
    @Override
    public void run() {
        try {
            //Writing from client to server
            DataInputStream from_client = new DataInputStream(client.getInputStream());
            from_client.read(request, 0, 2048);

            //Close the connection submessage
            request = closeConnection(request);

            //get serverName to connect to server
            String hostName = getHostName(request);

            //Intialize hostname and DNSCachetableEntry
            InetAddress hostAddress;
            DNSCacheTableEntry cacheEntry;

            //does the hashmap contain the hostName?
            if (proxyd.cache.containsKey(hostName)){

                //check if entry is more than 30 seconds old if it does contain the hostName
                if ((cacheEntry = proxyd.cache.get(hostName)).getTime() < 30){

                    //If it hasn't expired/timedout, take the address and return that you have found it
                    hostAddress = cacheEntry.getHostAddress();
                    String finalHostAddress = hostAddress.getHostAddress();
                    System.out.println("Cache hit: " + hostAddress);
                    server = new Socket(finalHostAddress, 80);
                }
                else{
                    //It expired, so remove it and add a new entry for it
                    hostAddress = InetAddress.getByName(hostName);
                    proxyd.cache.remove(hostName);
                    proxyd.cache.put(hostName, new DNSCacheTableEntry(hostAddress));
                    server = new Socket(hostName, 80);
                }
            } else {
                //it was never in the cache, so put it in
                hostAddress = InetAddress.getByName(hostName);
                proxyd.cache.put(hostName, new DNSCacheTableEntry(hostAddress));
                server = new Socket(hostName, 80);
            }

            //write to server
            DataOutputStream to_server = new DataOutputStream(server.getOutputStream());
            to_server.write(request);
            to_server.flush();

            // Send response from server to client
            (new Thread(new ProxyResponseThread(server, client))).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //returns the serverName without the Host: in front of it
    private String getHostName(byte[] message) {
        String hostName = "";
        String[] serverParts;
        String string = new String(message, StandardCharsets.UTF_8);
        String[] parts = string.split("\r\n");
        int i = 0;
        while (i < parts.length) {
            if (parts[i].contains("Host: ")) {
                hostName = parts[i].replaceAll("Host: ", "");
                serverParts = hostName.split(":");
                hostName = serverParts[0];
                System.out.println(serverParts[0]);
            }
            i++;
        }
        return hostName;
    }

    //shoutout to https://stackoverflow.com/questions/18571223/how-to-convert-java-string-into-byte
    //writes the Connection: close in place of Connection: keep-alive or Proxy-connection: keep-alive
    private byte[] closeConnection(byte[] message) {
        String string = new String(message, StandardCharsets.UTF_8);
        String[] parts = string.split("\r\n");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < parts.length) {
            System.out.println(parts[i]);
            if (parts[i].contains("Connection: keep-alive") || parts[i].contains("Proxy-Connection: keep-alive")) {
                sb.append("Connection: close" + "\r\n");
            }
            else
                sb.append(parts[i] + "\r\n");
            i++;
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}

