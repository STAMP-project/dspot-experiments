import sys
import json
import toolbox

def run(project, classes=toolbox.keys_selected_classes):
    selected_classes = toolbox.get_test_classes_to_be_amplified(project)
    for test_class in classes:
        path_to_json_result = \
            toolbox.get_absolute_path(
                toolbox.prefix_result + project + "/" + test_class + "_amplification/" + selected_classes[test_class] + "_mutants_killed.json"
            )
        with open(path_to_json_result) as data_file:
            data = json.load(data_file)
        test_cases = data["testCases"]
        ratio_per_test_case = {}
        for test_case in test_cases:
            ratio_per_test_case[test_case["name"]] = \
                float(test_case["nbMutantKilled"]) / float((test_case["nbAssertionAdded"] + test_case["nbInputAdded"]))
        ratio_per_test_case_sorted = sorted(ratio_per_test_case.items(), key=lambda x: -x[1])
        print ratio_per_test_case_sorted[0]

if __name__ == '__main__':

    toolbox.init(sys.argv)

    if "all" == sys.argv[1]:
        projects = ["javapoet",
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
        for project in projects:
            run(project)
    elif len(sys.argv) > 3:
        run(sys.argv[1], classes=sys.argv[2:])
    else:
        run(sys.argv[1])
