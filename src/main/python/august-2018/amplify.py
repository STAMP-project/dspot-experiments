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
            toolbox.prefix_current_dataset + project + "/" + current_class + "_amplification.log")
        path_to_original_mutation_score = toolbox.getAbsolutePath(path_to_output +
                                                                  "/" + current_class + "/" +
                                                                  os.listdir(toolbox.getAbsolutePath(
                                                                      path_to_output + "/" + current_class))[0] + "/mutations.csv")
        toolbox.printAndCall(
        #print(
            [toolbox.java_home + "java", "-XX:-UseGCOverheadLimit", "-XX:-OmitStackTraceInFastThrow", "-Xms8G",
             "-Xmx16G",
             "-jar",
             toolbox.path_to_dspot_jar,
             "--path-to-properties", "",
             "--verbose",
             "--no-minimize",
             "--working-directory",
             "--maven-home", toolbox.maven_home,
             "--output-path", toolbox.getAbsolutePath(path_to_output + "/amplificaiton_" + current_class),
             "--iteration", "3",
             "--amplifiers",
             "StringLiteralAmplifier:NumberLiteralAmplifier:CharLiteralAmplifier:BooleanLiteralAmplifier:MethodAdd:MethodRemove:MethodGeneratorAmplifier:ReturnValueAmplifier:NullifierAmplifier",
             "--test-criterion", "PitMutantScoreSelector",
             "--path-pit-result", path_to_original_mutation_score,
             "--test", selected_classes[current_class]
             ]
        )



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
