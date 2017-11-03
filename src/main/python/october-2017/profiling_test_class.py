from os import walk
import json
import count_mutant
import sys
import build_rate_table
import math


def profile(projects):
    top = []
    worst = []
    prefix = "original/june-2017/per_class/"

    blacklist = ["com.github.mustachejavabenchmarks.simple.SimpleBenchmarkTest_mutations.csv",
                 "io.protostuff.ProtostuffDelimiterTest_mutations.csv",
                 "org.traccar.protocol.ApelProtocolDecoderTest_mutations.csv",
                 "org.apache.ibatis.type.NClobTypeHandlerTest_mutations.csv",
                 "org.apache.ibatis.type.ClobTypeHandlerTest_mutations.csv",
                 ]

    for project in projects:
        results = []
        for (dirpath, dirnames, filenames) in walk(prefix + project):
            if filenames:
                for filename in filenames:
                    if filename not in blacklist:
                        total, killed = count_mutant.countForTestClass(prefix + project + "/" + filename)
                        if 0 < total <= 1000 and total > killed:
                            results.append((total, killed, filename[:-len("_mutations.csv")], project, float(killed) / float(total) * 100.0))
        sorted_results = sorted(
            sorted(sorted(sorted(results, key=lambda result: result[2]), key=lambda result: result[1]),
                   key=lambda result: result[0]),
            key=lambda result: float(result[1]) / float(result[0]) * 100.0
        )

        print "=" * 30
        for result in sorted_results:
            print result, "{0:.2f}".format(float(result[1]) / float(result[0]) * 100.0)
        print "=" * 30

        worst.append(sorted_results[0])
        worst.append(sorted_results[1])
        top.append(sorted_results[-1])
        top.append(sorted_results[-2])

    return top, worst


def load_data_from_json(path):
    with open(path.replace("Ampl", "")) as data_file:
        json_report = json.load(data_file)
        i_ampl, a_ampl = 0, 0
        for testCase in json_report["testCases"]:
            a_ampl += testCase["nbAssertionAdded"]
            i_ampl += testCase["nbInputAdded"]
        return json_report["nbOriginalTestCases"], \
               len(json_report["testCases"]), \
               "{0:.2f}".format(float(i_ampl) / len(json_report["testCases"])) \
                   if len(json_report["testCases"]) > 0 else "0.0", \
               "{0:.2f}".format(float(a_ampl) / len(json_report["testCases"])) \
                   if len(json_report["testCases"]) > 0 else "0.0"


def print_line(t, gray):
    prefix_result = "results/per_class/"

    total, killed = t[0], t[1]
    project = t[-1]
    test_name = t[2]
    test_name_ampl = '.'.join(test_name.split(".")[:-1]) + '.' + \
                     ("Ampl" + test_name.split(".")[-1] if test_name.split(".")[-1].endswith("Test") else
                      test_name.split(".")[-1] + "Ampl")
    path_to_file = prefix_result + project + "/" + test_name_ampl
    path_to_csv = path_to_file + "_mutations.csv"
    total_ampl, killed_ampl = count_mutant.countForTestClass(path_to_csv)

    delta_total = total_ampl - total
    delta_killed = killed_ampl - killed

    perc_killed = ("{0:.2f}".format(float(killed) / float(total) * 100.0) if total > 0 else "0.0")
    perc_killed_ampl = ("{0:.2f}".format(float(killed_ampl) / float(total_ampl) * 100.0) if total_ampl > 0 else "0.0")
    perc_delta_killed = ("{0:.2f}".format(float((killed_ampl - killed)) / float(killed) * 100.0) if killed > 0 else "0.0")

    path_to_json = path_to_file + "_mutants_killed.json"
    nb_test, nb_test_ampl, i_ampl_avg, a_ampl_avg = load_data_from_json(path_to_json)

    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    path_to_json_fail_time = prefix_result + project + "/" + \
                             ("/" + properties_rates[project]["subModule"] if properties_rates[project]["subModule"] else project) +\
                             ".json"
    with open(path_to_json_fail_time) as data_file:
        json_time = json.load(data_file)
    for time_class in json_time["classTimes"]:
        if time_class["fullQualifiedName"] == test_name:
            time_ms = time_class["timeInMs"]

    time_min = "{0:.2f}".format(float(time_ms) / 1000.0 / 60.0)

    print "{}{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
        "\scriptsize{" + project + "}", "\scriptsize{" + test_name.split(".")[-1].replace("_", "\\_") + "}",
        nb_test, nb_test_ampl,
        total,
        ("{\color{ForestGreen}$\\nearrow$}" if delta_total > 0 else "$\\rightarrow$"),
        total_ampl, delta_total,
        killed,
        ("{\color{ForestGreen}$\\nearrow$}" if delta_killed > 0 else "$\\rightarrow$"),
        killed_ampl, delta_killed,
        perc_killed,
        perc_killed_ampl, perc_delta_killed, i_ampl_avg, a_ampl_avg, time_min
    )

cpt = 1

