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



def run(project, mvnHome="~/apache-maven-3.3.9/", javaHome="~/jdk1.8.0_121/bin/", withAmplifier="withAmplifier", JBSE=False, selector="mutant", againstAampl=True):
    prefix_dataset = "dataset/"
    #mvnHome=""
    #javaHome=""
    amplify = javaHome + "java -Xms8G -Xmx16G -cp \
    ../Ex2Amplifier/target/exhaustive-explorer-amplifier-1.0.0.jar:../Ex2Amplifier/dspot/dspot/target/dspot-1.0.6-SNAPSHOT-jar-with-dependencies.jar \
    fr.inria.stamp.Main \
    --path-to-properties src/main/resources/${project}.properties \
    --iteration 3 " + \
    "--test-criterion PitMutantScoreSelector " if selector == "mutant" else "--test-criterion JacocoCoverageSelector " + \
    "--verbose \
    --output-path dspot-report \
    --randomSeed 23  "
    amplify += "--amplifiers"
    if withAmplifier == "withAmplifier":
        amplify += " Ex2Amplifier"
    elif withAmplifier == "withAmplifiers":
        amplify += " Ex2Amplifier:StatementAdd"
    else:
        amplify += " None"
    amplify += " --jbse" if JBSE else ""
    amplify += " --maven-home " + mvnHome if not mvnHome == "" else ""
    opt_test = " --test "
    opt_mutations_original= " --path-pit-result "

    print "#!/usr/bin/env bash"

    print "project=" + project
    print "root_exp=${PWD}"

    print "python src/main/python/october-2017/install.py ${project}"
    print "cd dataset/${project}/"
    print mvnHome + "bin/mvn install -DskipTests"
    print "cd ${root_exp}"

    prefix_aampl_mutations_file = "results/february-2018/" + project + "_aampl/" if againstAampl else "original/october-2017/" + project + "/"

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
            ampl_java_file = '.'.join(java_file.split(".")[:-1]) + "." + build_rate_table.buildAmplTest(java_file.split(".")[-1])
            mutation_path_file = (java_file if not againstAampl else ampl_java_file) + "_mutations.csv"
            print amplify + opt_test + java_file + opt_mutations_original + prefix_aampl_mutations_file + mutation_path_file
            print
    #print "zip -r dspot-report.zip dspot-report"
    #print "to_download=$(curl --upload-file dspot-report.zip " + "https://transfer.sh/" + project + "_dspot-report.zip)"
    #print "echo \"curl ${to_download} -o " + project + "_dspot-report.zip\""
    output_dir = "results/february-2018/" + project
    if withAmplifier == "withAmplifiers":
        output_dir += "Ex2amplifier_StatementAdd"
    elif withAmplifier == "None":
        output_dir += "_aampl"

    print "cp -r dspot-report/* " + output_dir + "/"

if __name__ == '__main__':
    withAmplifier = "withAmplifier"
    mode = "CATG"
    selector = "mutation"
    againstAampl = False
    for i in range(0, len(sys.argv)):
        if sys.argv[i] == "withAmplifiers":
            withAmplifier = "withAmplifiers"
        elif sys.argv[i] == "JBSE":
            mode = "JBSE"
        elif sys.argv[i] == "coverage":
            selector = "coverage"
        elif sys.argv[i] == "aampl":
            againstAampl = True


    if len(sys.argv) == 1:
        print "usage is : python src/main/python/mutations_analysis.py <project> (withAmplifier)"
    elif len(sys.argv) > 2:
        run(project=sys.argv[1], withAmplifier=withAmplifier, JBSE=mode == "JBSE", selector=selector, againstAampl=againstAampl)
    else:
        run(project=sys.argv[1])
