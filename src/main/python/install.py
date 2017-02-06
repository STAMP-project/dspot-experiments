import subprocess
import json
import sys

project=sys.argv[1]


with open('dataset/dataset.json') as data_file:
    data = json.load(data_file)

cmd = "git clone "
cmd += data[project]["url"]
cmd += " dataset/"
cmd += project

subprocess.call(cmd, shell=True)

cmd = "cd dataset/" + project + " && "
cmd += "git reset --hard "
cmd += data[project]["commitid"]

print cmd

subprocess.call(cmd, shell=True)
