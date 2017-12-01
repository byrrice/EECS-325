import socket
import struct
import time

# Sites that worked: github.com, hltv.org, teamfortress.tv, google.com, wordpress.com, youtube.com, facebook.com, livejasmin.com, target.com, wiki.com
# sites that didn't work: yahoo.com, amazon.com, twitch.tv, cnn.com, pornhub.com, xvideos.com :(, stackoverflow.com, bing.com, instagram.com, yelp.com
# Constants
ttl = 32
timeout = 5.0
port = 33437
buffer_size = 1500
msg = "measurement for class project. questions to student ikn3@case.edu or professor mxr136@case.edu"
payload = bytes(msg + "a"*(1472 - len(msg)), "ascii")


def main():

    #open up the files
    input_file = open("target.txt")
    output_file = open("results.txt", "w")

    #finding the hosts
    hosts = input_file.read().splitlines()

    #uses for loops to go through list of hosts
    for host in hosts:

        #retrieve host address
        host_address = socket.gethostbyname(host)
        print(host_address)

        #get the requisite data (Website, Hops, Bytes, RTT)
        data = getdata(host_address)
        print(data)

        #Writes data to file else returns error message
        if data is not None:
            output_data = "Website: {}\n{}Number of Hops: {}\nRTT: {} ms\nBytes Remaining: {}\n".format(host, data[0], data[1], data[2], data[3])
            output_file.write(output_data + "\n")
        else:
            error_message = "Error Reaching {}\n".format(host)
            output_file.write(error_message + "\n")

    #close the files
    input_file.close()
    output_file.close()


def getdata(host_address):


    #attempt to reach the host until retried 3 times
    for attempt in range(0, 3):

        #the send_socket
        send_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
        print("send_socket set")
        #the recv_socket
        recv_socket = socket.socket(socket.AF_INET, socket.SOCK_RAW, socket.IPPROTO_ICMP)
        print("revc_socket set")

        #As according to MISCHA RABINOVICHSANDWICH
        send_socket.setsockopt(socket.SOL_IP, socket.IP_TTL, ttl)

        #make sure to set the timeout in order to check for the timeout later on
        send_socket.settimeout(timeout)
        recv_socket.settimeout(timeout)

        try:
            #bind the receiving socket
            recv_socket.bind(("", 0))
            print("recv_socket bound")

            #send the message and start the time to measure RTT
            send_socket.sendto(payload, (host_address, port))
            start_time = time.time()
            print("message sent")

            #Initialize recv_packet and address
            recv_packet = None
            recv_address = None

            #Get the message
            recv_packet, recv_address = recv_socket.recvfrom(1500)
            print("set recv_packet, recv_address")
            end_time = time.time()
            print("Received packet successfully.")

            #get the icmp header, type, and code
            icmp_header = recv_packet[20:28]
            icmp_header_unpacked = struct.unpack_from("bbHHh", icmp_header)
            icmp_type = icmp_header_unpacked[0]
            icmp_code = icmp_header_unpacked[1]

            #get the ttl and destination address
            ip_header = recv_packet[28:48]
            ip_header_unpacked = struct.unpack_from("!BBHHHBBH4s4s", ip_header)
            ip_ttl = ip_header_unpacked[5]
            ip_dest_address = socket.inet_ntoa(ip_header_unpacked[9])

            #get the udp destination port
            udp_dest_port = struct.unpack("!H", recv_packet[50:52])[0]

            #find number of hops, rtt, and the data remaining
            hops = ttl - ip_ttl
            rtt = 1000*(end_time - start_time) # multiply by 1000 for ms.
            remaining_data = len(recv_packet[48:])

            #have a message that will get written to file
            message = None
            if ip_dest_address == host_address and udp_dest_port == port and icmp_type == 3 and icmp_code == 3:
                message = "IP Address: {} \nPort: {} \n".format(ip_dest_address, udp_dest_port)
            else:
                message = "Received ICMP Message from wrong address.\n"

            #return the requisite data to be written to file
            return message, hops, rtt, remaining_data

        #loop again if error and print error message
        except (socket.timeout, socket.error):
            print("Socket timeout/error detected.")
            pass

        #finally close the sockets
        finally:
            send_socket.close()
            recv_socket.close()
    else:
        return None

if __name__ == "__main__":
    main()
