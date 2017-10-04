import sys
import json

import build_rate_table


def allsame(x):
    return len(set(x)) == 1

def run(projects, mvnHome="~/apache-maven-3.3.9/bin/"):
    prefix_dataset = "dataset/"
    #mvnHome=""
    run_pitest = mvnHome + "mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G "
    report = " -DreportsDirectory=target/pitest-reports"
    target_classes = " -DtargetClasses="
    target_tests = " -DtargetTests="
    with open("dataset/properties_rates.json") as data_file:
        properties_rates = json.load(data_file)
    prefix_properties = "src/main/resources/"
    extension_properties = ".properties"
    for project in projects:
        properties = build_rate_table.load_properties(prefix_properties + project + extension_properties)
        path = prefix_dataset + project + "/" + \
               (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")
        cmd = "cd " + path
        with open("dataset/selected_classes.json") as data_file:
            selected_classes = json.load(data_file)
        path += "target/pitest-reports"
        target_classes_values = properties["filter"]
        types = ["top_1", "top_2", "worst_1", "worst_2"]

        cmds_to_download = []
        print "root_exp=${PWD}"

        for type in types:
            java_file = selected_classes[project][type]
            cmd_pit = run_pitest + report
            cmd_pit += target_classes + target_classes_values + " "
            cmd_pit += target_tests + java_file
            if "additionalClasspathElements" in properties:
                cmd_pit  += " -DadditionalClasspathElements=" + properties["additionalClasspathElements"]
            if "excludedClasses" in properties:
                cmd_pit += " -DexcludedClasses=" + properties["excludedClasses"]
            print cmd
            print cmd_pit
            print "cd ${root_exp}"
            path_to_target = prefix_dataset + project + "/" + \
                             (properties_rates[project]["subModule"] + "/" if not properties_rates[project]["subModule"] == "" else "")
            print "python src/main/python/copy_pit_results.py " + path_to_target + " " + java_file
            cmds_to_download.append("curl --upload-file " + java_file + "_mutations.csv " + "https://transfer.sh/" + java_file + "_mutations.csv")

        for cmd in cmds_to_download:
            print "echo \""+cmd+"\""

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    run(projects=projects)
