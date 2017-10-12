from os import walk
import sys
import subprocess

def run(path_to_project, fullqualified_name):
    path = path_to_project + "/target/pit-reports/"
    for (dirpath, dirnames, filenames) in walk(path):
        if filenames:
            copycmd = "cp " + dirpath + "/" + filenames[0] + " " + fullqualified_name + "_mutations.csv"
            subprocess.call(copycmd, shell=True)

if __name__ == '__main__':
    run(path_to_project=sys.argv[1], fullqualified_name=sys.argv[2])