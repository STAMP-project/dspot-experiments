import profiling_test_class
import count_mutant
import os
import json


def get_for_project_and_class(project, test_class, arrays):
    for array in arrays:
        if array[-1] == project and array[-2] == test_class:
            return array
    return None

def build_lines_for_array(table):
    gray = False
    for t in table:
        project = t[-1]
        test_class = t[-2]
        total, killed = t[0], t[1]
        nbTest, nbTestGen, totalAmpl, killedAmpl, deltaAmplTotal, deltaAmplKilled, nbIAmpl, nbAAmpl, time = 0, 0, 0, 0, 0, 0, 0, 0, 0
        if test_class in amplification_results:
            amplification_result = amplification_results[test_class]
            nbTest = amplification_result[0]
            nbTestGen = amplification_result[1]
            totalAmpl = amplification_result[2]
            deltaAmplTotal =  totalAmpl - total
            killedAmpl = amplification_result[3]
            deltaAmplKilled = killedAmpl - killed
            nbTest = amplification_result[4]
            time = amplification_result[5]
        line = "\scriptsize{" + project + "}" \
               + "&\scriptsize{" + test_class + "}" \
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
               + "&" + ("0.0" if totalAmpl == 0 else str("{0:.2f}".format(float(killedAmpl) / float(totalAmpl) * 100))) \
               + "&" + ("+" if deltaAmplKilled > 0 else "") + str(deltaAmplKilled) \
               + "&" + "+" + "{0:.2f}".format(
            float(float(deltaAmplKilled) / float(total)) * 100) + "\%" \
               + "&" + str(nbIAmpl) \
               + "&" + str(nbAAmpl) \
               + "&" + "{0:.2f}".format(float(float(time) / 1000.0 / 60.0))

        if not test_class in amplification_results:
            print "\\rowcolor[HTML]{FF6666}" + "\n" + line + "\\\\"
        else :
            print ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else "") + line + "\\\\"
        gray = not gray


root_folder = "results"

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]

types_class = ["max", "min", "avg"]
prefixDataset = "dataset"
with open(prefixDataset + "/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)

amplification_results = {}

for type_class in types_class:
    for project in projects:
        for index in range(1, 3):
            path = root_folder + "/" + project
            line = "\scriptsize{" + project + "}" + "&"
            folder = path + "_" + type_class + str(index)
            if folder.endswith("jsoup_avg2") or \
                    folder.endswith("jsoup_max1"):
                continue
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
                    nbTestGen = len(data["testCases"])
                    nbIAmpl = 0
                    nbAAmpl = 0
                    for testCase in data["testCases"]:
                        nbIAmpl += testCase["nbInputAdded"]
                        nbAAmpl += testCase["nbAssertionAdded"]

                    full_qualified_name_ampl = ".".join(data["name"].split(".")[:-1]) + "." + \
                                               (name + "Ampl" if name.startswith("Test") else "Ampl" + name)
                    totalAmpl, killedAmpl = count_mutant.countForTestClass(
                        "results/per_class/" + project + "/" + full_qualified_name_ampl + "_mutations.csv"
                    )
                    timer = filter(lambda x: x.endswith(".json") and
                                             not x == file, os.listdir(folder))[0]
                    with open(folder + "/" + timer) as data_file:
                        data = json.load(data_file)
                        time = data["classTimes"][0]["timeInMs"]
                        amplification_results[name] = [nbTest, nbTestGen, totalAmpl, killedAmpl, nbIAmpl, nbAAmpl, time]

top, worst = profiling_test_class.profile(projects=projects)

build_lines_for_array(top)
print "\\hline\\hline"
build_lines_for_array(worst)