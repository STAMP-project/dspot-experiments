import heuristic_pr
import sys
import toolbox
import json
import os.path
import math

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
    print "&\\multicolumn{3}{l}{Low \\ms}\\\\"
    print "\\hline"

    for project in projects:
        selected_classes = toolbox.get_test_classes_to_be_amplified(project)
        print_for(["worst_1", "worst_2"], project, selected_classes)

    print len(one_candidate_but_lot_of_amplified_test), one_candidate_but_lot_of_amplified_test

one_candidate_but_lot_of_amplified_test = []

def print_for(classes, project, selected_classes):
    global counter
    is_gray = False
    global one_candidate_but_lot_of_amplified_test
    for test_class in classes:
        path_to_json_result = toolbox.get_absolute_path(
            toolbox.prefix_result_october + project + "/" + selected_classes[test_class] + "_mutants_killed.json"
        )
        time = str(get_time(toolbox.prefix_result_october + project + "/" + get_json_name(project) + ".json", selected_classes[test_class]))
        if not os.path.isfile(path_to_json_result):
            return
        with open(path_to_json_result) as data_file:
            json_data = json.load(data_file)
            name, original_number_test, amplified_number_test = get_info_from_json(
                json_data)
            abs_mutation_score_original = toolbox.get_number_killed_mutants_october(
                toolbox.get_absolute_path(
                    "original/october-2017/" + project + "/" + name + "_mutations.csv")
            )
            abs_mutation_score_amplified = toolbox.get_number_killed_mutants_october(
                toolbox.get_absolute_path(
                    toolbox.prefix_result_october + project + "/" + toolbox.get_amplified_name_october(
                        name) + "_mutations.csv")
            )
            selected_amplified_test_method_for_pull_request = heuristic_pr.find_best_amplified_test_methods(json_data["testCases"])
            total_number_of_mutants = toolbox.get_total_number_mutants_october(
                toolbox.get_absolute_path(
                    toolbox.prefix_result_october + project + "/" + toolbox.get_amplified_name_october(
                        name) + "_mutations.csv")
            )
            abs_mutation_score_amplified_aampl = toolbox.get_number_killed_mutants_october(
                toolbox.get_absolute_path(
                    toolbox.prefix_result_october + project + "_aampl/" + toolbox.get_amplified_name_october(
                        name) + "_mutations.csv")
            )
            nb_candidate_for_pull_request = len(selected_amplified_test_method_for_pull_request)
            if nb_candidate_for_pull_request == 0 and amplified_number_test > 0:
                name_amplified_test_method_without_candidate.append(name)
                specified_method = []
                for amplified_test_case in json_data["testCases"]:
                    killed_mutants = amplified_test_case["mutantsKilled"]
                    for i in range(len(killed_mutants)):
                        if not killed_mutants[i]["locationMethod"] in specified_method:
                            specified_method.append(killed_mutants[i]["locationMethod"])
            if nb_candidate_for_pull_request == 1 and amplified_number_test > 1:
                one_candidate_but_lot_of_amplified_test.append( (selected_classes[test_class], counter) )

            print_line(counter,
                       name,
                       original_number_test,
                       amplified_number_test,
                       total_number_of_mutants,
                       abs_mutation_score_original,
                       abs_mutation_score_amplified,
                       abs_mutation_score_amplified_aampl,
                       nb_candidate_for_pull_request,
                       time,
                       is_gray)

            counter = counter + 1
        is_gray = not is_gray

def get_info_from_json(json_data):
    return json_data["name"], \
           json_data["nbOriginalTestCases"], \
           len(json_data["testCases"])

def get_time(path, test_class_name):
    with open(path) as data_file:
        json_data = json.load(data_file)
        for classes in json_data["classTimes"]:
            if classes["fullQualifiedName"] == test_class_name:
                return classes["timeInMs"]

def get_json_name(project):
    with open("dataset/properties_rates.json") as data_file:
        json_data = json.load(data_file)
        return json_data[project]["subModule"] if json_data[project]["subModule"] else project

name_amplified_test_method_without_candidate = []

def print_line(id,
               name,
               original_number_test,
               amplified_number_test,
               abs_number_mutant,
               abs_mutation_score_original,
               abs_mutation_score_amplified,
               abs_mutation_score_amplified_aampl,
               nb_candidate_for_pull_request,
               time,
               is_gray):
    global name_amplified_test_method_without_candidate
    # id, name, #test, ms, #atest, #candidates, #mutants_killed, #amutants_killed, increase, arrow, #aamplmutants #increase, arrow, time
    original_mutation_score = int(float(float(abs_mutation_score_original) / float(abs_number_mutant)) * 100.0)
    increase_ms_both = compute_increase_percentage(abs_mutation_score_original, abs_mutation_score_amplified)
    increase_ms_aampl = compute_increase_percentage(abs_mutation_score_original, abs_mutation_score_amplified_aampl)
    arrow_both = "{\\color{ForestGreen}$\\nearrow$}" if abs_mutation_score_amplified > abs_mutation_score_original else "$\\rightarrow$"
    arrow_aampl = "{\\color{ForestGreen}$\\nearrow$}" if abs_mutation_score_amplified_aampl > abs_mutation_score_original else "$\\rightarrow$"
    print ("\\rowcolor[HTML]{EFEFEF}\n" if is_gray else "") + "&".join([
        str(id),
        "\\scriptsize{" + name.split(".")[-1].replace("_", "\\_") + "}",
        str(original_number_test),
        str(original_mutation_score) + "\%",
        str(amplified_number_test),
        str(nb_candidate_for_pull_request),
        str(abs_mutation_score_original),
        str(abs_mutation_score_amplified),
        str(increase_ms_both) + "\%",
        arrow_both,
        str(abs_mutation_score_amplified_aampl),
        str(increase_ms_aampl) + "\%",
        arrow_aampl,
        "{0:.2f}".format(float(time) / 1000.0 / 60.0)
    ]), "\\\\"

def compute_increase_percentage(original, increased):
    value = float((float(increased - original) / float(original))) * 100.0
    if value >= 1.0:
        return str(int(value))
    else:
        return str(math.ceil(value*100)/100)

if __name__ == '__main__':
    run()
    print name_amplified_test_method_without_candidate
