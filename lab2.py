import os
import multiprocessing
from multiprocessing import Process

n_processes = 3

class MyProcess:
    
    def __init__(self, read_pipes, write_pipes):
        
        self.name = multiprocessing.current_process().name
                
        self.clock = 0
        
        self.read_pipes = read_pipes
        self.write_pipes = write_pipes
        
        self.currRequest = None
                
            
    def closePipes(self):
        for pipe in self.read_pipes:
            pipe.close()
        for pipe in self.write_pipes:
            pipe.close()
    
    def enterCriticalSection(self):
        self.waitForResponses()
        self.doCriticalSection()
        self.exitCriticalSection()
        
    def doCriticalSection(self):
        print (self.name)
        
        for i in range(5):
            print (i)
            
        print()
                
    def requestEntryCriticalSection(self):
        for pipe in self.write_pipes:
            pipe.write(self.name + " " + str(self.clock) + "\n")
            
    def respondRequestEntry(self, message):
        pass
        
    def waitForResponses(self):
        for pipe in self.read_pipes:
            message = pipe.readline()
            self.clock = max(int(message.split(" ")[1]), self.clock)
    
    def exitCriticalSection(self):
        for pipe in self.write_pipes:
            pipe.write(self.name + " " + str(self.clock) + "\n")
            
    
    

def process(index, totalNum):
    
    read_pipes = []
    write_pipes = []

    for i in range(totalNum):
        if i != index:
            try:
                os.mkfifo(str(i) + str(index) + ".txt")
            except OSError:
                pass  
            finally:
                fifo = os.open(str(i) + str(index) + ".txt", os.O_RDONLY)
                read_pipes.append(fifo)
                
            try:
                os.mkfifo(str(index) + str(i) + ".txt")
            except OSError:
                pass  
            finally:
                fifo = open(str(i) + str(index) + ".txt", 'w')
                write_pipes.append(fifo)

            
                    
    proc = MyProcess(read_pipes, write_pipes)

    proc.doCriticalSection()

    proc.closePipes()

if __name__ == "__main__":
    processes = []
    
    for i in range(n_processes):
        processes.append(Process(target=process, args=(i, n_processes)))
        
    for proc in processes:
        proc.start()

    for proc in processes:
        proc.join()
        
        