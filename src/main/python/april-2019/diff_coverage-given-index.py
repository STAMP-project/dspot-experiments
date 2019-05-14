import sys
import toolbox
import commit_setter
import preparation
import shutil


def diff_coverage(project, index):
    project_json = toolbox.get_json_file(toolbox.get_absolute_path(
        toolbox.prefix_current_dataset + project
    ))
    commits = project_json["commits"]
    commit_json = project_json["commits"][int(index)]
    concerned_module = commit_json["concernedModule"]
    commit_setter.set_commit(
        toolbox.get_absolute_path(toolbox.prefix_current_dataset + project), project, int(index)
    )
    path_to_concerned_module_parent = toolbox.get_absolute_path(
        toolbox.prefix_current_dataset + project + "_parent/" + concerned_module
    )
    path_to_concerned_module = toolbox.get_absolute_path(
        toolbox.prefix_current_dataset + project + "/" + concerned_module
    )
    if not concerned_module == "":
        toolbox.set_output_log_path("trash.log")  # we don't care about the log of the installation
        path_to_project_root = toolbox.get_absolute_path(
            toolbox.prefix_current_dataset + project + "/"
        )
        cmd = [toolbox.maven_home + "mvn",
               "clean",
               "install",
               "-DskipTests",
               "-Dmaven.compiler.source=1.8",
               "-Dmaven.compiler.target=1.8",
               "-Dmaven.compile.source=1.8",
               "-Dmaven.compile.target=1.8",
               "-Dcheckstyle.skip=true",
               "-Denforcer.skip=true",
               "-Dxwiki.clirr.skip=true"
               ]
        toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_project_root)

    toolbox.create(toolbox.prefix_result + project )
    toolbox.create(toolbox.prefix_result + project + "/" + toolbox.get_output_folder_for_commit(commit_json, commits))

    toolbox.create_diff(
        commit_json["sha"],
        path_to_concerned_module_parent,
    )
    toolbox.create_diff(
        commit_json["parent"],
        path_to_concerned_module,
    )
    shutil.copy(
        toolbox.get_absolute_path(path_to_concerned_module + "/patch.diff"),
        toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" +
            toolbox.get_output_folder_for_commit(commit_json, commits) + "/commit_coverage_patch.diff"
        )
    )
    shutil.copy(
        toolbox.get_absolute_path(path_to_concerned_module_parent + "/patch.diff"),
        toolbox.get_absolute_path(
            toolbox.prefix_result + project + "/" +
            toolbox.get_output_folder_for_commit(commit_json, commits) + "/parent_coverage_patch.diff"
        )
    )

    preparation.prepare(project)

    compute_diff_coverage_for_given_commit(path_to_concerned_module_parent,
                                           path_to_concerned_module,
                                           commit_json,
                                           commits,
                                           project,
                                           "parent_coverage"
                                           )

    compute_diff_coverage_for_given_commit(path_to_concerned_module,
                                           path_to_concerned_module_parent,
                                           commit_json,
                                           commits,
                                           project,
                                           "commit_coverage"
                                           )


def compute_diff_coverage_for_given_commit(path_to_concerned_module,
                                           path_to_concerned_module_other,
                                           commit_json,
                                           commits,
                                           project,
                                           prefix_result):
    output_path_dir = toolbox.get_absolute_path(
        toolbox.prefix_result + project + "/" + toolbox.get_output_folder_for_commit(commit_json, commits)
    )

    toolbox.set_output_log_path(output_path_dir +
                                "/" + prefix_result + "_" + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + ".log")
    concerned_module = commit_json["concernedModule"]

    cmd = [
        toolbox.maven_home + "mvn", "clean",
        "eu.stamp-project:dspot-diff-test-selection:2.1.1-SNAPSHOT:list",
        "-Dpath-dir-second-version=" + path_to_concerned_module_other,
        "-Dmodule=" + concerned_module,
        "-Doutput-path=" + output_path_dir +
                "/" + prefix_result + "_" + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + ".csv",
        "-X",
        "-Dxwiki.clover.skip=false"
    ]
    toolbox.print_and_call_in_a_file(" ".join(cmd), cwd=path_to_concerned_module)


if __name__ == '__main__':
    toolbox.init(sys.argv)
    for i in range(2, len(sys.argv)):
        diff_coverage(sys.argv[1], sys.argv[i])
