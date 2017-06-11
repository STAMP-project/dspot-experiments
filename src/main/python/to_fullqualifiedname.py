import json
from os import walk
from itertools import takewhile, izip
import os
import install
import build_rate_table
import subprocess

prefix_dataset = "dataset/"

prefix = "original/per_class/"

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]

with open("dataset/properties_rates.json") as data_file:
    properties_rates = json.load(data_file)

prefix_properties = "src/main/resources/"
extension_properties = ".properties"

for project in projects:
    print project
    properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
    test_directory = properties["testSrc"]
    path = prefix_dataset + project + "/" + properties_rates[project]["subModule"] + "/"
    cmd = "cd " + path + " && "
    java_files = []
    for (dirpath, dirnames, filenames) in walk(path + "/" + test_directory):
        if filenames:
            for filename in filenames:
                if filename.endswith(".java") and "Test" in filename:
                    java_files.append(dirpath[len(path + "/" + test_directory):] + "/" + filename)

    print java_files

    for (dirpath, dirnames, filenames) in walk("original/per_class/" + project):
        if filenames:
            for filename in filenames:
                test_class_name = filename.split("_")[0]
                for java_file in java_files:
                    if test_class_name == java_file.split(".")[0].split("/")[-1]:
                        cmd = "mv " + dirpath + "/" + filename + " " + dirpath + "/" + java_file.split(".")[0].replace(
                            "/", ".") + "_mutations.csv"
                        print cmd
                        subprocess.call(cmd, shell=True)
    print 30 * "="
    #    print java_file.split(".")[0].replace("/",".")
