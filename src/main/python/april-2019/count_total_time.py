import toolbox
import os
import extract_table

def run():
    total_time = 0
    for project in toolbox.projects:
        project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
        commits = project_json["commits"]
        modes = ["assert_amplification", "input_amplification/1", "input_amplification/2", "input_amplification/3"]
        for commit_json in commits[0:10]:
            path_to_commit_folder = toolbox.get_absolute_path(
                toolbox.prefix_result + project + '/' + toolbox.get_output_folder_for_commit(commit_json, commits)
            ) + '/'
            for mode in modes:
                path_to_mode_result = path_to_commit_folder + '/' + mode + '/'
                if os.path.isdir(path_to_mode_result):
                    if not commit_json['concernedModule'] == "":
                        total_time = total_time + extract_table.get_time(path_to_mode_result, commit_json['concernedModule'].split('/')[-2])
                    else:
                        total_time = total_time + extract_table.get_time(path_to_mode_result, project + toolbox.suffix_parent)

    return total_time

if __name__ == '__main__':
    print extract_table.convert_time(run())
