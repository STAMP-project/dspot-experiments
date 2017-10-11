import sys
import json

import build_rate_table


def allsame(x):
    return len(set(x)) == 1


def fullQualifiedNameToAmplifiedName(fullQualifiedName):
    return ".".join(fullQualifiedName.split(".")[:-1]) + "." + build_rate_table.buildAmplTest(
        fullQualifiedName.split(".")[-1])


def run(project, mvn_home="~/apache-maven-3.3.9/bin/",  java_home="~/jdk1.8.0_121/bin/", amplified=True):
    prefix_dataset = "dataset/"
    prefix_results = "results/october-2017/"
    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"

    #mvn_home = ""
    #java_home = ""
    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    path_to_properties ="src/main/resources/${project}.properties"
    run_pitest = java_home + "java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis " + path_to_properties + " "

    path = prefix_dataset + project + "/" + \
           (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")

    with open(prefix_dataset + "selected_classes.json") as data_file:
        selected_classes = json.load(data_file)

    properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)

    path += "target/pitest-reports"
    types = ["top_1", "top_2", "worst_1", "worst_2"]

    print "#!/usr/bin/env bash"
    print "project=" + project
    print "root_exp=${PWD}"
    print "python src/main/python/october-2017/install.py ${project}"
    print "cd " + prefix_dataset + project
    print mvn_home + "mvn install -DskipTests"
    print "cd ${root_exp}"

    for type in types:
        java_file=selected_classes[project][type]
        path_to_target = prefix_dataset + project + "/" + \
                         (properties_rates[project]["subModule"] + "/" if not properties_rates[project][
                                                                                  "subModule"] == "" else "")
        if amplified:
            print "cp ${root_exp}/" + prefix_results + project + "/" + build_rate_table.buildAmplTestPath(java_file) + ".java ${root_exp}/" + \
                  path_to_target + properties["testSrc"] + build_rate_table.buildPackageAsPath(java_file) + "/"
        print run_pitest + (fullQualifiedNameToAmplifiedName(selected_classes[project][type]) if amplified else selected_classes[project][type])
        path_to_pit_results = prefix_dataset + project + "/" + \
                         (properties_rates[project]["subModule"] + "/" if not properties_rates[project][
                                                                                  "subModule"] == "" else "")
        path_to_output_pit_results = ("results/" if amplified else "original/") + "october-2017/" + project + \
            "/" + (fullQualifiedNameToAmplifiedName(selected_classes[project][type]) if amplified else selected_classes[project][type])
        print "python src/main/python/october-2017/copy_pit_results.py " + path_to_pit_results + " " + path_to_output_pit_results
        print

if __name__ == '__main__':

    if len(sys.argv) == 1:
        print "usage is : python src/main/python/mutations_analysis.py <project> (amplified)"
    elif len(sys.argv) > 2:
        run(project=sys.argv[1], amplified=sys.argv[2] == "amplified")
    else:
        run(project=sys.argv[1], amplified=False)
