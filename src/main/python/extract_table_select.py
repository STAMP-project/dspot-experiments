import json
import os
import sys
import random

root_folder = "results_select"
projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback"]
types_class = ["max1", "max2", "avg1", "avg2", "min1", "min2"]

prefixDspot = "/tmp/dspot-experiments/"
prefixDataset = prefixDspot + "dataset"

with open(prefixDataset + "/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)

# project class nbTest nbMutant nbTestGen nbMutantGen nbIAmpl nbAAmpl time

for project in projects:
    path = root_folder + "/" + project
    for type_class in types_class:
        line = project + "&"
        folder = path + "_" + type_class
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
                    mutants=testCase["mutantsKilled"]
                    nbUniqueMutantKilled = 0
                    for mutant in mutants:
                        mutantAsStr = str(mutant["ID"]) + ":" + str(mutant["lineNumber"]) + ":" + str(mutant["locationMethod"])
                        if not mutantAsStr in killedMutant:
                            nbUniqueMutantKilled += 1
                            killedMutant.append(mutantAsStr)
                    nbMutantGen += nbUniqueMutantKilled
                    nbIAmpl += testCase["nbInputAdded"]
                    nbAAmpl += testCase["nbAssertionAdded"]
                line += name + "&" \
                        + str(nbTest) \
                        + "&" + str(nbMutant)  \
                        + "&" + str(nbTestGen) \
                        + "&" + str(nbMutant + nbMutantGen) \
                        + "(" + ("+" if nbMutantGen > 0 else "") + str(nbMutantGen) + ")" \
                        + "&" + str(nbIAmpl) \
                        + "&" + str(nbAAmpl) + "&"

            timer = filter(lambda x: x.endswith(".json") and
                                     not x == file, os.listdir(folder))[0]
            with open(folder + "/" + timer) as data_file:
                data = json.load(data_file)
                time = data["classTimes"][0]["timeInMs"]
                #line += "{.2f}".format(float(float(time)/ 1000.0 / 60.0)) + "\\\\"
                line += str(time) + "\\\\"

        else:
            line += "-&" * 7
            line += "-\\\\"

        print line
