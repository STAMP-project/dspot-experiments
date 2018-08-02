from subprocess import PIPE, Popen
import subprocess
import json
import os

# env
maven_home = ""
java_home = ""

# paths
prefix_dataset = "dataset/"
path_selected_classes = prefix_dataset + "selected_classes.json"
path_properties = prefix_dataset + "properties_rates.json"
prefix_current_dataset = prefix_dataset + "august-2018/"
output_log_path = ""

prefix_resources = "src/main/resources/"
suffix_properties = ".properties"

# variables
top_1 = "top_1"
top_2 = "top_2"
worst_1 = "worst_1"
worst_2 = "worst_2"
keys_selected_classes = [top_1, top_2, worst_1, worst_2]
key_excludedClasses = "excludedClasses"
key_subModule = "subModule"
key_filter = "filter"

def getAbsolutePath(pathfile):
    return os.path.abspath(pathfile)

def getJsonFile(filePath):
    with open(filePath) as data_file:
        return json.load(data_file)

def getTestClassesToBeAmplified(project):
    return getJsonFile(path_selected_classes)[project]

def getProperties(project):
    return getJsonFile(path_properties)[project]

def printAndCall(cmd, cwd=None):
    if not cwd == None:
        print cwd
    print " ".join(cmd) + " | " + " ".join(["tee", "-a", output_log_path])
    p1 = Popen(cmd, stdout=PIPE, cwd=cwd)
    p2 = Popen(["tee", "-a", output_log_path], stdin=p1.stdout, stdout=PIPE, cwd=cwd)
    print p2.communicate()[0]

def load_properties(filepath, sep='=', comment_char='#'):
    """
    Read the file passed as parameter as a properties file.
    """
    props = {}
    with open(prefix_resources + filepath + suffix_properties, "rt") as f:
        for line in f:
            l = line.strip()
            if l and not l.startswith(comment_char):
                key_value = l.split(sep)
                key = key_value[0].strip()
                value = sep.join(key_value[1:]).strip().strip('"')
                props[key] = value
    return props


def init(argv):
    global maven_home
    global java_home
    if "onClusty" in argv:
        maven_home = "~/apache-maven-3.3.9/bin/"
        java_home = "~/jdk1.8.0_121/bin/"
    else:
        maven_home = ""
