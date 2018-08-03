import toolbox
import sys
import os


def run(project, classes=toolbox.keys_selected_classes):
    path_to_output = toolbox.prefix_current_dataset + project
    selected_classes = toolbox.getTestClassesToBeAmplified(project)
    for current_class in classes:
        if current_class == "onClusty":
            continue
        toolbox.output_log_path = toolbox.getAbsolutePath(
            toolbox.prefix_result + project + "/" + "_amplification.log")
        path_to_original_mutation_score = toolbox.getAbsolutePath(path_to_output +
                                                                  "/" + current_class + "/" +
                                                                  os.listdir(toolbox.getAbsolutePath(
                                                                      path_to_output + "/" + current_class))[
                                                                      0] + "/mutations.csv")
        path_to_output = toolbox.prefix_result + project
        # print(
        cmd = " ".join(
            [toolbox.java_home + "java", "-XX:-UseGCOverheadLimit", "-XX:-OmitStackTraceInFastThrow", "-Xms8G",
             "-Xmx16G",
             "-jar",
             toolbox.path_to_dspot_jar,
             "--path-to-properties",
             toolbox.getAbsolutePath(toolbox.prefix_resources + project + toolbox.suffix_properties),
             "--verbose",
             "--no-minimize",
             "--working-directory",
             "--output-path", toolbox.getAbsolutePath(path_to_output + "/" + current_class + "_amplification/"),
             "--iteration", "3",
             "--amplifiers",
             "StringLiteralAmplifier:NumberLiteralAmplifier:CharLiteralAmplifier:BooleanLiteralAmplifier:MethodAdd:MethodRemove:MethodGeneratorAmplifier:ReturnValueAmplifier:NullifierAmplifier",
             "--test-criterion", "PitMutantScoreSelector",
             "--path-pit-result", path_to_original_mutation_score,
             "--test", selected_classes[current_class],
             "--maven-home", toolbox.maven_home
             ]
        )
        toolbox.printAndCallInAFile(cmd)

if __name__ == '__main__':

    toolbox.init(sys.argv)

    if "all" == sys.argv[1]:
        projects = ["javapoet",
                    "mybatis",
                    "traccar",
                    "stream-lib",
                    "mustache.java",
                    "twilio-java",
                    "jsoup",
                    "protostuff",
                    "logback",
                    "retrofit"
                    ]
        for project in projects:
            run(project)
    else:
        run(sys.argv[1], classes=sys.argv[2:])
