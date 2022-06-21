# Client sends 15 ping requests to server
# Each mesg contains data including keyword PING, seq. number from 3331 and timestamp.
# After sending each packet, client waits up to 600ms to receive reply
# If no reply, client assumes packet has been lost

from socket import *
from datetime import datetime
import sys
import time

serverIP = sys.argv[1]
serverPort = int(sys.argv[2])   

# create the UDP client socket
clientSocket = socket(AF_INET, SOCK_DGRAM)

rttList = []
packetsLost = 0

for i in range(15):
    localTime = datetime.now().isoformat(sep = ' ')[:-3]

    message = f"{i + 3331} PING" + str(i) + ' ' + localTime + '\r\n'
    #print(message)

    sendTime = datetime.now()
    # send message, specify destination addr + port no.
    clientSocket.sendto(message.encode(),(serverIP, serverPort))

    try:
        clientSocket.settimeout(0.6)
        modifiedMessage, serverAddress = clientSocket.recvfrom(1024)
        #print(modifiedMessage)

        # time when the message is received by client
        receiveTime = datetime.now()

        rtt = round((receiveTime - sendTime).total_seconds() * 1000)

        rttList.append(rtt)

        print(f"{3331 + i} ping to {serverIP}, seq = {i}, rtt = {rtt} ms")

        clientSocket.settimeout(None)

    except timeout:
        # no reply after 600ms, assume packet is lost
        packetsLost += 1 
        print(f'{3331 + i} ping to {serverIP}, seq = {i}, rtt = time out')

print("\n")
print(f'Minimum RTT = {min(rttList)} ms')
print(f'Maximum RTT = {max(rttList)} ms')
print(f'Average RTT = {round(float(sum(rttList) / len(rttList)))} ms')
print(f'{float(packetsLost) / 10 * 100} % of packets have been lost')

clientSocket.close()           