import sys
import json

import build_rate_table


def allsame(x):
    return len(set(x)) == 1


def run(projects, mvnHome="~/apache-maven-3.3.9/", javaHome="~/jdk1.8.0_121/bin/"):
    prefix_dataset = "dataset/"
    #mvnHome=""
    #javaHome=""
    amplify = javaHome + "java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar \
    --path-to-properties src/main/resources/${project}.properties \
    --iteration 3 \
    --amplifiers TestDataMutator:StatementAdd \
    --test-criterion PitMutantScoreSelector \
    --verbose \
    --output-path dspot-report \
    --randomSeed 23  "
    amplify += " --maven-home " + mvnHome if not mvnHome == "" else ""
    opt_test = " --test "
    opt_mutations_original= " --path-pit-result "
    for project in projects:

        print "#!/usr/bin/env bash"

        print "project=" + project
        print "root_exp=${PWD}"

        print "python src/main/python/october-2017/install.py ${project}"
        print "cd dataset/${project}/"
        print mvnHome + "bin/mvn install -DskipTests"
        print "cd ${root_exp}"

        prefix_original_mutation_file = "original/october-2017/" + project + "/"

        with open("dataset/properties_rates.json") as data_file:
            properties_rates = json.load(data_file)
        prefix_properties = "src/main/resources/"
        extension_properties = ".properties"
        for project in projects:
            properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
            path = prefix_dataset + project + "/" + \
                   (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")
            with open("dataset/selected_classes.json") as data_file:
                selected_classes = json.load(data_file)
            types = ["top_1", "top_2", "worst_1", "worst_2"]
            for type in types:
                java_file = selected_classes[project][type]
                print amplify + opt_test + java_file + opt_mutations_original + prefix_original_mutation_file + java_file + "_mutations.csv"
            print "zip -r dspot-report.zip dspot-report"
            print "to_download=$(curl --upload-file dspot-report.zip " + "https://transfer.sh/" + project + "_dspot-report.zip)"
            print "echo \"curl ${to_download} -o " + project + "_dspot-report.zip\""


if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    run(projects=projects)
