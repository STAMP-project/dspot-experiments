import json
import os
import sys
import random

random.seed(23)

'''
    used keys by json files
'''

name = "name"
testCases = "testCases"
assertion = "nbAssertionAdded"
input = "nbInputAdded"
mutant = "nbMutantKilled"
mutants = "mutantsKilled"
id = "ID"
ln = "lineNumber"
method = "locationMethod"

prefix = "results/"
projects = ["protostuff", ]

'''
    Compare functions
'''

def gt(a, b):
    return a > b


def lt(a, b):
    return a < b


def eq(a, b):
    return a == b


def compare_(compareFunc, array, testCase):
    if compareFunc(testCase[mutant], array[1]) or (
                    testCase[mutant] == array[1] and compareFunc(array[2],
                                                                 testCase[input] + testCase[assertion])):
        return [data[name], testCase[mutant], testCase[input] + testCase[assertion]], True
    else:
        return array, False


def checkOnArray(compareFunc, array, testCase):
    for i in range(2):
        array[i], change = compare_(compareFunc, array[i], testCase)
        if change:
            return array, change
    return array, change


def checkZeroTestGenerated():
    change = False
    for i in range(2):
        if badlyAmplifiedTestClasses[i][1] > 0:
            badlyAmplifiedTestClasses[i] = [data[name], 0, 0]
            change = True
            break
    if not change:
        for i in range(2):
            if badlyAmplifiedTestClasses[i][1] == 0 and bool(random.getrandbits(1)):
                badlyAmplifiedTestClasses[i] = [data[name], 0, 0]
                break
    return badlyAmplifiedTestClasses


def getNumbers(testCases):
    mutantKilled = []
    nbMutationTest = 0
    nbAssertion = 0
    for testCase in testCases:
        for mutant in testCase[mutants]:
            mutant_id = str(mutant[id]) + ":" + str(mutant[ln]) + ":" + str(mutant[method])
            if mutant_id not in mutantKilled:
                mutantKilled.append(mutant_id)
        nbMutationTest += testCase[input]
        nbAssertion += testCase[assertion]
    return len(mutantKilled), nbMutationTest, nbAssertion

'''
    Final output function: print the content of a latex table
'''

def toLatex():
    latexStrWell = ""
    latexStrBad = ""
    for file in files:
        for i in range(2):
            if wellAmplifiedTestClasses[i][0] in file:
                path = prefix + project + "/" + file
                with open(path) as data_file:
                    data = json.load(data_file)
                    nbTestCaseGenerated = len(data[testCases])
                    nbUniqueMutantKilled, nbMutationTest, nbAssertion = getNumbers(data[testCases])
                    latexStrWell += project + "&" + str(data[
                                                  name]) + "&" + str(0) + "&" + str(nbTestCaseGenerated) + "&" + str(
                        nbUniqueMutantKilled) + "&" + str(nbMutationTest) + "&" + str(nbAssertion) + "\\\\\n"
            elif badlyAmplifiedTestClasses[i][0] in file:
                path = prefix + project + "/" + file
                with open(path) as data_file:
                    data = json.load(data_file)
                    nbTestCaseGenerated = len(data[testCases])
                    nbUniqueMutantKilled, nbMutationTest, nbAssertion = getNumbers(data[testCases])
                    latexStrBad += project + "&" + str(data[
                                                  name]) + "&" + str(0) + "&" + str(nbTestCaseGenerated) + "&" + str(
                        nbUniqueMutantKilled) + "&" + str(nbMutationTest) + "&" + str(nbAssertion) + "\\\\\n"
    print(latexStrWell + latexStrBad)

'''
    Sort functions
'''

def sortByMutantScoreAndMutation(test1, test2):
    if test1[0] == None:
        return -1
    if test2[0] == None:
        return 1
    if test1[1] == test2[1]:
        return test1[2] - test2[2]
    else:
        return test1[1] - test2[1]
def reverseSortByMutantScoreAndMutation(test1, test2):
    return - sortByMutantScoreAndMutation(test1, test2)


'''
    Main Function
'''

for project in projects:

    '''
        Retrieve per project: two classes that is well amplified, and two classes that we could not amplify
            criterion: - number of new mutants killed by the generated test cases.
    '''

    wellAmplifiedTestClasses = [[None for y in range(3)] for x in range(2)]
    badlyAmplifiedTestClasses = [[None, sys.maxint, 0], [None, sys.maxint, 0]]

    files = filter(lambda x: x.endswith(".json"), os.listdir(prefix + project))
    for file in files:
        path = prefix + project + "/" + file
        with open(path) as data_file:
            data = json.load(data_file)
            print data[name]
            if data[testCases]:
                for testCase in data[testCases]:
                    wellAmplifiedTestClasses, change = checkOnArray(gt, wellAmplifiedTestClasses, testCase)
                    if not change:
                        badlyAmplifiedTestClasses, change = checkOnArray(lt, badlyAmplifiedTestClasses, testCase)
                    if change:
                        print "break"
                        break
            else:
                checkZeroTestGenerated()

        wellAmplifiedTestClasses=sorted(wellAmplifiedTestClasses, cmp=sortByMutantScoreAndMutation)
        badlyAmplifiedTestClasses=sorted(badlyAmplifiedTestClasses, cmp=reverseSortByMutantScoreAndMutation)

    toLatex()
