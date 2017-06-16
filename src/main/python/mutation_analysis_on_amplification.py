import json
import subprocess
import sys
from os import walk
import os.path

import profiling_test_class
import build_rate_table
import install


def run(projects, mvnHome="~/apache-maven-3.3.9/bin/"):
    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    run_pitest = mvnHome + "mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G "
    report = " -DreportsDirectory=target/pitest-reports"
    targetClasses = " -DtargetClasses="
    test_class = " -DtargetTests="

    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"
    path_to_dataset = "dataset/"
    prefix = "results/per_class/"

    for project in projects:
        #install.install(project)
        properties = build_rate_table.load_properties(
            prefix_properties + project + extension_properties
        )
        test_directory = properties["testSrc"]
        top, worst = profiling_test_class.profile(projects=[project])
        path_to_module = path_to_dataset + project + "/" + properties_rates[project]["subModule"]
        path_to_test_directory = path_to_module + "/" + test_directory + "/"
        for element in top + worst:
            test_name_ampl = '.'.join(element[2].split(".")[:-1]) + '.' + \
                             ("Ampl" + element[2].split(".")[-1] if element[2].split(".")[-1].endswith("Test") else
                              element[2].split(".")[-1] + "Ampl")

            if not os.path.isfile(prefix + project + "/" + test_name_ampl + "_mutations.csv") or True:
                print test_name_ampl
                filename = prefix + project + "/" + test_name_ampl.replace(".", "/") + ".java"
                cmd = "cp " + filename + " " + path_to_test_directory
                print cmd
               	subprocess.call(cmd, shell=True)

                cmd = "cd " + path_to_module + " && "
                cmd += run_pitest
                cmd += report
                cmd += targetClasses + properties["filter"]
                cmd += test_class + test_name_ampl
                if "additionalClasspathElements" in properties:
                    cmd += " -DadditionalClasspathElements=" + properties["additionalClasspathElements"]
                if "excludedClasses" in properties:
                    cmd += " -DexcludedClasses=" + properties["excludedClasses"]
                print cmd
                subprocess.call(cmd, shell=True)

                path_to_pit_reports = path_to_module + "/target/pitest-reports/"
                for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
                    if filenamespit:
                        for filenamepit in filenamespit:
                            if filenamepit.endswith(".csv"):
                                cmd = "cp " + dirpathpit + "/" + filenamepit + " " + \
                                      prefix + project + "/" + test_name_ampl + "_mutations.csv"
                                print cmd
                                subprocess.call(cmd, shell=True)


if __name__ == '__main__':
    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback"]#, "retrofit"]
    run(projects=projects)
