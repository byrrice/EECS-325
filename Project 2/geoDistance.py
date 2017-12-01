import geoip2.database
import socket
import urllib.request
from math import *


def main():

    #Open Requisite Files
    input_file = open("target.txt")
    output_file = open("distance_results.txt", "w")

    # https://pypi.python.org/pypi/geoip2
    # find our ip, latitude and longitude
    reader = geoip2.database.Reader("GeoLite2-City.mmdb")
    our_ip = urllib.request.urlopen("http://checkip.amazonaws.com").read()
    our_ip = our_ip.decode("ascii").strip()
    our_city = reader.city(our_ip)
    our_latitude = our_city.location.latitude
    our_longitude = our_city.location.longitude

    #Write your location into the file
    output_file.write("Our IP: {}\nLatitude: {}\nLongitude: {}\n".format(our_ip, our_latitude, our_longitude))
    output_file.write("\n")

    #For each host, get the address, latitude, longitude, and distance, then write them to file
    hosts = input_file.read().splitlines()
    for host in hosts:
        host_address = socket.gethostbyname(host)
        host_city = reader.city(host_address)
        host_latitude = host_city.location.latitude
        host_longitude = host_city.location.longitude
        distance = haversine_distance(our_latitude, our_longitude, host_latitude, host_longitude)
        output_file.write("Host: {}\nLatitude: {}\nLongitude: {}\nDistance: {} km\n".format(host, host_latitude, host_longitude, distance))
        output_file.write("\n")

    # close the requisite files
    input_file.close()
    output_file.close()


#Find the haversine distance
#https://stackoverflow.com/questions/4913349/haversine-formula-in-python-bearing-and-distance-between-two-gps-points
def haversine_distance(our_latitude, our_longitude, host_latitude, host_longitude):

    #finding the haversine distance according to the formula
    lat1, long1, lat2, long2 = map(radians, [our_latitude, our_longitude, host_latitude, host_longitude])
    long_distance = long2 - long1
    lat_distance = lat2 - lat1
    a = sin(lat_distance/2)**2 + cos(lat1) * cos(lat2) * sin(long_distance/2)**2
    c = 2 * asin(sqrt(a))
    km = 6367 * c
    return km

if __name__ == "__main__":
    main()
