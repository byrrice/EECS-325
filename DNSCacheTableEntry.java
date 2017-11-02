import java.net.InetAddress;

//class that resembles DNS Cache Table Entry
public class DNSCacheTableEntry {

    //initialize hostName and starttime
    private InetAddress hostAddress;
    private long startTime;

    //Constructor starts timer as well as inserts hostName
    DNSCacheTableEntry(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
        startTime = System.currentTimeMillis();
    }

    //get the hostName you stored
    public InetAddress getHostName() {
        return hostAddress;
    }

    //gets the Time
    public long getTime() {
        return ((System.currentTimeMillis() - startTime) / (1000));
    }
}