import toolbox
import sys
import json


def run(project, classes=toolbox.keys_selected_classes):
    selected_classes = toolbox.get_test_classes_to_be_amplified(project)
    for test_class in classes:
        path_to_json_result = \
            toolbox.get_absolute_path(
                toolbox.prefix_result + project + "/" + test_class + "_amplification/" + selected_classes[
                    test_class] + "_mutants_killed.json"
            )
        with open(path_to_json_result) as data_file:
            data = json.load(data_file)
        find_best_amplified_test_methods(data["testCases"])


def ratio(input, assertions, mutants):
    return float(mutants / float((input + assertions)))


def find_best_amplified_test_methods(json_test_cases):
    '''
    By best, we mean that the amplified test methods the minimum number of modification and a maximum of mutant killed in the same method
    '''
    json_test_cases_sorted = sorted(json_test_cases,
                                    key=lambda json_test_case:
                                    - ratio(json_test_case["nbInputAdded"],
                                          json_test_case["nbAssertionAdded"],
                                          len(json_test_case["mutantsKilled"]))
                                    )
    method_specified_by_amplified_test_case = {}
    for json_test_case in json_test_cases_sorted:
        counter_per_method = {}
        json_mutant = json_test_case["mutantsKilled"]
        for mutant_killed in json_mutant:
            if not is_already_specified_by_an_amplified_test(method_specified_by_amplified_test_case, mutant_killed["locationMethod"]):
                if not mutant_killed["locationMethod"] in counter_per_method:
                    counter_per_method[mutant_killed["locationMethod"]] = 1
                else:
                    counter_per_method[mutant_killed["locationMethod"]] += 1
        method_specified_by_amplified_test_case. \
            update(select_test_case_according_to_a_ratio(json_mutant, counter_per_method, json_test_case))
    for element in method_specified_by_amplified_test_case:
        print element, method_specified_by_amplified_test_case[element]

def is_already_specified_by_an_amplified_test(method_specified_by_amplified_test_case, method):
    for element in method_specified_by_amplified_test_case:
        if method_specified_by_amplified_test_case[element][0] == method:
            return True
    return False;



def select_test_case_according_to_a_ratio(json_mutant, counter_per_method, json_test_case):
    method_specified_by_amplified_test_case = {}
    sorted_counter_per_method = [(k, v) for k, v in counter_per_method.items()]
    ratio = 1.0
    while ratio > 0.75:
        current_targeted_number = len(json_mutant) * ratio
        for tuple in sorted_counter_per_method:
            method = tuple[0]
            if counter_per_method[method] >= current_targeted_number:
                method_specified_by_amplified_test_case[json_test_case["name"]] = (
                    method, counter_per_method[method], current_targeted_number, ratio)
        if not method_specified_by_amplified_test_case:
            ratio -= 0.05
        else:
            return method_specified_by_amplified_test_case
    return method_specified_by_amplified_test_case


if __name__ == '__main__':
    toolbox.init(sys.argv)

    run(project="javapoet", classes=["top_1"])
