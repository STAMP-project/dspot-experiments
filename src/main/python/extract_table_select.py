import json
import os
import sys
import random
import count_mutant

root_folder = "results"
projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]

projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback"]

types_class = ["max", "min", "avg"]

prefixDspot = ""  # ""/tmp/dspot-experiments/"
prefixDataset = prefixDspot + "dataset"

with open(prefixDataset + "/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)

# project class nbTest nbMutant nbTestGen nbMutantGen nbIAmpl nbAAmpl time

lines = []

for type_class in types_class:
    for project in projects:
        for index in range(1, 3):
            path = root_folder + "/" + project
            line = "\scriptsize{"+ project + "}" + "&"
            folder = path + "_" + type_class + str(index)
            files = filter(lambda x: x.endswith(".json") and
                                     not x == project + ".json" and
                                     not x == (properties_rates[project]["subModule"] + ".json"), os.listdir(folder))
            if len(files) > 0:
                file = files[0]
                with open(folder + "/" + file) as data_file:
                    data = json.load(data_file)
                    name = data["name"].split(".")[-1]
                    fullqualifiedname = data["name"]
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

                    total, killed = count_mutant.countForTestClass(
                        "original/per_class/" + project + "/" + fullqualifiedname + "_mutations.csv")

                    if total <= 1000:
                        full_qualified_name_ampl = ".".join(data["name"].split(".")[:-1]) + "." + \
                                                   (name + "Ampl" if name.startswith("Test") else "Ampl" + name)
                        totalAmpl, killedAmpl = count_mutant.countForTestClass(
                            "results/per_class/" + project + "/" + full_qualified_name_ampl + "_mutations.csv"
                        )

                        deltaAmplTotal = totalAmpl - total
                        deltaAmplKilled = killedAmpl - killed

                        line += "\scriptsize{"+ name + "}" \
                                + "&" + str(nbTest) \
                                + "&" + str(nbTestGen) \
                                + "&" + str(total) \
                                + "&" + str(killed) \
                                + "&" + str("{0:.2f}".format(float(killed) / float(total) * 100)) \
                                + "&" + (
                                    "{\color{ForestGreen}$\\nearrow$}" if deltaAmplTotal > 0 else "$\\rightarrow$") \
                                + "&" + str(totalAmpl) \
                                + "&" + (
                                    "{\color{ForestGreen}$\\nearrow$}" if deltaAmplKilled > 0 else "$\\rightarrow$") \
                                + "&" + str(killedAmpl) \
                                + "&" + str("{0:.2f}".format(float(killedAmpl) / float(totalAmpl) * 100)) \
                                + "&" + ("+" if deltaAmplKilled > 0 else "") + str(deltaAmplKilled) \
                                + "&" + "+" + "{0:.2f}".format(
                            float(float(deltaAmplKilled) / float(total)) * 100) + "\%" \
                                + "&" + str(nbIAmpl) \
                                + "&" + str(nbAAmpl) + "&"
                        '''

                        line += "\small{"+ name + "}" \
                                + "&" + str(nbTest) \
                                + "&" + str(nbTestGen) \
                                + "&" + str(total) \
                                + "&" + str(killed) \
                                + "&" + str("{0:.2f}".format(float(killed) / float(total) * 100)) \
                                + "&" + ("{\color{ForestGreen}$\\nearrow$}" if nbMutantGen > 0 else "$\\rightarrow$") \
                                + "&" + str(nbMutant + nbMutantGen) \
                                + "&" + str("{0:.2f}".format(float(nbMutant + nbMutantGen) / float(total) *100)) \
                                + "&" + ("+" if nbMutantGen > 0 else "") + str(nbMutantGen) \
                                + "&" + ("+" + "{0:.2f}".format(float(float(nbMutantGen) / float(nbMutant)) * 100)
                                         if nbMutantGen > 0 else "0") + "\%" \
                                + "&" + str(nbIAmpl) \
                                + "&" + str(nbAAmpl) + "&"
                        '''

                if total <= 1000:
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
            if total <= 1000:
                lines.append((float(float(killed) / float(total) * 100), line))
gray = False
line_draw = False
for line in sorted(lines, key=lambda array: array[0]):
    if line[0] > 50 and not line_draw:
        print "\\hline\\hline"
        line_draw = True

    print  ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else "") + line[1]
    gray = not gray
