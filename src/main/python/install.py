import subprocess
import json
import sys

def install(project):
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

    subprocess.call(cmd, shell=True)

    cmd = "cd dataset/" + project + " && "
    cmd += "~/apache-maven-3.3.9/bin/mvn clean install -DskipTests"

    subprocess.call(cmd, shell=True)

if __name__ == '__main__':
    install(sys.argv[1])


