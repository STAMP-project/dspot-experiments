import subprocess
import json
import sys
import toolbox


def install(project):
    toolbox.set_output_log_path(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project + "/install.log"))

    with open(toolbox.prefix_current_dataset + 'dataset.json') as data_file:
        data = json.load(data_file)

    toolbox.print_and_call(
        ["git", "clone", data[project]["url"], toolbox.prefix_dataset + project]
    )

    toolbox.print_and_call(
        ["git", "reset", "--hard", data[project]["commitid"]], cwd=toolbox.prefix_dataset + project
    )

if __name__ == '__main__':

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
            install(project)
    else:
        install(sys.argv[1])
