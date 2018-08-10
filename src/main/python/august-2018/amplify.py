import toolbox
import sys
import os


def run(project, classes=toolbox.keys_selected_classes):
    path_to_dataset = toolbox.prefix_current_dataset + project
    selected_classes = toolbox.get_test_classes_to_be_amplified(project)
    path_to_output = toolbox.prefix_result + project
    for current_class in classes:
        if current_class == "onClusty":
            continue
        toolbox.set_output_log_path(toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" + current_class + "_amplification.log"))
        path_to_original_mutation_score = toolbox.get_absolute_path(
            path_to_dataset + "/" + current_class + "/" +
            os.listdir(toolbox.get_absolute_path(path_to_dataset + "/" + current_class))[-1] + "/mutations.csv"
        )
        # print(
        path_to_output_for_class = toolbox.get_absolute_path(path_to_output + "/" + current_class + "_amplification/")
        toolbox.delete_if_exists(path_to_output_for_class)
        cmd = " ".join(
            [toolbox.java_home + "java", "-XX:-UseGCOverheadLimit", "-XX:-OmitStackTraceInFastThrow", "-Xms8G",
             "-Xmx16G",
             "-jar",
             toolbox.path_to_dspot_jar,
             "--path-to-properties",
             toolbox.get_absolute_path(toolbox.prefix_resources + project + toolbox.suffix_properties),
             "--verbose",
             "--no-minimize",
             "--working-directory",
             "--output-path", path_to_output_for_class,
             "--iteration", "3",
	         "--budgetizer", "NoBudgetizer",
             "--amplifiers",
             "StringLiteralAmplifier:NumberLiteralAmplifier:CharLiteralAmplifier:BooleanLiteralAmplifier:MethodAdd:MethodRemove:MethodGeneratorAmplifier:ReturnValueAmplifier:NullifierAmplifier",
             "--test-criterion", "PitMutantScoreSelector",
             "--path-pit-result", path_to_original_mutation_score,
             "--test", selected_classes[current_class],
             "--maven-home", toolbox.maven_home
             ]
        ) + ("" if not selected_classes[current_class] in toolbox.no_amplified_name_classes else "--generate-new-test-class")
        toolbox.print_and_call_in_a_file(cmd)


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
    elif len(sys.argv) > 3:
        run(sys.argv[1], classes=sys.argv[2:])
    else:
        run(sys.argv[1])
