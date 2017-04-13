import sys
import os
import subprocess
import json

import select_test_classes
import extract_rates_from_file
import install


def cmdPitest(filter):
    cmd = "${HOME}/apache-maven-3.3.9/bin/mvn clean test -DskipTests " \
          "org.pitest:pitest-maven:mutationCoverage " \
          "-DreportsDirectory=target/pit-reports " \
          "-DoutputFormats=CSV " \
          "-DwithHistory " \
          "-Dmutators=ALL " \
          "-DtargetClasses=" + filter + " " \
          "-DexcludedClasses=" + fullQualifiedName  + " " \
          "-DtimeoutConst=10000 " \
          "-DjvmArgs=16G 1>/dev/null"
    print cmd
    return cmd

def buildAmplTest(test):
    if test.lower().startswith("Test"):
        return test + "Ampl"
    else:
        return "Ampl" + test


def buildPackageAsPath(fullQualifiedName):
    return "/".join(fullQualifiedName.split(".")[:-1])


def buildAmplTestPath(fullQualifiedName):
    return buildPackageAsPath(fullQualifiedName) + "/" + buildAmplTest(fullQualifiedName.split(".")[-1])


def buildPathToGeneratedPitestResults(currentPathToDataSet):
    tmp = currentPathToDataSet + "/target/pit-reports/"
    return tmp + os.listdir(tmp)[0] + "/mutations.csv"


def load_properties(filepath, sep='=', comment_char='#'):
    """
    Read the file passed as parameter as a properties file.
    """
    props = {}
    with open(filepath, "rt") as f:
        for line in f:
            l = line.strip()
            if l and not l.startswith(comment_char):
                key_value = l.split(sep)
                key = key_value[0].strip()
                value = sep.join(key_value[1:]).strip().strip('"')
                props[key] = value
    return props


def getLine(rates):
    return str(rates[0]) + "&" + \
           str(rates[4]) + " " + '{:.2%}'.format(float(rates[4]) / float(rates[0])).replace("%", "\\%") + "&" + \
           str(rates[1]) + "&" + \
           str(rates[2]) + " " + '{:.2%}'.format(float(rates[2]) / float(rates[1])).replace("%", "\\%") + "&" + \
           str(rates[3]) + " " + '{:.2%}'.format(float(rates[3]) / float(rates[1])).replace("%", "\\%") + "&" + \
           '{:.2%}'.format(float(rates[3]) / float(rates[2])).replace("%", "\\%")

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback"]
projects = ["javapoet", "mybatis", "traccar", "mustache.java", "twilio-java", "protostuff", "logback"]

prefixDspot = "/tmp/dspot-experiments/"
prefixDataset = prefixDspot + "dataset"

with open(prefixDataset + "/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)
types_class = ["max1", "max2", "min1", "min2", "avg1", "avg2"]

prefixProperties = prefixDspot + "src/main/resources/"
suffix_original = "_mutant/mutations.csv"

f = open('workfile', 'w')

for project in projects:
    install.install(project)
    subModule = properties_rates[project]["subModule"]
    isPackage = properties_rates[project]["isPackage"]
    excludedClasses = properties_rates[project]["excludedClasses"].split(":")
    rates = []
    properties = load_properties(prefixProperties + project + ".properties")
    fullQualifiedName = ""
    resultSelect = select_test_classes.select(project, excludedClasses, isPackage)
    print resultSelect
    code=subprocess.call("cd " + prefixDataset + "/" + project + "/" + subModule + " && " +
                    cmdPitest(properties["filter"]), shell=True)
    if not code == 0:
	sys.exit(code)
    original_rate = (project, extract_rates_from_file.extract(
        buildPathToGeneratedPitestResults(prefixDataset + "/" + project + "/" + subModule)
    ))
    for type_class in types_class:
        root_folder = "results"
        fullQualifiedName = resultSelect[types_class.index(type_class)][0]
        amplTestPath = root_folder + "/" + project + "_" + type_class + "/" + \
                       buildAmplTestPath(fullQualifiedName) + ".java"
        originalTestPath = prefixDataset + "/" + project + "/" + \
                           subModule + "/src/test/java/" + \
                           buildPackageAsPath(fullQualifiedName) + "/" + \
                           fullQualifiedName.split(".")[-1] + ".java"
        outputPath = prefixDataset + "/" + project + "/" + \
                     subModule + "/src/test/java/" + buildPackageAsPath(fullQualifiedName)
        code=subprocess.call("cp " + amplTestPath + " " + outputPath, shell=True)
    	if not code == 0:
	    sys.exit(code)
        #subprocess.call("rm -f " + originalTestPath, shell=True)
        subprocess.call("cd " + prefixDataset + "/" + project + "/" + subModule + " && " +
                        cmdPitest(properties["filter"]), shell=True)
        current_rate = extract_rates_from_file.extract(
            buildPathToGeneratedPitestResults(prefixDataset + "/" + project + "/" + subModule)
        )
        rates.append((fullQualifiedName.split(".")[-1], current_rate))
        fullQualifiedName = resultSelect[types_class.index(type_class)][0]
        toBeRemove = prefixDataset+ "/" +project + "/" + subModule + "/src/test/java/" \
			 + buildAmplTestPath(fullQualifiedName) + ".java"
        print "rm " + toBeRemove
        subprocess.call("rm -f " + toBeRemove, shell=True)
        #subprocess.call("cd " + prefixDataset + "/" + project + " && git checkout -- .", shell=True)

    print original_rate
    for rate in rates:
        print rate
    print original_rate[0] + "&" + getLine(original_rate[1]) + "\\\\"
    print "\\hline"
    for rate in rates:
        print rate[0] + "&" + getLine(rate[1]) + "\\\\"
    current = original_rate[0] + "&" + getLine(original_rate[1]) + "\\\\\n"
    current += "\\hline\n"
    for rate in rates:
        current += rate[0] + "&" + getLine(rate[1]) + "\\\\\n"
    f.write(current)

    




