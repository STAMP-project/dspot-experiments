import sys
import json
import count_mutant
import extract_table_profile_2


def run(projects):
    types = ["top_1", "top_2", "worst_1", "worst_2"]
    with open("dataset/selected_classes.json") as data_file:
        classes = json.load(data_file)
    PMS = []
    for project in projects:
        for type in types:
            PMS.append( (extract_table_profile_2.computePMS(classes[project][type], project), classes[project][type],
                       project) )

    for array in PMS:
        if array[0] > 50:
           line(array[-2], array[-1])
    for array in PMS:
        if array[0] <= 50:
            line(array[-2], array[-1])

gray = False
cpt = 1

def line(full_qualified_name, project):
    global gray
    global cpt
    suffix = "_mutations.csv"
    prefix_original = "original/per_class"
    prefix_assert_aampl = "results/per_class_only_aampl"
    prefix_assert = "results/per_class"

    amplified_name = ".".join(full_qualified_name.split(".")[:-1]) + "." + \
                     ("Ampl" + full_qualified_name.split(".")[-1]
                      if full_qualified_name.split(".")[-1].endswith("Test")
                      else  full_qualified_name.split(".")[-1] + "Ampl")

    covered, killed = count_mutant.countForTestClass(
        prefix_original + "/" + project + "/" + full_qualified_name + suffix)
    covered_both, killed_both = count_mutant.countForTestClass(
        prefix_assert + "/" + project + "/" + amplified_name + suffix)
    covered_aampl, killed_aampl = count_mutant.countForTestClass(
        prefix_assert_aampl + "/" + project + "/" + amplified_name + suffix)

    delta_killed_aampl = (float(killed_aampl - killed) / float(killed) * 100.0) if killed > 0 else 0.0
    delta_killed_both = (float(killed_both - killed) / float(killed) * 100.0) if killed > 0 else 0.0

    print "{}{}&{}&{}&{}&{}&{}&{}&{}\\\\".format(
        ("\\rowcolor[HTML]{EFEFEF}" + "\n" if gray else ""),
        cpt,
        #"\\scriptsize{" + full_qualified_name.split(".")[-1].replace("_", "\\_") + "}",
        killed,
        killed_aampl, "{0:.2f}".format(delta_killed_aampl),
        ("{\color{ForestGreen}$\\nearrow$}" if delta_killed_aampl > 0 else "$\\rightarrow$"),
        killed_both, "{0:.2f}".format(delta_killed_both),
        ("{\color{ForestGreen}$\\nearrow$}" if delta_killed_both > 0 else "$\\rightarrow$"),
        )
    gray = not gray
    cpt += 1

if __name__ == '__main__':
    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

run(projects=projects)
