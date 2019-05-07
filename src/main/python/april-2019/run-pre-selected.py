# coding=utf-8
import toolbox
import sys
import preparation
import commit_setter


def run(project, index_begin, index_end, amplifiers, iteration):
    path_to_project_json = toolbox.prefix_current_dataset + project + ".json"
    project_json = toolbox.get_json_file(path_to_project_json)
    path_to_project_root = toolbox.prefix_dataset + project
    commits = project_json["commits"]

    # for each commits.
    for commit in commits[index_begin:index_end]:
        output_path = toolbox.get_absolute_path(toolbox.prefix_result + project
                                                + "/" + toolbox.get_output_folder_for_commit(commit, commits))
        if amplifiers:
            output_path = output_path + "/input_amplification"
        else:
            output_path = output_path + "/assert_amplification"

        toolbox.create(output_path)
        toolbox.set_output_log_path(output_path + "/commit_set.log")
        #  1) set up both version of the program
        commit_setter.set_commit(path_to_project_root, project, commits.index(commit))

        path_to_concerned_module = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + "/" + commit["concernedModule"])
        path_to_concerned_module_parent = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + toolbox.suffix_parent + "/" + commit["concernedModule"])

        path_to_test_that_executes_the_changes = toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" + toolbox.get_output_folder_for_commit(commit, commits) +
            "/parent_coverage_" + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + ".csv"
        )

        preparation.prepare(project)
        # run now dspot with maven plugin
        cmd = [
            toolbox.maven_home + "mvn",
            "eu.stamp-project:dspot-maven:2.0.1-SNAPSHOT:amplify-unit-tests",
            "-Dpath-to-test-list-csv=" + path_to_test_that_executes_the_changes,
            "-Dverbose=true",
            "-Dtest-criterion=ChangeDetectorSelector",
            "-Doutput-path=" + output_path,
            "-Dpath-to-second-version=" + path_to_concerned_module,
            "-Dgenerate-new-test-class=true",
            "-Dclean=true",
            "-Dworking-directory=true"
            "-X"
        ]

        if amplifiers:
            cmd.append(
                "-Damplifiers=TestDataMutator,MethodAdd,MethodRemove,NullifierAmplifier")
            cmd.append("-Dbudgetizer=SimpleBudgetizer")
            for current_iteration in iteration:
                output_path_iteration = output_path + '/' + current_iteration
                toolbox.create(output_path_iteration)
                toolbox.set_output_log_path(output_path_iteration + "/amplification.log")
                cmd_final = cmd[:]
                cmd_final.append("-Doutput-path=" + output_path_iteration)
                cmd_final.append('-Diteration=' + current_iteration)
                cmd_final = preparation.add_needed_options(cmd_final, project, commits.index(commit))
                toolbox.print_and_call_in_a_file(" ".join(cmd_final), cwd=path_to_concerned_module_parent)
        else:
            toolbox.set_output_log_path(output_path + "/amplification.log")
            cmd = preparation.add_needed_options(cmd, project, commits.index(commit))
            toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_concerned_module_parent)

if __name__ == '__main__':

    toolbox.init(sys.argv)
    amplifiers = "amplifiers" in sys.argv

    if len(sys.argv) < 2:
        print "usage: python run.py <project> <index_start> <index_end>"

    iteration = toolbox.get_value_of_option(sys.argv, '--iteration', ['1', '2', '3'])
    index_begin = toolbox.get_value_of_option(sys.argv, '--begin', 0)
    index_end= toolbox.get_value_of_option(sys.argv, '--end', -1)

    run(project=sys.argv[1], index_begin=index_begin, index_end=index_end, amplifiers=amplifiers, iteration=iteration)
