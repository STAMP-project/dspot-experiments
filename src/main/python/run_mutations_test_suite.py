import json
import subprocess
import sys
from os import walk

import build_rate_table


def run(projects, mvnHome="~/apache-maven-3.3.9/bin/"):
    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    run_pitest = mvnHome + "mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G -DreportsDirectory=target/pitest-reports"
    targetClasses = " -DtargetClasses="

    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"
    path_to_dataset = "dataset"
    prefix = "original"

    for project in projects:
        properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
        cmd = "cd " + path_to_dataset + "/" + project + "/" + properties_rates[project]["subModule"] + " && "
        cmd += run_pitest
        cmd += targetClasses + properties["filter"]
        print cmd
        subprocess.call(cmd, shell=True)

        path_to_pit_reports = path_to_dataset + "/" + project + "/" + "target/pitest-reports/"
        for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
            if filenamespit:
                for filenamepit in filenamespit:
                    if filenamepit.endswith(".csv"):
                        cmd = "cd " + path_to_dataset + "/" + project + "/" + properties_rates[project]["subModule"] + " && "
                        cmd += "cp " + dirpathpit + "/" + filenamepit + " " + prefix + "/" + project + "/new_mutations.csv"
                        print cmd
                        subprocess.call(cmd, shell=True)

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
                    "logback", "retrofit"]
    run(projects=projects)