def print_line_2(t, gray):
    global cpt
    prefix_result = "results/per_class/"

    total, killed = t[0], t[1]
    score = round(float(killed) / float(total) * 100.0, 2)
    project = t[-1]
    test_name = t[2]
    test_name_ampl = '.'.join(test_name.split(".")[:-1]) + '.' + \
                     ("Ampl" + test_name.split(".")[-1] if test_name.split(".")[-1].endswith("Test") else
                      test_name.split(".")[-1] + "Ampl")
    path_to_file = prefix_result + project + "/" + test_name_ampl
    path_to_csv = path_to_file + "_mutations.csv"
    total_ampl, killed_ampl = count_mutant.countForTestClass(path_to_csv)

    delta_total = float((total_ampl - total)) / float(total) * 100.0 \
        if total > 0 else 0.0
    delta_killed = float((killed_ampl - killed)) / float(killed) * 100.0 \
        if killed > 0 else 0.0

    path_to_json = path_to_file + "_mutants_killed.json"
    nb_test, nb_test_ampl, i_ampl_avg, a_ampl_avg = load_data_from_json(path_to_json)

    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    path_to_json_fail_time = prefix_result + project + "/" + \
                             ("/" + properties_rates[project]["subModule"] if properties_rates[project]["subModule"] else project) + \
                             ".json"
    with open(path_to_json_fail_time) as data_file:
        json_time = json.load(data_file)
    for time_class in json_time["classTimes"]:
        if time_class["fullQualifiedName"] == test_name:
            time_ms = time_class["timeInMs"]

    time_min = "{0:.2f}".format(float(time_ms) / 1000.0 / 60.0)

    print "{}{}&{}&{}&{}&{}\\%&{}&{}&{}&{}\\%&{}&{}&{}&{}\\%&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
        cpt,
        "\small{" + project + "}", "\small{" + test_name.split(".")[-1].replace("_", "\\_") + "}",
        nb_test, score,
        nb_test_ampl,
        total, total_ampl,
        round(delta_total, 2), ("{\color{ForestGreen}$\\nearrow$}" if not float(delta_total) == 0.0 else "$\\rightarrow$"),
        killed, killed_ampl,
        round(delta_killed, 2), ("{\color{ForestGreen}$\\nearrow$}" if not float(delta_killed) == 0.0 else "$\\rightarrow$"),
        time_min
    )
    cpt += 1
    return delta_total, delta_killed

def print_line_3(t, gray):
    global cpt
    prefix_result = "results/october-2017/"

    total, killed = t[0], t[1]
    score = round(float(killed) / float(total) * 100.0, 2)
    project = t[-1]
    with open("dataset/selected_classes.json") as data_file:
        classes = json.load(data_file)

    test_name = t[2]
    test_name_ampl = '.'.join(test_name.split(".")[:-1]) + '.' + \
                     (build_rate_table.buildAmplTest(test_name.split(".")[-1]))
    path_to_file = prefix_result + project + "/" + test_name_ampl
    path_to_csv = path_to_file + "_mutations.csv"
    total_ampl, killed_ampl = count_mutant.countForTestClass(path_to_csv)

    covered_aampl, killed_aampl = count_mutant.countForTestClass(
        prefix_result + project + "_aampl/" + test_name_ampl + "_mutations.csv")

    delta_total = float((total_ampl - total)) / float(total) * 100.0 \
        if total > 0 else 0.0
    delta_killed = float((killed_ampl - killed)) / float(killed) * 100.0 \
        if killed > 0 else 0.0
    delta_killed_aampl = (float(killed_aampl - killed) / float(killed) * 100.0) \
        if killed > 0 else 0.0

    path_to_json = path_to_file + "_mutants_killed.json"
    nb_test, nb_test_ampl, i_ampl_avg, a_ampl_avg = load_data_from_json(path_to_json)

    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)

    path_to_json_fail_time = prefix_result + project + "/" + \
                             ("/" + properties_rates[project]["subModule"] if properties_rates[project]["subModule"] else project) + \
                             ".json"
    time_ms = -1
    with open(path_to_json_fail_time) as data_file:
        json_time = json.load(data_file)
    for time_class in json_time["classTimes"]:
        if time_class["fullQualifiedName"] == test_name:
            time_ms = time_class["timeInMs"]

    time_min = "{0:.2f}".format(float(time_ms) / 1000.0 / 60.0)

    # COLOR ID & testName & nbMethod & PMS & nbAmplMethod & ExpCovOri & ExpCovAmpl & KilledOri & KilledAmpl & IncreaseKilled & Arrow & AAmplKilled &
    # Increase & Arrow & time

    is_pull_request = False
    if "pull_request" in classes[project]:
        is_pull_request = classes[project]["pull_request"] == test_name


    print "{}{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
        cpt,
        "\small{" + test_name.split(".")[-1].replace("_", "\\_") + "}" + ("\\textbf{*}" if is_pull_request else ""),
        nb_test,
        "0" if score == 0.0 else
        (round(score, 1) if (score < 1.0) else int(round(score, 1))),
        nb_test_ampl,
        total, total_ampl,
        killed, killed_ampl,
        "0" if delta_killed == 0.0 else \
        (round(delta_killed, 1) if (delta_killed < 1.0) else int(round(delta_killed, 1))),
        ("{\color{ForestGreen}$\\nearrow$}" if not float(delta_killed) == 0 else "$\\rightarrow$"),
        killed_aampl,
        "0" if delta_killed_aampl == 0.0 else \
        (round(delta_killed_aampl, 1) if (delta_killed_aampl < 1.0) else int(round(delta_killed_aampl, 1))),
        ("{\color{ForestGreen}$\\nearrow$}" if not float(delta_killed_aampl) == 0 else "$\\rightarrow$"),
        time_min
    )
    cpt += 1
    return delta_total, delta_killed, nb_test, score, nb_test_ampl

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    top, worst = profile(projects)

    print "=" * 30

    for e in top:
        print e

    print "=" * 30

    for e in worst:
        print e
