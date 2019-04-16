import sys
import toolbox
import commit_setter
import test_suite_switcher
import os
import json
import junit_enforcer

suffix_target_surefire_reports = "/target/surefire-reports/"

def run(project, index_commit):
    project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
    beg = 0 if index_commit == -1 else index_commit
    end = index_commit if index_commit == -1 else index_commit + 1
    output_path_to_json = toolbox.get_absolute_path(toolbox.prefix_result + project + "/behavioral-encoding-check.json")
    if os.path.isfile(output_path_to_json):
        data = toolbox.get_json_file(toolbox.get_absolute_path(output_path_to_json))
    else:
        data = {project: {}}
    data_project = data[project]
    commits = project_json["commits"]
    for commit in commits:
        if not commits.index(commit) in data_project:
            commit_setter.set_commit(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project), project,
                                     commits.index(commit))
            prefix_folder_result = toolbox.get_absolute_path(
                toolbox.prefix_result + project + "/" + toolbox.get_output_folder_for_commit(commit, commits) + "/")
            key_commit = str(commits.index(commit)) + "_" + str(commit["sha"][0:7])
            for configuration in ["assert_amplification", "input_amplification"]:
                value = run_for_configuration(configuration, prefix_folder_result, commit)
                if not value is None:
                    if not key_commit in data_project:
                        data_project[key_commit] = {}
                    data_project[key_commit][configuration] = value
    with open(output_path_to_json, 'w') as outfile:
        json.dump(data, outfile)

def run_for_configuration(configuration, prefix_folder_result, commit):
    folder_result = prefix_folder_result + "/" + configuration + "/"
    if os.path.isdir(folder_result):
        for file in os.listdir(folder_result):
            if os.path.isdir(folder_result + file):
                toolbox.set_output_log_path(
                    toolbox.get_absolute_path(folder_result + "behavioral_encoding_check.log")
                )
                path_to_concerned_module = toolbox.get_absolute_path(
                    toolbox.prefix_current_dataset + project + "/" + commit["concernedModule"])
                print path_to_concerned_module
                path_concerned_pom = path_to_concerned_module + "/pom.xml"
                test_suite_switcher.switch(folder_result, path_concerned_pom)
                junit_enforcer.enforce(path_concerned_pom)
                toolbox.print_and_call(
                    ["mvn", "clean", "install", "-DskipTests", "--quiet", "-Dcheckstyle.skip=true",
                     "-Denforcer.skip=true", "-Dxwiki.clirr.skip=true"], cwd=toolbox.get_absolute_path(
                        toolbox.prefix_current_dataset + project + "/"))
                toolbox.print_and_call(["mvn", "clean", "test"], cwd=path_to_concerned_module)
                path_to_surefire_report = path_to_concerned_module + suffix_target_surefire_reports
                for report in os.listdir(path_to_surefire_report):
                    if report.startswith("TEST-") and report.endswith(".xml"):
                        return containsFailureOrError(path_to_surefire_report + "/" + report)
    return None

def containsFailureOrError(report):
    with open(report) as data_file:
        for line in data_file:
            if "<failure message=" in line or "<failure message=" in line :
                return True
    return False

if __name__ == '__main__':
    project = sys.argv[1]
    index_commit = int(sys.argv[2])  # -1 for all
    run(project, index_commit)
