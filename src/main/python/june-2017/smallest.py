import json
import sys
from pprint import pprint

keyAssertion = "#AssertionAdded"
keyInputs = "#InputAdded"
keyMutant = "#MutantKilled"

def usage():
    print("usage : python best_ration.py (-m <mode>) <pathToOutDspot.json>")

def smallest(filename):

    currentAdd = 1000
    smallestAmplifiedMethodName="none"

    with open(filename) as data_file:
        data = json.load(data_file)
    for clazz in data:
        for amplifiedMethod in data[clazz]:
            if data[clazz][amplifiedMethod][keyAssertion] + data[clazz][amplifiedMethod][keyInputs] < currentAdd:
                currentAdd = data[clazz][amplifiedMethod][keyAssertion] + data[clazz][amplifiedMethod][keyInputs]
                smallestAmplifiedMethodName = amplifiedMethod

        if smallestAmplifiedMethodName != "none":
            print clazz + "$" + smallestAmplifiedMethodName
            pprint(data[clazz][smallestAmplifiedMethodName])

if len(sys.argv) < 2:
    usage()
    exit(1)

for i in range(1, len(sys.argv)):
    smallest(sys.argv[i])

