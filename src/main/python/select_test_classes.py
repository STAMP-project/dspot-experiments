import csv
import operator
import sys
import subprocess


def nearestFromAvg(a, b):
    return abs(avg - a) - abs(avg - b)

def getKeyByValue(dict, searchValue, alreadySelected):
    for key, value in dict.iteritems():
        if value == searchValue and not key == alreadySelected:
            return key


def buildCmd(testClass, suffix):
    print testClass
    cmd = "/usr/bin/time -o ${HOME}/time/" + project + " -v" \
                                                       " ${HOME}/jdk1.8.0_121/jre/bin/java -Xms14G -Xmx16G" \
                                                       " -jar target/dspot-experiment-1.0.0-jar-with-dependencies.jar" \
                                                       " --path-to-propeties src/main/resources/" + project + ".properties" \
                                                                                                              " --amplifiers MethodAdd:TestDataMutator:StatementAdderOnAssert" \
                                                                                                              " --iteration 3" \
                                                                                                              " --output-path " + project + "_" + suffix + \
          " --maven-home /home/spirals/danglot/apache-maven-3.3.9/" \
          " -t " + testClass + \
          " -m ${HOME}/" + project + "_mutant/mutations.csv"
    print cmd
    return cmd


def copyResult(suffix):
    return "cp -R " + project + "_" + suffix + "  ${HOME}/results/" + project + "_" + suffix


project = sys.argv[1]
path = "/home/spirals/danglot/" + project + "_mutant/mutations.csv"
print path

with open(path, 'rb') as csvfile:
    mutations_csv = csv.reader(csvfile, delimiter=',', quotechar='|')
    scorePerClass = {}
    nbTotalKilled = 0
    for row in mutations_csv:
        if row[-2] == 'KILLED':
            killer = row[-1]
            if "(" in killer:  # normal case
                killer = killer[killer.find("(") + 1:killer.find(")")]
            elif killer[:len(killer) / 2] == killer[1 + len(killer) / 2:]:  # full qualified name is repeated
                print killer
                killer = killer[:len(killer) / 2]
            else:  # only the full qualified is printed
                print killer

            if scorePerClass.has_key(killer):
                scorePerClass[killer] = scorePerClass[killer] + 1
            else:
                scorePerClass[killer] = 1
            nbTotalKilled = nbTotalKilled + 1

    avg = nbTotalKilled / len(scorePerClass)
    max1 = max(scorePerClass.iteritems(), key=operator.itemgetter(1))
    del scorePerClass[max1[0]]
    max2 = max(scorePerClass.iteritems(), key=operator.itemgetter(1))
    min1 = min(scorePerClass.iteritems(), key=operator.itemgetter(1))
    del scorePerClass[min1[0]]
    min2 = min(scorePerClass.iteritems(), key=operator.itemgetter(1))
    tmp = sorted(scorePerClass.values(), cmp=nearestFromAvg)
    avg1 = getKeyByValue(scorePerClass, tmp[0], None), tmp[0]
    avg2 = getKeyByValue(scorePerClass, tmp[1], avg1[0]), tmp[1]
    print max1[0], max2[0]
    print avg1[0], avg2[0]
    print min1[0], min2[0]

    subprocess.call(buildCmd(max1[0], "max1"), shell=True)
    subprocess.call(copyResult("max1"), shell=True)
    subprocess.call(buildCmd(max2[0], "max2"), shell=True)
    subprocess.call(copyResult("max2"), shell=True)
    subprocess.call(buildCmd(min1[0], "min1"), shell=True)
    subprocess.call(copyResult("min1"), shell=True)
    subprocess.call(buildCmd(min2[0], "min2"), shell=True)
    subprocess.call(copyResult("min2"), shell=True)
    subprocess.call(buildCmd(avg1[0], "avg1"), shell=True)
    subprocess.call(copyResult("avg1"), shell=True)
    subprocess.call(buildCmd(avg2[0], "avg2"), shell=True)
    subprocess.call(copyResult("avg2"), shell=True)
