# coding=utf-8
import toolbox
import sys
import preparation
import commit_setter
import random_generator
import run_subset


def run(project, index_begin, index_end, subset_begin, subset_end, iseed):
    path_to_project_json = toolbox.prefix_current_dataset + project + ".json"
    project_json = toolbox.get_json_file(path_to_project_json)
    path_to_project_root = toolbox.prefix_dataset + project
    commits = project_json["commits"]

    # for each commits.
    for commit in commits[index_begin:index_end]:
        output_path = toolbox.get_absolute_path(toolbox.prefix_result + project
                                                + "/" + toolbox.get_output_folder_for_commit(commit, commits))

        output_path = output_path + "/seeds/"

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
            "eu.stamp-project:dspot-maven:2.1.1-SNAPSHOT:amplify-unit-tests",
            "-Dpath-to-test-list-csv=" + run_subset.copy_and_subset(path_to_test_that_executes_the_changes, subset_begin, subset_end),
            "-Dverbose=true",
            "-Dtest-criterion=ChangeDetectorSelector",
            "-Doutput-path=" + output_path,
            "-Dpath-to-second-version=" + path_to_concerned_module,
            "-Dgenerate-new-test-class=true",
            "-Dclean=true",
            "-Dmax-test-amplified=100",
            "-Dworking-directory=true",
            "-X",
            "-Damplifiers=TestDataMutator,MethodAdd,MethodRemove,NullifierAmplifier",
            "-Dbudgetizer=SimpleBudgetizer",
            '-Diteration=1'
        ]
        if not iseed == '':
            seed = random_generator.seeds[int(iseed)]
            output_path_seed = output_path + '/' + seed + '_' + str(subset_begin) + '_' + str(subset_end) +'/'
            toolbox.create(output_path_seed)
            toolbox.set_output_log_path(output_path_seed + "/amplification.log")
            cmd_final = cmd[:]
            cmd_final.append("-Doutput-path=" + output_path_seed)
            cmd_final.append('-DrandomSeed=' + seed)
            cmd_final = preparation.add_needed_options(cmd_final, project, commits.index(commit))
            toolbox.print_and_call_in_a_file(" ".join(cmd_final), cwd=path_to_concerned_module_parent)
        else:
            for seed in random_generator.seeds:
                output_path_seed = output_path + '/' + seed + '_' + str(subset_begin) + '_' + str(subset_end) +'/'
                toolbox.create(output_path_seed)
                toolbox.set_output_log_path(output_path_seed + "/amplification.log")
                cmd_final = cmd[:]
                cmd_final.append("-Doutput-path=" + output_path_seed)
                cmd_final.append('-DrandomSeed=' + seed)
                cmd_final = preparation.add_needed_options(cmd_final, project, commits.index(commit))
                toolbox.print_and_call_in_a_file(" ".join(cmd_final), cwd=path_to_concerned_module_parent)


if __name__ == '__main__':

    toolbox.init(sys.argv)

    if len(sys.argv) < 2:
        print "usage: python run.py <project> <index_start> <index_end>"

    index_begin = toolbox.get_value_of_option(sys.argv, '--begin', '0')
    index_end = toolbox.get_value_of_option(sys.argv, '--end', '-1')
    subset_begin = toolbox.get_value_of_option(sys.argv, '--sbegin', '0')
    subset_end = toolbox.get_value_of_option(sys.argv, '--send', '-1')
    iseed = toolbox.get_value_of_option(sys.argv, '--iseed', '')

    run(project=sys.argv[1], index_begin=int(index_begin), index_end=int(index_end), subset_begin=int(subset_begin), subset_end=int(subset_end), iseed=iseed)
