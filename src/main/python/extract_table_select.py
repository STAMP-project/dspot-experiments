import json
import os
import sys
import random

root_folder = "results_select"
projects = ["javapoet", "logback", "protostuff", "stream-lib", "traccar", "twilio-java" ,"mustache.java", "mybatis"]
types_class = ["max1", "max2", "avg1", "avg2", "min1", "min2"]
blacklist = ["compiler.json", "logback-core.json"]

# project class nbTest nbMutant nbTestGen nbMutantGen nbIAmpl nbAAmpl time

for project in projects:
    path = root_folder + "/" + project
    for type_class in types_class:
        line = project + "&"
        folder = path + "_" + type_class
        files = filter(lambda x: x.endswith(".json") and
                                 not x == project + ".json" and
                                 not x in blacklist, os.listdir(folder))
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
                for testCase in data["testCases"]:
                    nbMutantGen += testCase["nbMutantKilled"]
                    nbIAmpl += testCase["nbInputAdded"]
                    nbAAmpl += testCase["nbAssertionAdded"]
                line += name + "&" \
                        + str(nbTest) \
                        + "&" + str(nbMutant) \
                        + "&" + str(nbTestGen) \
                        + "&" + str(nbMutant+nbMutantGen) \
                        + "(" + ("+" if nbMutantGen > 0 else "") + str(nbMutantGen) + ")" \
                        + "&" + str(nbIAmpl) \
                        + "&" + str(nbAAmpl) + "&"

            timer = filter(lambda x: x.endswith(".json") and
                             not x == file, os.listdir(folder))[0]
            with open(folder + "/" + timer) as data_file:
                data = json.load(data_file)
                time = data["classTimes"][0]["timeInMs"]
                line += str(time) + "\\\\"

        else:
            line += "-&" * 7
            line += "-\\\\"

        print line




