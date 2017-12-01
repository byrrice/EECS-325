README FOR P1
Author: Isaac Ng, ikn3

a) I am student 5030, so I used a default port of 5030.
b) Add to linux server:
   COMMAND LINE
    1. psftp ikn3@eecslinab1.engineering.cwru.edu
    2. mkdir project1
    3. mkdir src
    4. put LOCATION ADDRESS OF FILES
    5. Type ls to check if the above files are in there
    6. rm FILENAME if you need to remove
   PUTTY
    1. Enter Server Name
    2. cd project1
    3. cd src
    4. javac proxyd.java
    5. java proxyd -port 5030
c) I used Google Chrome to test this.
   For future reference (Windows 10):
    1. Chrome, Settings, Advanced, Proxy
    2. Command Window, type in ipconfig
    3. Set port to desired port number (5030)
    4. Lan Settings: ipconfig is ipv4 address (wifi), port: 5030/desired port
d) Tested on teamfortress.tv, 4chan.org, cnn.com, case.edu, cluster41.case.edu
e) I get errors like Unknown Host Exception which is due to the hostName sometimes 
   being wrong. It still loads the requisite websites though. 