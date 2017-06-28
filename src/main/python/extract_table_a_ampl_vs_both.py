import sys
import json
import count_mutant

def run(projects):
    suffix = "_mutations.csv"
    prefix_original = "original/per_class"
    prefix_assert_aampl = "results/per_class_only_aampl"
    prefix_assert = "results/per_class"

    types = ["top_1", "top_2", "worst_1", "worst_2"]

    with open("dataset/selected_classes.json") as data_file:
        classes = json.load(data_file)

    gray = False

    for project in projects:
        for type in types:
            full_qualified_name = classes[project][type]
            amplified_name = ".".join(full_qualified_name.split(".")[:-1]) + "." + \
                             ("Ampl" + full_qualified_name.split(".")[-1] \
                            if full_qualified_name.split(".")[-1].endswith("Test") \
                            else  full_qualified_name.split(".")[-1] + "Ampl")

            covered, killed = count_mutant.countForTestClass(prefix_original + "/" + project + "/" + full_qualified_name + suffix)
            covered_both, killed_both = count_mutant.countForTestClass(prefix_assert + "/" + project + "/" + amplified_name + suffix)
            covered_aampl, killed_aampl = count_mutant.countForTestClass(prefix_assert_aampl + "/" + project + "/" + amplified_name + suffix)

            delta_covered_aampl = (float(covered_aampl - covered) / float(covered) * 100.0) if covered > 0 else 0.0
            delta_killed_aampl = (float(killed_aampl - killed) / float(killed) * 100.0) if killed > 0 else 0.0
            delta_covered_both = (float(covered_both - covered) / float(covered) * 100.0) if covered > 0 else 0.0
            delta_killed_both = (float(killed_both - killed) / float(killed) * 100.0) if killed > 0 else 0.0

            print "{}{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}&{}\\\\".format(
                ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
                "\\small{"+project+"}",
                "\\small{"+full_qualified_name.split(".")[-1]+"}",
                covered,
                covered_aampl, "{0:.2f}".format(delta_covered_aampl),
                ("{\color{ForestGreen}$\\nearrow$}" if delta_covered_aampl > 0 else "$\\rightarrow$"),
                covered_both, "{0:.2f}".format(delta_covered_both),
                ("{\color{ForestGreen}$\\nearrow$}" if delta_covered_both > 0 else "$\\rightarrow$"),
                killed,
                killed_aampl, "{0:.2f}".format(delta_killed_aampl),
                ("{\color{ForestGreen}$\\nearrow$}" if delta_killed_aampl > 0 else "$\\rightarrow$"),
                killed_both, "{0:.2f}".format(delta_killed_both),
                ("{\color{ForestGreen}$\\nearrow$}" if delta_killed_both > 0 else "$\\rightarrow$"),
            )
            gray = not gray

if __name__ == '__main__':
    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

run(projects=projects)