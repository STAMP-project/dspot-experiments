import subprocess
import json
import sys

project=sys.argv[1]

with open('dataset/dataset.json') as data_file:
    data = json.load(data_file)

print data[project]["url"]

cmd = "git clone "
cmd += data[project]["url"]
cmd += " dataset/"
cmd += project

subprocess.call(cmd, shell=True)

cmd = "git reset --hard"
cmd += data[project]["commitid"]

subprocess.call(cmd, shell=True)