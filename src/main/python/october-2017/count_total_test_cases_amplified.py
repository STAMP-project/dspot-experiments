from os import walk
import sys
import json
import read_select_classes

def run(projects):
    prefix = "results/october-2017/"
    amplified_test_case = []
    for project in projects:
        path = prefix + project + "/"
        for (dirpath, dirnames, filenames) in walk(path):
            if filenames:
                for filename in filenames:
                    if filename.endswith("_mutants_killed.json"):
                        print dirpath + filename
                        path_to_json = dirpath + filename
                        with open(path_to_json) as data_file:
                            json_amplification = json.load(data_file)
                            for testCase in json_amplification["testCases"]:
                                name_of_test_case = project + "#" + \
                                                    filename + "#" + \
                                                    testCase["name"].split("_")[0]
                                if not name_of_test_case in amplified_test_case:
                                    amplified_test_case.append(name_of_test_case)
    print "number of amplified test cases : ", len(amplified_test_case)

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = [ "javapoet",
                     "mybatis",
                     "traccar",
                     "stream-lib",
                     "mustache.java",
                     "twilio-java",
                     "jsoup",
                     "protostuff",
                     "logback",
                     "retrofit"
                     ]

    run(projects=projects)
