# coding=utf-8
import random

if __name__ == '__main__':
    random.seed(23)
    values = []
    for i in range(0, 10):
        values.append(str(random.getrandbits(32))[0:-2])
    print 'seeds = ', values