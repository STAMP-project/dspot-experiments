import heuristic_pr
import sys
import toolbox
import json
import os.path


counter = 1


def run():
    projects = [
        "javapoet",
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
        selected_classes = toolbox.get_test_classes_to_be_amplified(project)
        print_for(["top_1", "top_2"], project, selected_classes)

    print "\\hline"

    for project in projects:
        selected_classes = toolbox.get_test_classes_to_be_amplified(project)
        print_for(["worst_1", "worst_2"], project, selected_classes)


def print_for(classes, project, selected_classes):
    global counter
    is_gray = False
    for test_class in classes:
        path_to_json_result = toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" + test_class + "_amplification/" + selected_classes[
                test_class] + "_mutants_killed.json"
        )
        if not os.path.isfile(path_to_json_result):
            return
        with open(path_to_json_result) as data_file:
            json_data = json.load(data_file)
            name, original_number_test, amplified_number_test, abs_mutation_score_original, abs_mutation_score_amplified = get_info_from_json(json_data)
            selected_amplified_test_method_for_pull_request = heuristic_pr.find_best_amplified_test_methods(json_data["testCases"])
            print_line(counter,
                       name,
                       original_number_test,
                       amplified_number_test,
                       abs_mutation_score_original,
                       abs_mutation_score_amplified,
                       len(selected_amplified_test_method_for_pull_request),
                       is_gray
                       )

            counter = counter + 1
        is_gray = not is_gray


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
            id_mutant_kill = ":".join(
                [mutant_json["ID"], str(mutant_json["lineNumber"]), mutant_json["locationMethod"]])
            if not id_mutant_kill in mutant_killed:
                mutant_killed.append(id_mutant_kill)
    return len(mutant_killed)


def print_line(id,
               name,
               original_number_test,
               amplified_number_test,
               abs_mutation_score_original,
               abs_mutation_score_amplified,
               nb_candidate_for_pull_request,
               is_gray):
    # id, name, #test, ms, #atest, #candidates, #mutants, #amutants, increase, arrow, #aamplmutants #increase, arrow, time
    print "{}" \
          "{}&{}&{}&{}&{}&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}\n" if is_gray else ""),
        id,
        original_number_test,
        amplified_number_test,
        abs_mutation_score_original,
        str(abs_mutation_score_original + abs_mutation_score_amplified),
        ("{\\color{ForestGreen}$\\nearrow$}" if abs_mutation_score_amplified > 0 else "$\\rightarrow$"),
        str(nb_candidate_for_pull_request)
    )


if __name__ == '__main__':
    run()
