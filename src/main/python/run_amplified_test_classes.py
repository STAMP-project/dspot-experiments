from os import walk
import json
import build_rate_table
import subprocess
import install
import sys

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
    prefix = "results/"
    types_class = ["max", "min", "avg"]
    cmd = "mkdir results/per_class/"
    subprocess.call(cmd, shell=True)

    for project in projects:
        install.install(project)
        properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
        test_directory = properties["testSrc"]
        cmd = "mkdir results/per_class/" + project
        subprocess.call(cmd, shell=True)
        for type in types_class:
            for i in range(1, 2):
                path = prefix + project + "_" + type + str(i)
                for (dirpath, dirnames, filenames) in walk(path):
                    if filenames:
                        for filename in filenames:
                            if filename.endswith(".java"):
                                cmd = "cp " + dirpath + "/" + filename + " " +\
                                      path_to_dataset + project + "/" + properties_rates[project]["subModule"] + "/" \
                                      + test_directory + dirpath[len(path):] + "/"
                                print cmd
                                subprocess.call(cmd, shell=True)
                                name_test_class = (dirpath[len(path) + 1:] + "/" + filename).split(".")[0].replace("/", ".")
                                cmd = "cd " + path_to_dataset + project + "/" + properties_rates[project]["subModule"] + " && " + \
                                    run_pitest + report + targetClasses + properties["filter"] + \
                                      test_class + name_test_class
                                print cmd
                                subprocess.call(cmd, shell=True)
                                path_to_pit_reports = path_to_dataset + project + "/" + properties_rates[project]["subModule"] + "/" + "target/pitest-reports"
                                for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
                                    if filenamespit:
                                        for filenamepit in filenamespit:
                                            if filenamepit.endswith(".csv"):
                                                cmd = "cp " + dirpathpit + "/" + filenamepit + " " + \
                                                    prefix + "per_class/" + project + "/" + name_test_class + "_mutations.csv"
                                                print cmd
                                                subprocess.call(cmd, shell=True)
                                cmd = "rm " + test_directory + dirpath[len(path):] + "/" + \
                                      ("Ampl*" if filename.startswith("Ampl") else "*Ampl.java")
                                print cmd
                                subprocess.call(cmd, shell=True)


if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
                    "logback", "retrofit"]
    run(projects=projects)
