# coding=utf-8
import os
import toolbox
import sys
import preparation
import commit_setter


def run(project, index_begin, index_end, amplifiers):
    path_to_project_json = toolbox.prefix_current_dataset + project + ".json"
    project_json = toolbox.get_json_file(path_to_project_json)
    path_to_project_root = toolbox.prefix_dataset + project
    commits = project_json["commits"]

    # for each commits.
    for commit in commits[index_begin:index_end]:
        log_path = toolbox.get_absolute_path(toolbox.prefix_result + project + "/commits_" + str(commits.index(commit)))
        if amplifiers:
            log_path = log_path + "_amplifiers"
        log_path = log_path + ".log"

        toolbox.set_output_log_path(log_path)
        #  1) set up both version of the program
        commit_setter.set_commit(path_to_project_root, project, commits.index(commit))

        path_to_concerned_module = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + "/" + commit["concernedModule"])
        path_to_concerned_module_parent = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + toolbox.suffix_parent + "/" + commit["concernedModule"])
        create_diff(commit["parent"], path_to_concerned_module)

        path_to_test_that_executes_the_changes = toolbox.get_path_to_csv_file(project, str(commits.index(commit)))
        preparation.prepare(project)

        # if the list does not exists, then compute it
        if not os.path.isfile(path_to_test_that_executes_the_changes):
            # must build all the project, i.e. installing the version
            cmd = [toolbox.maven_home + "mvn", "clean", "install", "-DskipTests"]
            toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_project_root)

            get_list_of_tests_that_execute_changes(commit["concernedModule"],
                                                   path_to_concerned_module,
                                                   path_to_concerned_module_parent,
                                                   path_to_test_that_executes_the_changes)

        output_path = toolbox.get_absolute_path(toolbox.prefix_result + project + "/commit_" + str(commits.index(commit)))
        if amplifiers:
            output_path = output_path + "_amplifiers"

        # run now dspot with maven plugin
        cmd = [
            toolbox.maven_home + "mvn",
            "eu.stamp-project:dspot-maven:1.1.1-SNAPSHOT:amplify-unit-tests",
            "-Dpath-to-test-list-csv=" + path_to_test_that_executes_the_changes,
            "-Dverbose=True",
            "-Dtest-criterion=ChangeDetectorSelector",
            "-Doutput-path=" + output_path,
            "-Dpath-to-second-version=" + path_to_concerned_module_parent,
            "-Dgenerate-new-test-class=true",
            "-Dclean=true"
        ]

        if amplifiers:
            cmd.append("-Damplifiers=AllLiteralAmplifiers,MethodAdd,MethodRemove,MethodGeneratorAmplifier,ReturnValueAmplifier,NullifierAmplifier")
            cmd.append("-Diteration=3")
            cmd.append("-Dbudgetizer=SimpleBudgetizer")

        cmd = preparation.add_needed_options(cmd, project)
        toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_concerned_module)

def get_list_of_tests_that_execute_changes(concerned_module, path_to_concerned_module, path_to_concerned_module_parent,
                                           output_path):
    cmd = [
        toolbox.maven_home + "mvn", "clean",
        "eu.stamp-project:diff-test-selection:0.5-SNAPSHOT:list",
        "-DpathToDiff=patch.diff",
        "-DpathToOtherVersion=" + path_to_concerned_module_parent,
        "-Dmodule=" + concerned_module,
        "-DoutputPath=" + output_path
    ]
    return_code = toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_concerned_module)
    return return_code


def create_diff(commit_id, cwd):
    toolbox.delete_if_exists(
        cwd + "/patch.diff"
    )
    cmd = [
        "git", "diff",
        commit_id,
        ">", toolbox.get_absolute_path(cwd + "/patch.diff")
    ]
    toolbox.print_and_call_in_a_file_no_redirection(cmd, cwd=cwd)


if __name__ == '__main__':

    toolbox.init(sys.argv)

    amplifiers = "amplifiers" in sys.argv

    if len(sys.argv) < 2:
        print "usage: python run.py <project> <index_start> <index_end>"

    index_begin = 0
    index_end = -1
    if len(sys.argv) > 2:
        index_begin = int(sys.argv[2])
        index_end = int(sys.argv[3])

    run(sys.argv[1], index_begin=index_begin, index_end=index_end, amplifiers=amplifiers)
