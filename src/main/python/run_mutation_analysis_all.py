# coding=utf-8
import sys
import read_select_classes
import subprocess
import build_rate_table
import json
from os import walk
import os.path

def run(projects=["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff","logback", "retrofit"], mvn_home="~/apache-maven-3.3.9/bin/"):

    '''
        this will for each project: read selected classes, then
            run original mutation analysis
            run A-Ampl mutation analysis
            run I-Ampl + A-Ampl mutation analysis
    '''

    cmd_pitest = mvn_home + "mvn clean test -DskipTests " \
                           "org.pitest:pitest-maven:mutationCoverage " \
                           "-DoutputFormats=CSV " \
                           "-Dmutators=ALL " \
                           "-DtimeoutConst=20000 " \
                           "-DjvmArgs=16G " \
                           "-DreportsDirectory=target/pitest-reports"
    target_classes = " -DtargetClasses="
    test_class = " -DtargetTests="
    additional_classpath_elements = " -DadditionalClasspathElements="
    excluded_classes = " -DexcludedClasses="

    prefix_dataset = "dataset"

    prefix_original = "original/per_class"
    prefix_a_ampl = "results/per_class_only_aampl"
    prefix_ampl = "results/per_class"

    suffix_results = "_mutations.csv"

    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"

    extension_java = ".java"

    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    for project in projects:
        path_to_target = prefix_dataset + "/" + project + "/" + properties_rates[project]["subModule"]
        properties = build_rate_table.load_properties(
            prefix_properties + project + extension_properties
        )
        full_qualified_names = read_select_classes.read([project])
        for full_qualified_name in full_qualified_names:
            #original
            cmd = "cd " + path_to_target + " && "
            cmd += cmd_pitest
            cmd += target_classes + properties["filter"]
            cmd += test_class + full_qualified_name[0]
            if "additionalClasspathElements" in properties:
                cmd += additional_classpath_elements + properties["additionalClasspathElements"]
            if "excludedClasses" in properties:
                cmd += excluded_classes + properties["excludedClasses"]
            print cmd
            subprocess.call(cmd, shell=True)

            path_to_pit_reports = path_to_target + "/target/pitest-reports/"
            for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
                if filenamespit:
                    for filenamepit in filenamespit:
                        if filenamepit.endswith(".csv"):
                            cmd = "cp " + dirpathpit + "/" + filenamepit + " " + \
                                  prefix_original + "/" + project + "/" + \
                                  full_qualified_name[0] + "_mutations.csv"
                            print cmd
                            subprocess.call(cmd, shell=True)
            print "-" * 30

            #Aampl
            path_to_class_through_packages = toAmplName(full_qualified_name[0]).replace(".", "/") + extension_java
            cmd = "cp " + prefix_a_ampl + "/" + project + "/" + path_to_class_through_packages
            cmd += " " + path_to_target + "/" + properties["testSrc"]
            print cmd
            subprocess.call(cmd, shell=True)

            cmd = "cd " + path_to_target + " && "
            cmd += cmd_pitest
            cmd += target_classes + properties["filter"]
            cmd += test_class + toAmplName(full_qualified_name[0])
            if "additionalClasspathElements" in properties:
                cmd += additional_classpath_elements + properties["additionalClasspathElements"]
            if "excludedClasses" in properties:
                cmd += excluded_classes + properties["excludedClasses"]
            print cmd
            subprocess.call(cmd, shell=True)

            for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
                if filenamespit:
                    for filenamepit in filenamespit:
                        if filenamepit.endswith(".csv"):
                            cmd = "cp " + dirpathpit + "/" + filenamepit + " " + \
                                  prefix_a_ampl + "/" + project + "/" + \
                                  full_qualified_name[0] + "_mutations.csv"
                            print cmd
                            subprocess.call(cmd, shell=True)

            print "-" * 30

            #Iampl + Aampl
            path_to_class_through_packages = toAmplName(full_qualified_name[0]).replace(".", "/") + extension_java
            cmd = "cp " + prefix_ampl + "/" + project + "/" + path_to_class_through_packages
            cmd += " " + path_to_target + "/" + properties["testSrc"]
            print cmd
            subprocess.call(cmd, shell=True)

            cmd = "cd " + path_to_target + " && "
            cmd += cmd_pitest
            cmd += target_classes + properties["filter"]
            cmd += test_class + toAmplName(full_qualified_name[0])
            if "additionalClasspathElements" in properties:
                cmd += additional_classpath_elements + properties["additionalClasspathElements"]
            if "excludedClasses" in properties:
                cmd += excluded_classes + properties["excludedClasses"]
            print cmd
            subprocess.call(cmd, shell=True)

            for (dirpathpit, dirnamespit, filenamespit) in walk(path_to_pit_reports):
                if filenamespit:
                    for filenamepit in filenamespit:
                        if filenamepit.endswith(".csv"):
                            cmd = "cp " + dirpathpit + "/" + filenamepit + " " + \
                                  prefix_ampl + "/" + project + "/" + \
                                  full_qualified_name[0] + "_mutations.csv"
                            print cmd
                            subprocess.call(cmd, shell=True)
            print "=" * 30

def toAmplName(name):
    package = ".".join(name.split(".")[:-1])
    name = name.split(".")[-1]
    return package + "." + (name + "Ampl" if name.startswith("Test") else "Ampl" + name)

if __name__ == '__main__':

    if len(sys.argv) > 1:
        run(projects=sys.argv[1:])
    else:
        run()

