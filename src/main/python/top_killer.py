import json
import sys

keyMutant = "#MutantKilled"

def usage():
    print("usage : python top_killer.py (-m <mode>) <pathToOutDspot.json>")

def top_killer(filename):

    topScore = 0
    bestAmplifiedMethodName="none"

    with open(filename) as data_file:
        data = json.load(data_file)
    for clazz in data:
        for amplifiedMethod in data[clazz]:
            currentScore = data[clazz][amplifiedMethod][keyMutant]
            if currentScore > topScore:
                topScore = currentScore
                bestAmplifiedMethodName = amplifiedMethod
        print(bestAmplifiedMethodName + " kills " + str(topScore) + " mutants")

if len(sys.argv) < 2:
    usage()
    exit(1)
top_killer(sys.argv[1])

