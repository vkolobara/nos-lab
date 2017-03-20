import os
import time
import multiprocessing
import threading
import random
from multiprocessing import Process

n_processes = 10

class MyProcess:
    
    def __init__(self, index, read_pipes, write_pipes):
        self.index = index
        self.respCount = 0
                
        self.clock = 0
        
        self.read_pipes = read_pipes
        self.write_pipes = write_pipes
        
        self.currRequest = None
        
        self.deferedResponses = []
                
            
    def closePipes(self):
        for pipe in self.write_pipes:
            pipe.close()
    
    def enterCriticalSection(self):
        while self.respCount != (n_processes-1):
            time.sleep(0.3)
        self.doCriticalSection()
        self.exitCriticalSection()
        
    def doCriticalSection(self):
        print ("CRITICAL SECTION: " + str(self.index))
        
        for i in range(5):
            print (i)
            
        print
                
    def requestEntryCriticalSection(self):
        self.currRequest = "REQ " + str(self.index) + " " + str(self.clock)
        for pipe in self.write_pipes:
            print ("REQUEST: " + self.currRequest + " " + pipe.name)
            pipe.write(self.currRequest + "\n")
            pipe.flush()
            
    def respondRequestEntry(self, message):
        index = int(message.split(" ")[1])
        clock = int(message.split(" ")[2])

        my_clock = self.clock
        
        if self.currRequest:
            my_clock = int(self.currRequest.split(" ")[2])
        
        if self.currRequest == None or my_clock > clock or (my_clock == clock and self.index > index) :
            for pipe in self.write_pipes:
                if pipe.name == str(self.index) + str(index):
                    pipe.write(self.createResponse(message))
                    pipe.flush()
                    break
        else:
            self.deferedResponses.append(message)
            
    
    def getMessage(self, pipe):
        message = pipe.readline()[:-1]
        print("RECV: " + message)
        if message:
            self.clock = max(int(message.split(" ")[1]), self.clock) + 1
            return message
    
    def createResponse(self, request):
        msgSplit = request.split(" ")
        return "RESP " + str(self.index) + " " + msgSplit[2] + "\n"
        
    def exitCriticalSection(self):
        self.respCount = 0
        self.currRequest = None
        for defered in self.deferedResponses:
            msgSplit = defered.split(" ")
            index = int(msgSplit[1])
            for pipe in self.write_pipes:
                if pipe.name == str(self.index) + str(index):
                    pipe.write(self.createResponse(defered))
                    pipe.flush()
                    break
        self.deferedResponses = []
            

def readMessage(proc):  
    while True:
        time.sleep(0.3)
        for pipe in proc.read_pipes:
            msg = proc.getMessage(pipe)
            if not msg:
                return
            msgSplit = msg.split(" ")
            
            if msgSplit[0] == "REQ":
                proc.respondRequestEntry(msg)
            elif msgSplit[0] == "RESP":
                proc.respCount += 1

def process(index, totalNum):
    
    read_pipes = []
    write_pipes = []
    time.sleep(1)
    for i in range(totalNum):
        if i != index:
            fifo = open(str(index) + str(i), 'w+', 0)   
            write_pipes.append(fifo)
    
    for i in range(totalNum):
        if i != index:
            fifo = open(str(i) + str(index), 'r', 0)
            read_pipes.append(fifo)
            
    proc = MyProcess(index, read_pipes, write_pipes)

    t = threading.Thread(target=readMessage, args = (proc, ))
    t.start()

    for i in range(5):
        time.sleep(0.3)
        proc.requestEntryCriticalSection()
        proc.enterCriticalSection()

    time.sleep(1)
    proc.closePipes()
    
if __name__ == "__main__":
    processes = []
    
    for i in range(n_processes):
        processes.append(Process(target=process, args=(i, n_processes)))
        for j in range(i+1, n_processes):
            if not os.path.exists(str(i) + str(j)):
                os.mkfifo(str(i) + str(j))
              
            if not os.path.exists(str(j) + str(i)):
                os.mkfifo(str(j) + str(i))
        processes[i].start()

    for i in range(n_processes):
        processes[i].join()
