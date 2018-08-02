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

if __name__ == '__main__':
    if "all" == sys.argv[1]:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback",
                    "retrofit"]
        for project in projects:
            install(project)

    install(sys.argv[1])


