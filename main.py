import multiprocessing
import sys, os
import numpy as np
import time
import random
from subprocess import Popen

MAP_SIZE = 4
NUM_SHIPS = 6


class Battleships:

    def __init__(self, mapSize, numShips, fn, queue):
        sys.stdin = os.fdopen(fn)
        self.mapSize = mapSize
        self.numShips = numShips
        self.map = np.empty((mapSize, mapSize), dtype="bool")
        self.queue = queue

    def game_over(self, message):
        self.print_message(message + "Poraz")
        self.put_to_queue(message + "Pobjeda")

    def my_turn(self):
        turn = self.queue.get()
        self.queue.put(turn)

        if turn == multiprocessing.current_process().name:
            return False

        return True

    def attacked(self):

        if not self.my_turn():
            self.queue.get()
            position = self.read_from_queue()[0]

            mes = 'Hit'

            if self.map[position[0], position[1]]:
                self.map[position[0], position[1]] = False

                if self.is_finished():
                    self.game_over()
                    return True

                self.put_to_queue('Hit')
            else:
                mes = 'Miss'
                self.put_to_queue('Miss')

            self.print_message("Shot at position: " + str(position) + "\n" + mes)
            self.queue.put(multiprocessing.current_process().name)

    def print_shoot_message(self):
        if self.not_my_turn():
            print self.read_from_queue()

    def attack(self):

        if self.my_turn():
            position = (random.randint(0, MAP_SIZE - 1), random.randint(0, MAP_SIZE - 1))
            self.put_to_queue(position)
            self.print_message("Shoot at: " + str(position) + "\n")

        return False

    @staticmethod
    def print_message(message):
        print multiprocessing.current_process().name
        print message
        print

    def ready(self):
        self.put_to_queue("spreman")

    def put_to_queue(self, message):
        self.queue.put((message, multiprocessing.current_process().name))

    def read_from_queue(self):
        while True:
            time.sleep(1)

            if self.queue.empty():
                continue

            message = self.queue.get(False)

            if message[1] == multiprocessing.current_process().name:
                self.queue.put(message)
            else:
                return message

    def is_finished(self):
        return not np.any(self.map)

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


def battleships(lock, queue, fn):

    attacker = queue.get()

    lock.acquire()
    player = Battleships(MAP_SIZE, NUM_SHIPS, fn, queue)
    print multiprocessing.current_process().name
    print
    player.init_map()
    lock.release()

    player.ready()
    player.read_from_queue()

    while not queue.empty():
        time.sleep(1)
        continue

    queue.put(attacker)

    while True:
        time.sleep(1)
        player.attack()
        player.attacked()







if __name__ == '__main__':
    lock = multiprocessing.Lock()
    queue = multiprocessing.Queue()
    fn = sys.stdin.fileno()
    p1 = multiprocessing.Process(target=battleships, args=(lock, queue,fn))
    p1.start()

    p2 = multiprocessing.Process(target=battleships, args=(lock, queue,fn))
    p2.start()

    queue.put(p1.name)

    queue.close()
    queue.join_thread()
    p1.join()
    p2.join()
