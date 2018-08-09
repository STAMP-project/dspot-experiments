import toolbox
import sys

def run(project):
    path_to_output = toolbox.prefix_result + project
    selected_classes = toolbox.get_test_classes_to_be_amplified(project)
    properties = toolbox.get_properties(project)
    filter = toolbox.load_properties(project)[toolbox.key_filter]
    module = properties[toolbox.key_subModule]

    # run mutations score all original test classes
    toolbox.set_output_log_path(toolbox.get_absolute_path(toolbox.prefix_result + project + "/all_original_mutations.log"))
    path_to_output_to_mutations_score = toolbox.get_absolute_path(path_to_output + "/all_original")
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
         "-DtargetClasses=" + filter
         ], cwd=toolbox.prefix_dataset + project + module
    )

    # copy all the amplified test classes and re-run mutation score analysis
    toolbox.set_output_log_path("trash.log")
    for current_class in toolbox.keys_selected_classes:
        if current_class == "onClusty":
            continue
        test_class_name = selected_classes[current_class]
        toolbox.copy_amplified_test_class(current_class, test_class_name, project)
    # run mutations score all original test classes
    toolbox.set_output_log_path(toolbox.get_absolute_path(toolbox.prefix_result + project + "/all_amplified_mutations.log"))
    path_to_output_to_mutations_score = toolbox.get_absolute_path(path_to_output + "/all_amplified")
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
         "-DtargetClasses=" + filter
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
    else:
        run(sys.argv[1])