import sys
import json
import subprocess
from os import walk

import build_rate_table
import install


def allsame(x):
    return len(set(x)) == 1

def run(projects, mvnHome="~/apache-maven-3.3.9/bin/"):
    prefix_dataset = "dataset/"

    run_pitest = mvnHome + "mvn clean test --quiet -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G "
    report = " -DreportsDirectory=target/pitest-reports"
    targetClasses = " -DtargetClasses="
    test_class = " -DtargetTests="

    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"

    for project in projects:
        #install.install(project)
        #subprocess.call("mkdir " + project, shell=True)
        properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
        path = prefix_dataset + project + "/" + properties_rates[project]["subModule"] + "/"
        cmd = "cd " + path + " && "
        java_files = []
        test_directory = properties["testSrc"]
        for (dirpath, dirnames, filenames) in walk(path + "/" + test_directory):
            if filenames:
                for filename in filenames:
                    if filename.endswith(".java") and "Test" in filename:
                        java_files.append(dirpath[len(path + "/" + test_directory):] + "/" + filename)

        path += "target/pitest-reports"
        package = properties["filter"]
        for java_file in java_files:
            name = java_file.split("/")[-1].split(".")[0]
            fullqualified_name =package + test_class + java_file.split(".")[0].replace("/", ".")
            subcmd = run_pitest + report + targetClasses + fullqualified_name

            if "additionalClasspathElements" in properties:
                subcmd  += " -DadditionalClasspathElements=" + properties["additionalClasspathElements"]
            if "excludedClasses" in properties:
                subcmd += " -DexcludedClasses=" + properties["excludedClasses"]

            print cmd + subcmd
            #subprocess.call(cmd + subcmd, shell=True)

            copycmd = "cp "
            for (dirpath, dirnames, filenames) in walk(path):
                if filenames:
                    copycmd += dirpath + "/" + filenames[0] + " " + project + "/" + fullqualified_name + "_mutations.csv"
                    break

            print copycmd
            #subprocess.call(copycmd, shell=True)
if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    run(projects=projects)
