import json
import sys

keyAssertion = "#AssertionAdded"
keyInputs = "#InputAdded"
keyMutant = "#MutantKilled"

def fitness(NbMutantKilled, statementInserted):
    return float( float(NbMutantKilled) / float(statementInserted))

def usage():
    print("usage : python best_ration.py (-m <mode>) <pathToOutDspot.json>")

def best_fitness(filename):

    maxFitness = 0.0
    bestAmplifiedMethodName="none"

    with open(filename) as data_file:
        data = json.load(data_file)
    for clazz in data:
        for amplifiedMethod in data[clazz]:
            currentFitness = fitness(data[clazz][amplifiedMethod][keyMutant],
                                     data[clazz][amplifiedMethod][keyAssertion] + data[clazz][amplifiedMethod][keyInputs])
            if currentFitness > maxFitness:
                maxFitness = currentFitness
                bestAmplifiedMethodName = amplifiedMethod

        if bestAmplifiedMethodName != "none":
            print clazz
            print (bestAmplifiedMethodName + " kill " + str(data[clazz][bestAmplifiedMethodName][keyMutant]) + " by adding " +
              (str(data[clazz][bestAmplifiedMethodName][keyInputs] + data[clazz][bestAmplifiedMethodName][keyAssertion])) +
              "("+ str(maxFitness) +")")

if len(sys.argv) < 2:
    usage()
    exit(1)

for i in range(1, len(sys.argv)):
    best_fitness(sys.argv[i])

