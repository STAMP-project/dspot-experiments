import toolbox
import sys

def run(project, classes=toolbox.keys_selected_classes):
    path_to_output = toolbox.prefix_result + project
    selected_classes = toolbox.get_test_classes_to_be_amplified(project)
    properties_rate = toolbox.get_properties(project)
    properties = toolbox.load_properties(project)
    filter = toolbox.load_properties(project)[toolbox.key_filter]
    module = properties_rate[toolbox.key_subModule]
    for current_class in classes:
        if current_class == "onClusty":
            continue
        test_class_name = selected_classes[current_class]
        toolbox.copy_amplified_test_class(current_class, test_class_name, project)
        toolbox.set_output_log_path(toolbox.get_absolute_path(toolbox.prefix_result + project + "/" + current_class + "_amplified_mutations.log"))
        path_to_output_to_mutations_score = toolbox.get_absolute_path(path_to_output + "/" + current_class + "_amplification")
        #toolbox.delete_if_exists(path_to_output_to_mutations_score)
        toolbox.print_and_call(
            # print (
            [toolbox.maven_home + "mvn", "clean", "test", "-DskipTests",
             "org.pitest:pitest-maven:1.4.0:mutationCoverage",
             "-DoutputFormats=CSV",
             "-DtimeoutConst=10000",
             "-DjvmArgs=16G",
             "-DmutationEngines=gregor",
             "-Dmutators=ALL",
             "-DreportsDirectory=" + path_to_output_to_mutations_score,
             "-DtargetClasses=" + filter,
             "-DtargetTests=" + selected_classes[current_class]+ "," + toolbox.get_amplified_name(test_class_name),
             "-DexcludedTestClasses=" + properties[toolbox.key_excludedClasses],
             "-DadditionalClasspathElements=" + properties[toolbox.key_additional_classpath_elements]
             ], cwd=toolbox.prefix_dataset + project + module
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
    elif len(sys.argv) > 3:
        run(sys.argv[1], classes=sys.argv[2:])
    else:
        run(sys.argv[1])