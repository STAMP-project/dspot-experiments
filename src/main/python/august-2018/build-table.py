import sys
import toolbox
import json


def run():
    projects = [
        "javapoet"  # ,
        # "mybatis",
        #   "traccar",
        #   "stream-lib",
        #   "mustache.java",
        #   "twilio-java",
        #   "jsoup",
        #   "protostuff",
        #   "logback",
        #   "retrofit"
    ]

    for project in projects:
        selected_classes = toolbox.get_test_classes_to_be_amplified(project)
        print_for(["top_1", "top_2"], project, selected_classes)

    for project in projects:
        selected_classes = toolbox.get_test_classes_to_be_amplified(project)
        print_for(["worst_1", "worst_2"], project, selected_classes)

def print_for(classes, project, selected_classes):
    for test_class  in classes:
        path_to_json_result = toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" + test_class + "_amplification/" + selected_classes[test_class] + "_mutants_killed.json"
        )
        with open(path_to_json_result) as data_file:
            name, original_number_test, amplified_number_test, abs_mutation_score_original, abs_mutation_score_amplified = get_info_from_json(json.load(data_file))
            print_line(1 ,name, original_number_test, amplified_number_test, abs_mutation_score_original, abs_mutation_score_amplified, False)

def get_info_from_json(json_data):
    return json_data["name"], \
           json_data["nbOriginalTestCases"], \
           len(json_data["testCases"]), \
           json_data["nbMutantKilledOriginally"], \
           compute_number_of_additional_mutant_killed_with_amplification(json_data["testCases"])

def compute_number_of_additional_mutant_killed_with_amplification(json_test_cases):
    mutant_killed = []
    for json_test_case in json_test_cases:
        mutants_json = json_test_case["mutantsKilled"]
        for mutant_json in mutants_json:
            id_mutant_kill = ":".join([mutant_json["ID"], str(mutant_json["lineNumber"]), mutant_json["locationMethod"]])
            if not id_mutant_kill in mutant_killed:
                mutant_killed.append(id_mutant_kill)
    return len(mutant_killed)


def print_line(id,
              name,
              original_number_test,
              amplified_number_test,
              abs_mutation_score_original,
              abs_mutation_score_amplified,
              is_gray):
    print "{}" \
          "{}&{}&{}&{}&{}&{}&{}".format(
        ("\n" if is_gray else ""),
        id,
        name,
        original_number_test,
        amplified_number_test,
        abs_mutation_score_original,
        abs_mutation_score_amplified,
        ("{\\color{ForestGreen}$\\nearrow$}" if abs_mutation_score_amplified > 0 else "$\\rightarrow$")
    )


if __name__ == '__main__':
    run()

