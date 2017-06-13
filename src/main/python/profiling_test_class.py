from os import walk
import json
import count_mutant
import sys


def profile(projects):
    top = []
    worst = []
    prefix = "original/per_class/"

    blacklist = ["com.github.mustachejavabenchmarks.simple.SimpleBenchmarkTest_mutations.csv",
                 "io.protostuff.ProtostuffDelimiterTest_mutations.csv",
                 "org.traccar.protocol.ApelProtocolDecoderTest_mutations.csv"
                 ]

    for project in projects:
        results = []
        for (dirpath, dirnames, filenames) in walk(prefix + project):
            if filenames:
                for filename in filenames:
                    if filename not in blacklist:
                        total, killed = count_mutant.countForTestClass(prefix + project + "/" + filename)
                        if 0 < total <= 1000:
                            results.append((total, killed, filename[:-len("_mutations.csv")], project))
        sorted_results = sorted(
            sorted(
                sorted(
                    sorted(results, key=lambda result: float(result[1]) / float(result[0]) * 100.0),
                    key=lambda result: result[0]
                ), key=lambda result: result[1]
            ), key=lambda result: result[2]
        )

        for result in sorted_results:
            print result

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
        return json_report["nbOriginalTestCases"], len(json_report["testCases"]), i_ampl, a_ampl


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

    perc_killed = "{0:.2f}".format(float(killed) / float(total) * 100.0)
    perc_killed_ampl = "{0:.2f}".format(float(killed_ampl) / float(total_ampl) * 100.0)

    perc_delta_killed = "{0:.2f}".format(float((killed_ampl - killed)) / float(killed) * 100.0)

    path_to_json = path_to_file + "_mutants_killed.json"
    nb_test, nb_test_ampl, i_ampl, a_ampl = load_data_from_json(path_to_json)

    print "{}{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
        "\small{" + project + "}", "\small{" + test_name.split(".")[-1] + "}",
        nb_test, nb_test_ampl,
        total,
        ("{\color{ForestGreen}$\\nearrow$}" if delta_total > 0 else "$\\rightarrow$"),
        total_ampl, delta_total,
        killed,
        ("{\color{ForestGreen}$\\nearrow$}" if delta_killed > 0 else "$\\rightarrow$"),
        killed_ampl, delta_killed,
        perc_killed,
        perc_killed_ampl, perc_delta_killed, i_ampl, a_ampl, 0
    )


if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff",
                    "logback", "retrofit"]

    top, worst = profile(projects)

    print "=" * 30

    for e in top + worst:
        print e
