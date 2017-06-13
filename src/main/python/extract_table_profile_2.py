import sys
import json
import profiling_test_class
from os import walk

import count_mutant


def run(projects):
    gray = False
    results_path = "results/per_class/"
    original_path = "original/per_class/"
    for project in projects:
        results_project_path = results_path + "/" + project + "/"
        original_project_path = original_path + "/" + project + "/"
        for (dirpath, dirnames, filenames) in walk(results_project_path):
            if filenames:
                for filename in filenames:
                    if filename.endswith(".csv"):
                        test_name = filename.replace("Ampl", "")[:-len("_mutations.csv")].split(".")[-1]

                        total, killed = count_mutant.countForTestClass(
                            original_project_path + filename.replace("Ampl", ""))
                        total_ampl, killed_ampl = count_mutant.countForTestClass(dirpath + "/" + filename)

                        delta_total = total_ampl - total
                        delta_killed = killed_ampl - killed

                        perc_killed = "{0:.2f}".format(float(killed) / float(total) * 100.0)
                        perc_killed_ampl = "{0:.2f}".format(float(killed_ampl) / float(total_ampl) * 100.0)

                        perc_delta_killed = ("0.0" if killed == 0 and killed_ampl == 0 else
                                             ("+Inf" if killed == 0 and not killed_ampl == 0 else
                                              "{0:.2f}".format(float((killed_ampl - killed)) / float(killed) * 100.0)
                                              )
                                             )

                        path_to_json = dirpath + "/" + filename.replace("Ampl", "").replace("_mutations.csv",
                                                                                            "_mutants_killed.json")
                        nb_test, nb_test_ampl, i_ampl, a_ampl = profiling_test_class.load_data_from_json(path_to_json)

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
                        gray = not gray


if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback"]  # , "retrofit"]

        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff",
                    "logback", "retrofit"]

        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "jsoup", "protostuff", "logback"]

    run(projects=projects)
