import json
import sys

keyAssertion = "nbAssertionAdded"
keyInputs = "nbInputAdded"
keyMutant = "nbMutantKilled"


def fitness(NbMutantKilled, statementInserted):
    if statementInserted == 0:
        return 0
    else:
        return float(float(NbMutantKilled) / float(statementInserted))


def usage():
    print("usage : python best_ration.py (-m <mode>) <pathToOutDspot.json>")


def best_fitness(filename):
    maxFitness = 0.0
    bestAmplifiedMethodName = "none"

    with open(filename) as data_file:
        data = json.load(data_file)
    if "testCases" in data:
        for amplifiedMethod in data["testCases"]:
            currentFitness = fitness(amplifiedMethod["nbMutantKilled"],
                amplifiedMethod["nbAssertionAdded"] + amplifiedMethod["nbInputAdded"])
            if currentFitness > maxFitness:
                maxFitness = currentFitness
                bestAmplifiedMethodName = amplifiedMethod

        if bestAmplifiedMethodName != "none":
            print (bestAmplifiedMethodName["name"] + " kills " + str(bestAmplifiedMethodName["nbMutantKilled"]) +
                " by adding " + str(bestAmplifiedMethodName[keyAssertion] + bestAmplifiedMethodName[keyInputs]) +
                "(" + str(maxFitness) + ")")


if len(sys.argv) < 2:
    usage()
    exit(1)

for i in range(1, len(sys.argv)):
    best_fitness(sys.argv[i])
