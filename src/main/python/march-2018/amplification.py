import sys
import json

import build_rate_table

blacklist=[
    "com.squareup.javapoet.UtilTest",
    "com.twilio.rest.api.v2010.account.usage.record.AllTimeTest",
    "com.twilio.rest.api.v2010.account.usage.record.DayTimeTest",
    "org.traccar.protocol.At2000ProtocolDecoderTest"
]

def allsame(x):
    return len(set(x)) == 1



def run(project, mvnHome="~/apache-maven-3.3.9/", javaHome="~/jdk1.8.0_121/bin/"):
    prefix_dataset = "dataset/"
    #mvnHome=""
    #javaHome=""
    amplify = javaHome + "java -Xms8G -Xmx16G -jar ../dspot/dspot/target/dspot-1.0.8-SNAPSHOT-jar-with-dependencies.jar \
    --path-to-properties src/main/resources/${project}.properties \
    --iteration 3 \
    --test-criterion PitMutantScoreSelector \
    --verbose \
    --timeOut 50000 \
    --output-path dspot-report \
    --no-minimize \
    --randomSeed 23 " \
    "--amplifiers ReplacementAmplifier:AllLiteralAmplifiers:StatementAdd:MethodAdd:MethodRemove" \
    " --maven-home " + mvnHome if not mvnHome == "" else ""
    opt_test = " --test "
    opt_mutations_original= " --path-pit-result "

    print "#!/usr/bin/env bash"

    print "rm -rf dspot/report/"

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
    properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
    path = prefix_dataset + project + "/" + \
              (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")
    with open("dataset/selected_classes.json") as data_file:
          selected_classes = json.load(data_file)
    types = ["top_1", "top_2", "worst_1", "worst_2"]
    for type in types:
        java_file = selected_classes[project][type]
        if not java_file in blacklist:
            print amplify + opt_test + java_file + opt_mutations_original + prefix_original_mutation_file + java_file + "_mutations.csv"
            print
    print "cp -r dspot-report/* " + "results/march-2018/" + project



if __name__ == '__main__':
    if len(sys.argv) == 1:
        print "usage is : python src/main/python/mutations_analysis.py <project> (withAmplifier)"
    elif len(sys.argv) > 2:
        run(project=sys.argv[1])
    else:
        run(project=sys.argv[1])
