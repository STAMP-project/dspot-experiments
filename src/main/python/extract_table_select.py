import json
import os
import sys
import random
import count_original_mutant_per_class

root_folder = "results"
projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]

projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback"]

# projects = ["javapoet",]
types_class = ["max1", "max2", "min1", "min2", "avg1", "avg2"]
types_class = ["max", "min", "avg"]

prefixDspot = ""  # ""/tmp/dspot-experiments/"
prefixDataset = prefixDspot + "dataset"

with open(prefixDataset + "/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)

# project class nbTest nbMutant nbTestGen nbMutantGen nbIAmpl nbAAmpl time

gray = False

for type_class in types_class:
    for project in projects:
        for index in range(1, 3):
            path = root_folder + "/" + project
            line = ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else "") + project + "&"
            gray = not gray
            folder = path + "_" + type_class + str(index)
            files = filter(lambda x: x.endswith(".json") and
                                     not x == project + ".json" and
                                     not x == (properties_rates[project]["subModule"] + ".json"), os.listdir(folder))
            if len(files) > 0:
                file = files[0]
                with open(folder + "/" + file) as data_file:
                    data = json.load(data_file)
                    name = data["name"].split(".")[-1]
                    nbTest = data["nbOriginalTestCases"]
                    nbMutant = data["nbMutantKilledOriginally"]
                    nbTestGen = len(data["testCases"])
                    nbMutantGen = 0
                    nbIAmpl = 0
                    nbAAmpl = 0
                    killedMutant = []
                    for testCase in data["testCases"]:
                        mutants = testCase["mutantsKilled"]
                        nbUniqueMutantKilled = 0
                        for mutant in mutants:
                            mutantAsStr = str(mutant["ID"]) + ":" + str(mutant["lineNumber"]) + ":" + str(
                                mutant["locationMethod"])
                            if not mutantAsStr in killedMutant:
                                nbUniqueMutantKilled += 1
                                killedMutant.append(mutantAsStr)
                        nbMutantGen += nbUniqueMutantKilled
                        nbIAmpl += testCase["nbInputAdded"]
                        nbAAmpl += testCase["nbAssertionAdded"]

                    if not name == "Basic":
                        total, killed = count_original_mutant_per_class.getTotalKilled(project, name)
                    else:
                        total, killed = 1, 0
                    line += name \
                            + "&" + str(nbTest) \
                            + "&" + str(nbTestGen) \
                            + "&" + str(total) \
                            + "&" + str("{0:.2f}".format(float(killed) / float(total) * 100)) \
                            + "&" + ("{\color{ForestGreen}$\\nearrow$}" if nbMutantGen > 0 else "$\\rightarrow$") \
                            + "&" + str(nbMutant + nbMutantGen) \
                            + "&" + ("+" if nbMutantGen > 0 else "") + str(nbMutantGen) \
                            + "&" + ("+" + "{0:.2f}".format(float(float(nbMutantGen) / float(nbMutant)) * 100)
                                     if nbMutantGen > 0 else "0") + "\%" \
                            + "&" + str(nbIAmpl) \
                            + "&" + str(nbAAmpl) + "&"

                timer = filter(lambda x: x.endswith(".json") and
                                         not x == file, os.listdir(folder))[0]
                with open(folder + "/" + timer) as data_file:
                    data = json.load(data_file)
                    time = data["classTimes"][0]["timeInMs"]
                    line += "{0:.2f}".format(float(float(time) / 1000.0 / 60.0)) + "\\\\"
                    # line += str(time) + "\\\\"

            else:
                line += "-&" * 7
                line += "-\\\\"

            print line
    print "\\hline\\hline"
