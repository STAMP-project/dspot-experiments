import sys
import json

import build_rate_table


def allsame(x):
    return len(set(x)) == 1


def fullQualifiedNameToAmplifiedName(fullQualifiedName):
    return ".".join(fullQualifiedName.split(".")[:-1]) + "." + build_rate_table.buildAmplTest(
        fullQualifiedName.split(".")[-1])


def run(project, mvnHome="~/apache-maven-3.3.9/bin/", amplified=True):
    prefix_dataset = "dataset/"
    prefix_results = "results/per_class2/"
    #mvnHome = ""
    run_pitest = mvnHome + "mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G "
    report = " -DreportsDirectory=target/pitest-reports"
    target_classes = " -DtargetClasses="
    target_tests = " -DtargetTests="
    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)
    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"
    properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
    path = prefix_dataset + project + "/" + \
           (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")
    cmd = "cd " + path
    with open("dataset/selected_classes.json") as data_file:
        selected_classes = json.load(data_file)
    path += "target/pitest-reports"
    target_classes_values = properties["filter"]
    types = ["top_1", "top_2", "worst_1", "worst_2"]

    print "#!/usr/bin/env bash"

    print "project=" + project
    print "root_exp=${PWD}"

    print "python src/main/python/install.py ${project}"
    print "cd dataset/${project}/"
    print mvnHome + "mvn install -DskipTests"
    print "cd ${root_exp}"


    for type in types:
        java_file = selected_classes[project][type]
        path_to_target = prefix_dataset + project + "/" + \
                         (properties_rates[project]["subModule"] + "/" if not properties_rates[project][
                                                                                  "subModule"] == "" else "")
        if amplified:
            print "cp ${root_exp}/" + prefix_results + project + "/" + build_rate_table.buildAmplTestPath(java_file) + ".java ${root_exp}/" + \
                  path_to_target + "src/test/java/" + build_rate_table.buildPackageAsPath(java_file) + "/"
        cmd_pit = run_pitest + report
        cmd_pit += target_classes + target_classes_values + " "
        cmd_pit += target_tests + (fullQualifiedNameToAmplifiedName(selected_classes[project][type]) if amplified else selected_classes[project][type])
        if "additionalClasspathElements" in properties:
            cmd_pit += " -DadditionalClasspathElements=" + properties["additionalClasspathElements"]
        if "excludedClasses" in properties:
            cmd_pit += " -DexcludedClasses=" + properties["excludedClasses"]
        print cmd
        print cmd_pit + " >> /dev/null"
        print "cd ${root_exp}"
        print "python src/main/python/copy_pit_results.py " + path_to_target + " " + \
              (fullQualifiedNameToAmplifiedName(selected_classes[project][type])
               if amplified else selected_classes[project][type])
        if amplified:
            print "to_download_" + type + "=$(curl --upload-file " + \
              fullQualifiedNameToAmplifiedName(selected_classes[project][type]) + "_mutations.csv " + "https://transfer.sh/" + \
              (fullQualifiedNameToAmplifiedName(selected_classes[project][type])
               if amplified else selected_classes[project][type]) + "_mutations.csv)"
        else:
            print "to_download_" + type + "=$(curl --upload-file " + \
                  selected_classes[project][type] + "_mutations.csv " + "https://transfer.sh/" + \
                  selected_classes[project][type] + "_mutations.csv)"

    for type in types:
        print "echo \"curl ${to_download_" + type + "} -o " + \
              (fullQualifiedNameToAmplifiedName(selected_classes[project][type])
               if amplified else selected_classes[project][type]) + "_mutations.csv\""


if __name__ == '__main__':

    if len(sys.argv) == 1:
        print "usage is : python src/main/python/mutations_analysis.py <project> (amplified)"
    elif len(sys.argv) > 2:
        run(project=sys.argv[1], amplified=sys.argv[2] == "amplified")
    else:
        run(project=sys.argv[1], amplified=False)
