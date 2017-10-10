import count_mutant
import sys
from os import walk


def run(projects):
    prefix = "original"
    file = "mutations.csv"
    for project in projects:
        covered, killed = count_mutant.countForTestClass(prefix + "/" + project + "/" + file)
        print "{}&{}&{}\\\\".format(covered, killed, round(float(killed) / float(covered) * 100.0, 2))

if __name__ == '__main__':
    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

run(projects=projects)