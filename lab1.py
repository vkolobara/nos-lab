import multiprocessing
import sys, os
import numpy as np
import time
import random
from subprocess import Popen

MAP_SIZE = 4
NUM_SHIPS = 6


class Battleships:

    def __init__(self, mapSize, numShips, fn, queue, lock):
        self.lock = lock
        sys.stdin = os.fdopen(fn)
        self.mapSize = mapSize
        self.numShips = numShips
        self.map = np.empty((mapSize, mapSize), dtype="bool")
        self.queue = queue
        self.pos = (0, 0)

    def game_over(self):
        print "PORAZ"
        self.put_to_queue("POBJEDA")

    def attack(self):
        position = self.pos
        pos = self.pos[0] * MAP_SIZE + self.pos[1] + 1
        self.pos = (pos / 4, pos % 4)
        self.put_to_queue(("ATTACK", position))
        time.sleep(1)
        resp = self.read_from_queue()
        while not isinstance(resp, tuple):
            self.queue.put(resp)
            resp = self.read_from_queue()
        self.print_message("SHOOT AT POSITION: " + str(position) + "\n" + resp[0])
        if resp[0] == "POBJEDA":
            self.map = np.empty((self.mapSize, self.mapSize), dtype="bool")
            return
        time.sleep(1)

    def attacked(self):
        message = self.read_from_queue()

        while message[0][0] != "ATTACK":
            self.queue.put(message)
            message = self.read_from_queue()

        position = message[0][1]
        self.print_message("SHOT AT POSITION: " + str(position))
        msg = "MISS"
        if self.map[position[0], position[1]]:
            self.map[position[0], position[1]] = False
            if self.is_finished():
                self.game_over()
                return
            else:
                msg = "HIT"

        print msg
        print
        self.put_to_queue(msg)
        time.sleep(1)

    @staticmethod
    def print_message(message):
        print multiprocessing.current_process().name
        print message

    def ready(self):
        self.put_to_queue("spreman")
        self.lock.acquire()
        msg = self.read_from_queue()
        self.lock.release()
       
    def put_to_queue(self, message):
        self.queue.put((message, multiprocessing.current_process().name))
        time.sleep(1)

    def read_from_queue(self):
        while True:
            time.sleep(1)
            message = self.queue.get()

            if message[1] != multiprocessing.current_process().name:
                return message
            else:
                self.queue.put(message)

    def is_finished(self):
        return not np.any(self.map)

    def init_map_auto(self):
        x = [True] * NUM_SHIPS + [False] * (MAP_SIZE * MAP_SIZE - NUM_SHIPS)
        np.random.shuffle(x)
        self.map = np.mat(x).reshape(MAP_SIZE, MAP_SIZE)
        print self.map

    def init_map(self):
        ship_map = np.empty((self.mapSize, self.mapSize), dtype="bool")
        count_ships = 0
        print 'Input your ship positions'

        for i in range(self.mapSize):
            line = sys.stdin.readline()
            positions = line.rstrip().split(" ")

            if len(positions) != self.mapSize:
                print 'Each line must contain ' + str(self.mapSize) + ' entries!'
                i -= 1
                continue

            for j in range(self.mapSize):
                if positions[j] == 'o':
                    ship_map[i, j] = True
                    count_ships += 1
                else:
                    ship_map[i, j] = False

        if count_ships != self.numShips:
            print 'There must be exactly ' + str(self.numShips) + ' ships!'
            self.init_map()
        else:
            self.map = ship_map



def battleship1(lock, queue, fn):

    lock.acquire()
    player = Battleships(MAP_SIZE, NUM_SHIPS, fn, queue, lock)
    print multiprocessing.current_process().name
    player.init_map_auto()
    print

    lock.release()

    player.ready()

    while True:
        player.attack()
        if player.is_finished():
            break
        player.attacked()
        if player.is_finished():
            break

def battleship2(lock, queue, fn):

    lock.acquire()
    player = Battleships(MAP_SIZE, NUM_SHIPS, fn, queue, lock)
    print multiprocessing.current_process().name
    player.init_map_auto()
    print

    lock.release()

    player.ready()

    while True:
        player.attacked()
        if player.is_finished():
            break
        player.attack()
        if player.is_finished():
            break






if __name__ == '__main__':
    lock = multiprocessing.Lock()
    queue = multiprocessing.Queue()
    fn = sys.stdin.fileno()
    attacker = "Process-1"
    p1 = multiprocessing.Process(target=battleship1, args=(lock, queue,fn))
    p1.start()

    p2 = multiprocessing.Process(target=battleship2, args=(lock, queue,fn))
    p2.start()

    queue.close()
    queue.join_thread()
    p1.join()
    p2.join()
