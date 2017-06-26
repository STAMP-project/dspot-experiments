import sys
import subprocess
import json

def read(projects):

    cmd = "~/jdk1.8.0_121/jre/bin/java -Xms16G -Xmx32G" \
          " -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar" \
          " --path-to-properties src/main/resources/{}.properties" \
          " --iteration 3" \
          " --output-path {}" \
          " --maven-home /home/spirals/danglot/apache-maven-3.3.9/" \
          " -t {}" \
          " -m original/per_class/{}/{}_mutations.csv"

    with open("dataset/selected_classes.json") as data_file:
        classes = json.load(data_file)
    for project in projects:
        for clazz in classes[project]:
            subprocess.call(cmd.format(project, project, classes[project][clazz], project, classes[project][clazz], shell=True))



if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff",
                    "logback", "retrofit"]

    print read(projects=projects)