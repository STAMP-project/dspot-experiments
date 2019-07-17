from scipy import stats
import toolbox
import os
import random_generator
import numpy as np
import extract_table_seeds

def build_table():
    success_per_seeds = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    nb_test_amplified_seed = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    nb_test_amplified_seed_only_seed = [[] for x in range(len(random_generator.seeds))]
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
                    if extract_table_seeds.get_nb_test_amplified(path_to_seed_result) > 0:
                        nb_test_amplified_seed[s][p][c] = nb_test_amplified_seed[s][p][c] + \
                                                                                 extract_table_seeds.get_nb_test_amplified(path_to_seed_result)
                    if extract_table_seeds.is_success(path_to_seed_result):
                        success_per_seeds[s][p][c] = success_per_seeds[s][p][c] + 1
                        nb_test_amplified_seed_only_seed[s].append(extract_table_seeds.get_nb_test_amplified(path_to_seed_result))
                else:
                    correct_range, correct_step = extract_table_seeds.get_range_and_step(path_to_commit_folder, seed)
                    tmp = 0
                    for x in correct_range:
                        path_to_seed_result = path_to_commit_folder + '/seeds/' + seed + '_' + str(x) + '_' + str(x+correct_step) + '/'
                        if os.path.isdir(path_to_seed_result):
                            nb_test_amplified_seed[s][p][c] = nb_test_amplified_seed[s][p][c] + \
                                                              extract_table_seeds.get_nb_test_amplified(path_to_seed_result)
                            if extract_table_seeds.is_success(path_to_seed_result):
                                tmp = tmp + extract_table_seeds.get_nb_test_amplified(path_to_seed_result)
                                success_per_seeds[s][p][c] = success_per_seeds[s][p][c] + 1
                    if tmp > 0:
                        nb_test_amplified_seed_only_seed[s].append(tmp)

    for s1 in range(len(random_generator.seeds)):
        for s2 in range(len(random_generator.seeds)):
            if not s1 == s2:
                print stats.mannwhitneyu(
                    nb_test_amplified_seed_only_seed[s1],
                    nb_test_amplified_seed_only_seed[s2]
                )

    print stats.kruskal(
        nb_test_amplified_seed_only_seed[0],
        nb_test_amplified_seed_only_seed[1],
        nb_test_amplified_seed_only_seed[2],
        nb_test_amplified_seed_only_seed[3],
        nb_test_amplified_seed_only_seed[4],
        nb_test_amplified_seed_only_seed[5],
        nb_test_amplified_seed_only_seed[6],
        nb_test_amplified_seed_only_seed[7],
        nb_test_amplified_seed_only_seed[8],
        nb_test_amplified_seed_only_seed[9],
    )

    print stats.kruskal(
        success_per_seeds[0],
        success_per_seeds[1],
        success_per_seeds[2],
        success_per_seeds[3],
        success_per_seeds[4],
        success_per_seeds[5],
        success_per_seeds[6],
        success_per_seeds[7],
        success_per_seeds[8],
        success_per_seeds[9],
    )

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

    print stats.f_oneway(
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
