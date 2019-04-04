# coding=utf-8
import toolbox
import sys
import preparation
import commit_setter


def run(project, indices, amplifiers):
    path_to_project_json = toolbox.prefix_current_dataset + project + ".json"
    project_json = toolbox.get_json_file(path_to_project_json)
    path_to_project_root = toolbox.prefix_dataset + project
    commits = project_json["commits"]

    # for each commits.
    for index in indices:
        commit = commits[index]
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
        toolbox.set_output_log_path(output_path + "/amplification.log")
        path_to_concerned_module = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + "/" + commit["concernedModule"])
        path_to_concerned_module_parent = toolbox.get_absolute_path(
            toolbox.prefix_dataset + project + toolbox.suffix_parent + "/" + commit["concernedModule"])
        path_to_test_that_executes_the_changes = toolbox.get_path_to_csv_file(
            project , commit, commits
        )
        preparation.prepare(project)
        # run now dspot with maven plugin
        cmd = [
            toolbox.maven_home + "mvn",
            "eu.stamp-project:dspot-maven:1.1.1-SNAPSHOT:amplify-unit-tests",
            "-Dpath-to-test-list-csv=" + path_to_test_that_executes_the_changes,
            "-Dverbose=true",
            "-Dtest-criterion=ChangeDetectorSelector",
            "-Doutput-path=" + output_path,
            "-Dpath-to-second-version=" + path_to_concerned_module,
            "-Dgenerate-new-test-class=true",
            "-Dclean=true",
            "-X"
        ]
        if amplifiers:
            cmd.append(
                "-Damplifiers=TestDataMutator,MethodAdd,MethodRemove,MethodGeneratorAmplifier,ReturnValueAmplifier,NullifierAmplifier")
            cmd.append("-Diteration=3")
            cmd.append("-Dbudgetizer=SimpleBudgetizer")
        cmd = preparation.add_needed_options(cmd, project)
        toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_concerned_module_parent)


def create_diff(commit_id, cwd):
    toolbox.delete_if_exists(
        cwd + "/patch.diff"
    )
    cmd = [
        "git", "diff",
        commit_id,
        ">", "patch.diff"
    ]
    toolbox.print_and_call_in_a_file_no_redirection(cmd, cwd=cwd)


if __name__ == '__main__':
    toolbox.init(sys.argv)
    amplifiers = "amplifiers" in sys.argv
    if len(sys.argv) < 2:
        print "usage: python run.py <project> indices..."
    indices = []
    for i in range(0, len(sys.argv)):
        if sys.argv[i].isdigit():
            indices.append(int(sys.argv[i]))
    run(project=sys.argv[1], indices=indices, amplifiers=amplifiers)
