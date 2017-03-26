import json
import os
import sys
import random

random.seed(23)

'''
    used keys by json files
'''

name = "name"
originalMutantScore="nbMutantKilledOriginally"
originalMutantScore="nbOriginalTestCases"#TODO
nbOriginalTestCases="nbOriginalTestCases"
testCases = "testCases"
assertion = "nbAssertionAdded"
input = "nbInputAdded"
mutant = "nbMutantKilled"
mutants = "mutantsKilled"
id = "ID"
ln = "lineNumber"
method = "locationMethod"

prefix = "results/"
projects = [ "javapoet",  ]

'''
    Compare functions
'''

def gt(a, b):
    return a > b

def lt(a, b):
    return a < b

def eq(a, b):
    return a == b

def near(a, b):
    if a == None:
        return -1
    if b == None:
        return 1
    else:
        return lt (abs(avgNbMutantKilledOriginally - a), abs(avgNbMutantKilledOriginally - b))

'''
    Sort Functions
'''

def sortByMutantScoreAndMutation(test1, test2):
    if test1[0] == None:
        return -1
    if test2[0] == None:
        return 1
    if test1[1] == test2[1]:
        if bool(random.getrandbits(1)):
            return -1
        else:
            return 1
    else:
        return test1[1] - test2[1]

def sortByDistanceFromAVG(test1, test2):
    if test1[0] == None:
        return -1
    if test2[0] == None:
        return 1
    d1 = abs(avgNbMutantKilledOriginally - test1[1])
    d2 = abs(avgNbMutantKilledOriginally - test2[1])
    return - (d1 - d2)

def reverseSortByMutantScoreAndMutation(test1, test2):
    return - sortByMutantScoreAndMutation(test1, test2)


'''
    Main Functions
'''

def compare(compareFunc, array, testClass):
    if compareFunc(testClass[originalMutantScore], array[1]):
        return [testClass[name], testClass[originalMutantScore]], True
    else:
        return array, False

def compareArray(compareFunc, array, testClass):
    for i in range(2):
        array[i], change = compare(compareFunc, array[i], testClass)
        if change:
            break
    return array


def iterateOnArray(testClass):
    print testClass[name], testClass[originalMutantScore]



for project in projects:

    '''
        Retrieve per project: 2 classes that are top killers, 2 classes that does not kill mutant,
        and 2 classes that have a mutation score near to the avg score mutant
    '''

    bestTestClasses = [ [None for y in range(2) ] for x in range(2) ]
    avgTestClasses = [ [None for y in range(2) ] for x in range(2) ]
    worstTestClasses = [ [None, sys.maxint] for x in range(2) ]

    avgNbMutantKilledOriginally = 0
    cpt = 0

    files = filter(lambda x: x.endswith(".json") and not x == project+".json", os.listdir(prefix + project))
    for file in files:
        with open(prefix + project + "/" + file) as data_file:
            data = json.load(data_file)
            avgNbMutantKilledOriginally += data[originalMutantScore]
            cpt += 1


    avgNbMutantKilledOriginally = avgNbMutantKilledOriginally / cpt

    for file in files:
        with open(prefix + project + "/" + file) as data_file:
            testClass = json.load(data_file)
            bestTestClasses = sorted(compareArray(gt, bestTestClasses, testClass), cmp=sortByMutantScoreAndMutation)
            avgTestClasses = sorted(compareArray(near, avgTestClasses, testClass), cmp=sortByDistanceFromAVG)
            badlyAmplifiedTestClasses = sorted(compareArray(lt, worstTestClasses, testClass), cmp=reverseSortByMutantScoreAndMutation)

    print avgNbMutantKilledOriginally
    print bestTestClasses
    print avgTestClasses
    print worstTestClasses


