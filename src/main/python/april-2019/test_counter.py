import toolbox
import sys
import commit_setter
import os
import json

def run(project):
    path_to_root_project = toolbox.get_absolute_path(toolbox.prefix_current_dataset + project + "/")
    project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
    commits = project_json['commits']
    for commit in commits:
        commit_setter.set_commit(
            path_to_root_project,
            project,
            commits.index(commit)
        )
        toolbox.print_and_call_no_redirection([
            toolbox.maven_home + "mvn",
            "clean", "test"
        ], cwd=path_to_root_project)
        commit['nb_test_per_commit'] = count_tests(path_to_root_project)
    with open(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project) + '.json', 'w') as outfile:
        json.dump(project_json, outfile)


def count_tests(path_to_root_project):
    nb_test = 0
    for root, dirs, files in os.walk(path_to_root_project):
        for file in files:
            if file.endswith('.txt'):
                with open(root + "/" + file, 'r') as test_report:
                    for line in test_report:
                        if line.startswith('Tests run:'):
                            nb_test = nb_test + int(line.split(',')[0].split(' ')[-1])
    return nb_test

if __name__ == '__main__':
    toolbox.init(sys.argv)
    build = 'build' in sys.argv
    for project in toolbox.projects:
        run(project)
