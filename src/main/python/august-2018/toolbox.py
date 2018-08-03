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

prefix_result = "results/august-2018/"

path_to_dspot_jar = "/tmp/dspot/dspot/target/dspot-1.1.1-SNAPSHOT-jar-with-dependencies.jar"

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
key_test_source = "testSrc"


def get_amplified_name(test_class_name):
    splitted_name = test_class_name.split(".")
    return ".".join(splitted_name[:-1]) + "." + \
           (
               "Ampl" + splitted_name[-1]
               if splitted_name[-1].endswith("Test")
               else splitted_name[-1] + "Ampl"
           )

def get_path_to_amplified_test_class(class_kind, test_class_name, project):
    return prefix_result + project + "/" + class_kind + "/" + get_amplified_name(test_class_name.replace(".", "/"))

def copy_amplified_test_class(class_kind, test_class_name, project):
    properties = get_properties(project)
    module = properties[key_subModule]
    test_folder = properties[key_test_source]
    path_to_amplified_test_class = get_path_to_amplified_test_class(class_kind, test_class_name, project)
    path_to_test_src_folder = prefix_dataset + project + "/" + module + test_folder + "/"
    print_and_call(
        ["cp", path_to_amplified_test_class, path_to_test_src_folder]
    )

def get_absolute_path(path_file):
    return os.path.abspath(path_file)


def get_json_file(file_path):
    with open(file_path) as data_file:
        return json.load(data_file)


def get_test_classes_to_be_amplified(project):
    return get_json_file(path_selected_classes)[project]


def get_properties(project):
    return get_json_file(path_properties)[project]


def print_and_call(cmd, cwd=None):
    if not cwd == None:
        print cwd
    print " ".join(cmd) + " | " + " ".join(["tee", "-a", output_log_path])
    p1 = Popen(cmd, stdout=PIPE, cwd=cwd)
    p2 = Popen(["tee", "-a", output_log_path], stdin=p1.stdout, stdout=PIPE, stderr=PIPE, cwd=cwd)
    print p2.communicate()[0]


path_to_script_to_run = get_absolute_path("src/main/bash/script.sh")

def print_and_call_in_a_file(cmd, cwd=None):
    with open(path_to_script_to_run, "w") as f:
        f.write(cmd + " " + " ".join(["2>&1", "|", "tee", "-a", output_log_path]))
        f.close()
    subprocess.call(path_to_script_to_run, cwd=cwd, shell=True)


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
        maven_home = "/home/spirals/danglot/apache-maven-3.3.9/bin/"
        java_home = "/home/spirals/danglot/jdk1.8.0_121/bin/"
    else:
        maven_home = ""