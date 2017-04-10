import csv
import operator
import sys

def nearestFromAvg(a, b):
    global avg
    return abs(avg - a) - abs(avg - b)

def getKeyByValue(dict, searchValue, alreadySelected):
    for key, value in dict.iteritems():
        if value == searchValue and not key == alreadySelected:
            return key

def buildCmd(project, testClass, suffix):
    print testClass
    cmd = "/usr/bin/time -o ${HOME}/time/" + project + " -v" \
                                                       " ${HOME}/jdk1.8.0_121/jre/bin/java -Xms16G -Xmx32G" \
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

def select(project):
    global avg
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
        print min1[0], min2[0]
        print avg1[0], avg2[0]

    return buildCmd(project, max1[0], "max1"), buildCmd(project, max2[0], "max2"), buildCmd(project, min1[0], "min1"), \
           buildCmd(project, min2[0], "min2"), buildCmd(project, avg1[0], "avg1"), buildCmd(project, avg2[0], "avg2")

avg = 0
select(sys.argv[1])
