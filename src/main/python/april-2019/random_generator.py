# coding=utf-8
import random

seeds =  ['39722659', '33474231', '40742307', '12451825', '38329720', '35632880', '3588474', '733434', '25427375', '13174550']

if __name__ == '__main__':
    random.seed(23)
    values = []
    for i in range(0, 10):
        values.append(str(random.getrandbits(32))[0:-2])
    print 'seeds = ', values