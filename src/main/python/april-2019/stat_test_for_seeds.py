from scipy import stats
import toolbox
import os
import random_generator
import numpy as np
import extract_table_seeds

def build_table():
    success_per_seeds = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    nb_test_amplified_seed = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    for p in range(len(toolbox.projects)):
        project = toolbox.projects[p]
        project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
        commits = project_json["commits"]
        for c in range(10):
            commit_json = commits[c]
            path_to_commit_folder = toolbox.get_absolute_path(
                toolbox.prefix_result + project + '/' + toolbox.get_output_folder_for_commit(commit_json, commits)
            ) + '/'

            path_to_ref_result = path_to_commit_folder + '/input_amplification/1/'
            success_ref = 0
            number_test_ref = 0
            if os.path.isdir(path_to_ref_result):
                if extract_table_seeds.is_success(path_to_ref_result):
                    success_ref = 1
                    number_test_ref = extract_table_seeds.get_nb_test_amplified(path_to_ref_result)

            success_per_seeds[-1] = success_ref
            nb_test_amplified_seed[-1] = number_test_ref

            for s in range(len(random_generator.seeds)):
                seed = random_generator.seeds[s]
                path_to_seed_result = path_to_commit_folder + '/seeds/' + seed + '/'
                if os.path.isdir(path_to_seed_result):
                    nb_test_amplified_seed[s][p][c] = nb_test_amplified_seed[s][p][c] + \
                                                                                 extract_table_seeds.get_nb_test_amplified(path_to_seed_result)
                    if extract_table_seeds.is_success(path_to_seed_result):
                        success_per_seeds[s][p][c] = success_per_seeds[s][p][c] + 1

    print stats.kruskal(
        nb_test_amplified_seed[0].flatten(),
        nb_test_amplified_seed[1].flatten(),
        nb_test_amplified_seed[2].flatten(),
        nb_test_amplified_seed[3].flatten(),
        nb_test_amplified_seed[4].flatten(),
        nb_test_amplified_seed[5].flatten(),
        nb_test_amplified_seed[6].flatten(),
        nb_test_amplified_seed[7].flatten(),
        nb_test_amplified_seed[8].flatten(),
        nb_test_amplified_seed[9].flatten()
    )

if __name__ == '__main__':
    build_table()
