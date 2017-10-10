import sys
import json

def read(projects):

    with open("dataset/selected_classes.json") as data_file:
        classes = json.load(data_file)
    top = []
    worst = []
    for project in projects:
        top.append(classes[project]["top_1"])
        top.append(classes[project]["top_2"])
        worst.append(classes[project]["worst_1"])
        worst.append(classes[project]["worst_2"])

    return top, worst



if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff",
                    "logback", "retrofit"]

    print read(projects=projects)
