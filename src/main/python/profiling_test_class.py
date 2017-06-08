import csv
from os import walk
import count_original_mutant_per_class

def remove_elements_from_array(elementsToRemove, array, indexElement, indexArray):
    element = []
    for elementToRemove in elementsToRemove:
        for a in array:
            if elementToRemove[indexElement] == a[indexArray]:
                element.append(a)
                del array[array.index(a)]
                break
    return element

def profile(projects):
    top = []
    oracle = []
    worst = []

    indexState = 5

    prefix = "original/per_class/"
    for project in projects:
        covered_killed = []
        for (dirpath, dirnames, filenames) in walk(prefix + project):
            if filenames:
                for filename in filenames:
                    total , killed = count_original_mutant_per_class.countForTestClass(prefix + project + "/" + filename)
                    if total > 0:
                        covered_killed.append((total, killed, filename.split('_')[0], project))

        selection = sorted([(float(element[0]) * 0.5 + float(element[1]) * 0.5, element[2])
                            for element in covered_killed
                            ], key=lambda array: -array[0])

        top_killers = remove_elements_from_array(selection[:2], covered_killed, 1, 2)
        worst_killers = remove_elements_from_array(selection[-2:], covered_killed, 1, 2)

        oracle_lack = [min(sorted(covered_killed, key=lambda array: float(array[1]) / float(array[0]) * 100.0),
                           key=lambda array: float(array[1]) / float(array[0]) * 100.0)]
        remove_elements_from_array(oracle_lack, covered_killed, 2, 2)
        oracle_lack.append(min(sorted(covered_killed, key=lambda array: float(array[1]) / float(array[0]) * 100.0),
                               key=lambda array: float(array[1]) / float(array[0]) * 100.0))
        remove_elements_from_array(oracle_lack, covered_killed, 2, 2)

        for top_killer in top_killers:
            top.append(top_killer)
        for oracle_lack_test in oracle_lack:
            oracle.append(oracle_lack_test)
        for worst_killer in worst_killers:
            worst.append(worst_killer)

    return top, oracle, worst


if __name__ == '__main__':
    projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
                "logback", "retrofit"]
    projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback",
                "retrofit"]
    print profile(projects)
