import toolbox
import sys

def run(project, classes=toolbox.keys_selected_classes):
    path_to_output = toolbox.prefix_current_dataset + project
    selected_classes = toolbox.getTestClassesToBeAmplified(project)
    properties = toolbox.getProperties(project)
    filter = toolbox.load_properties(project)[toolbox.key_filter]
    module = properties[toolbox.key_subModule]
    for current_class in classes:
        if current_class == "onClusty":
            continue
        toolbox.output_log_path = toolbox.getAbsolutePath(toolbox.prefix_current_dataset + project + "/" + current_class + "_original_mutations.log")
        toolbox.printAndCall(
            # print (
            [toolbox.maven_home + "mvn", "clean", "test", "-DskipTests",
             "org.pitest:pitest-maven:1.4.0:mutationCoverage",
             "-DoutputFormats=CSV",
             "-DtimeoutConst=10000",
             "-DjvmArgs=16G",
             "-DmutationEngines=gregor",
             "-Dmutators=ALL",
             "-DreportsDirectory=" + toolbox.getAbsolutePath(path_to_output + "/" + current_class),
             "-DtargetClasses=" + filter,
             "-DtargetTests=" + selected_classes[current_class],
             "-DexcludedTestClasses=" + properties[toolbox.key_excludedClasses]
             ], cwd=toolbox.prefix_dataset + project + "/" + module
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
