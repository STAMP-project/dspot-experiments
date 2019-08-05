from scipy import stats
import toolbox
import os
import random_generator
import numpy as np
import extract_table_seeds

def build_table():
    success_per_seeds = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    nb_test_amplified_seed = np.zeros((len(random_generator.seeds) + 1, len(toolbox.projects), 10))
    nb_test_amplified_seed_only_seed = [[] for x in range(len(random_generator.seeds) + 1)]
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

            success_per_seeds[-1][p][c] = success_ref
            nb_test_amplified_seed[-1] = number_test_ref
            nb_test_amplified_seed_only_seed[-1].append(number_test_ref)

            for s in range(len(random_generator.seeds)):
                seed = random_generator.seeds[s]
                path_to_seed_result = path_to_commit_folder + '/seeds/' + seed + '/'
                if os.path.isdir(path_to_seed_result):
                    if extract_table_seeds.get_nb_test_amplified(path_to_seed_result) > 0:
                        nb_test_amplified_seed[s][p][c] = nb_test_amplified_seed[s][p][c] + \
                                                                                 extract_table_seeds.get_nb_test_amplified(path_to_seed_result)
                    if extract_table_seeds.is_success(path_to_seed_result):
                        success_per_seeds[s][p][c] = 1
                        nb_test_amplified_seed_only_seed[s].append(extract_table_seeds.get_nb_test_amplified(path_to_seed_result))
                    else:
                        nb_test_amplified_seed_only_seed[s].append(0)
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
                                success_per_seeds[s][p][c] = 1
                    if tmp > 0:
                        nb_test_amplified_seed_only_seed[s].append(tmp)
                    else:
                        nb_test_amplified_seed_only_seed[s].append(0)

    nb_success_per_seed = []
    for success in success_per_seeds:
        nb_success_per_seed.append(sum(success.flatten()))

    print mean_confidence_interval(nb_success_per_seed)

def mean_confidence_interval(data, confidence=0.95):
    a = 1.0 * np.array(data)
    n = len(a)
    m, se = np.mean(a), stats.sem(a)
    h = se * stats.t.ppf((1 + confidence) / 2., n-1)
    return format(m), format(m+h), format(m-h), format(h)

def format(x):
    return  "{0:.2f}".format(x)

def mean(table):
    return sum(table) / len(table)

if __name__ == '__main__':
    build_table()
