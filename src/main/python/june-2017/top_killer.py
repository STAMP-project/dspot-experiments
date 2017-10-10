import json
import sys

keyMutant = "NbMutantKilled"

def usage():
    print("usage : python top_killer.py (-m <mode>) <pathToOutDspot.json>")

def top_killer(filename):

    topScore = 0
    bestAmplifiedMethodName="none"

    with open(filename) as data_file:
        data = json.load(data_file)
    if "testCases" in data:
        for amplifiedMethod in data["testCases"]:
            currentScore = amplifiedMethod["nbMutantKilled"]
            if currentScore > topScore:
                topScore = currentScore
                bestAmplifiedMethodName = amplifiedMethod

        if bestAmplifiedMethodName != "none":
            print(bestAmplifiedMethodName["name"] + " kills " + str(topScore) + " mutants")

if len(sys.argv) < 2:
    usage()
    exit(1)

for i in range(1, len(sys.argv)):
    top_killer(sys.argv[i])

