import subprocess
import sys
import os.path

import install
import profiling_test_class

def run(projects):

    cmd = "~/jdk1.8.0_121/jre/bin/java -Xms16G -Xmx32G" \
          " -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar" \
          " --path-to-properties src/main/resources/{}.properties" \
          " --amplifiers MethodAdd:TestDataMutator:StatementAdderOnAssert" \
          " --iteration 3" \
          " --output-path {}" \
          " --maven-home /home/spirals/danglot/apache-maven-3.3.9/" \
          " -t {}" \
          " -m original/per_class/{}/{}_mutations.csv"

    results_path = "results/per_class/"
    already_run = []
    for project in projects:
        #install.install(project)
        top, worst = profiling_test_class.profile(projects=[project])
        for e in top + worst:
            test_name = e[2].split(".")[-1]
            mutations_csv_file = ".".join(e[2].split(".")[:-1]) + "." + test_name + "_mutants_killed.json"
            print mutations_csv_file
            path_to_mutation_csv_file = results_path + project + "/" + mutations_csv_file
            if not os.path.isfile(path_to_mutation_csv_file):
                print cmd.format(project, project, e[2], project, e[2])
                #subprocess.call(cmd.format(project, project, e[2], project, e[2]), shell=True)
            else:
                already_run.append(e[2])
    return already_run

    already_run = run(projects)
    print "=" * 30
    print "=" * 30
    for a in already_run:
        print a

