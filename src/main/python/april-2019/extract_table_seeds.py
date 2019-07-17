import sys
import toolbox
import os
import diff_size
import random_generator

def build_table(projects):
    print_header()
    gray = False
    total_time = 0
    for project in projects:
        project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
        commits = project_json["commits"]
        nb_test_to_be_amplified = 0  # DATA 2
        nb_success = [0 for x in range(0, len(random_generator.seeds))]
        nb_test_amplified = [[], [], [], []]  # DATA 4
        time = [[], [], [], []]
        coverage = []
        nb_test_total_project = []
        print '\multirow{11}{*}{\\rotvertical{' + project + '}}'
        for commit_json in commits[0:10]:
            path_to_commit_folder = toolbox.get_absolute_path(
                toolbox.prefix_result + project + '/' + toolbox.get_output_folder_for_commit(commit_json, commits)
            ) + '/'
            coverage_commit = get_diff_coverage_commit(path_to_commit_folder)
            nb_test_total = commit_json['nb_test_per_commit']
            if coverage_commit == -1:
                continue
            coverage.append(round(coverage_commit, 2))
            nb_test_to_be_amplified = nb_test_to_be_amplified + get_nb_test_to_be_amplified(path_to_commit_folder)
            nb_test_amplified_seed =  [0 for x in range(0, len(random_generator.seeds))]
            time_seed =  [0 for x in range(0, len(random_generator.seeds))]
            success_seed = [0 for x in range(0, len(random_generator.seeds))]
            for seed in random_generator.seeds:
                path_to_seed_result = path_to_commit_folder + '/seeds/' + seed + '/'
                if os.path.isdir(path_to_seed_result):
                    if is_success(path_to_seed_result):
                        success_seed[random_generator.seeds.index(seed)] = success_seed[random_generator.seeds.index(seed)] + 1

                    nb_test_amplified_seed[random_generator.seeds.index(seed)] = nb_test_amplified_seed[
                                                                    random_generator.seeds.index(seed)] + get_nb_test_amplified(path_to_seed_result)
                    if not commit_json['concernedModule'] == "":
                        time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                               commit_json['concernedModule'].split('/')[-2])
                    else:
                        time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                               project + toolbox.suffix_parent)
                else:
                    correct_range, correct_step = get_range_and_step(path_to_commit_folder, seed)
                    # the result has been splitted into different folder
                    for x in correct_range:
                        path_to_seed_result = path_to_commit_folder + '/seeds/' + seed + '_' + str(x) + '_' + str(x+correct_step) + '/'
                        if os.path.isdir(path_to_seed_result):
                            if is_success(path_to_seed_result):
                                success_seed[random_generator.seeds.index(seed)] = success_seed[random_generator.seeds.index(seed)] + 1

                            nb_test_amplified_seed[random_generator.seeds.index(seed)] = nb_test_amplified_seed[
                                                                                             random_generator.seeds.index(seed)] + get_nb_test_amplified(path_to_seed_result)
                            if not commit_json['concernedModule'] == "":
                                time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                                                                         commit_json['concernedModule'].split('/')[-2])

                                total_time = total_time + get_time(path_to_seed_result, commit_json['concernedModule'].split('/')[-2])
                            else:
                                time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                                                                         project + toolbox.suffix_parent)
                                total_time = total_time + get_time(path_to_seed_result,
                                                                   project + toolbox.suffix_parent)


            path_to_ref_result = path_to_commit_folder + '/input_amplification/1/'
            success_ref = 0
            number_test_ref = 0
            if os.path.isdir(path_to_ref_result):
                if is_success(path_to_ref_result):
                    success_ref = 1
                    number_test_ref = get_nb_test_amplified(path_to_ref_result)
            else:
                mode = 'input_amplification/1'
                base_path_to_mode_result = path_to_commit_folder + '/' + mode.split('/')[0] + '_'
                correct_range = range(0, 30, 5) if mode.split('/')[1] == '1' else range(0, 30, 2)
                correct_step = 5 if mode.split('/')[1] == '1' else 2
                for x in correct_range:
                    path_to_mode_result = base_path_to_mode_result + str(x) + '_' + str(x+correct_step) + '/' + mode.split('/')[1] + '/'
                    if os.path.isdir(path_to_mode_result):
                        if is_success(path_to_mode_result):
                            success_ref = 1
                            number_test_ref = get_nb_test_amplified(path_to_mode_result)

            print_line(
                str(commit_json["sha"])[0:7],
                success_ref,
                number_test_ref,
                success_seed,
                nb_test_amplified_seed,
            )

            nb_success[0] = nb_success[0] + success_seed[0]
            nb_success[1] = nb_success[1] + success_seed[1]
            nb_success[2] = nb_success[2] + success_seed[2]
            nb_test_total_project.append(nb_test_total)
            nb_test_amplified[0].append(nb_test_amplified_seed[0])
            nb_test_amplified[1].append(nb_test_amplified_seed[1])
            nb_test_amplified[2].append(nb_test_amplified_seed[2])

        print '\\hline'

    return total_time

def get_range_and_step(path_to_commit_folder, seed):
    if os.path.isdir(path_to_commit_folder + '/seeds/' + seed + '_0_2/'):
        return range(0,30,2), 2
    elif os.path.isdir(path_to_commit_folder + '/seeds/' + seed + '_0_1/'):
        return range(0,30,1), 1
    else:
        return range(0,30,5), 5

def avg(table):
    return "{0:.2f}".format(avg_value(table))

def avg_value(table):
    if len(table) == 0:
        return 0.0
    return sum(table) / float(len(table))


def print_line(id,
               success_ref,
               number_test_ref,
               success_seed,
               array_number_test_per_seed):
    to_print = ['', id]
    for i in range(0, len(array_number_test_per_seed)):
        mark = '-'
        if success_seed[i] > 0:
            if success_ref > 0:
                mark = '\\cmark'
            else:
                mark = '\\lcmark'
            to_print.append('{}'.format(array_number_test_per_seed[i]))
        else:
            if not success_seed[i] > 0 and success_ref > 0:
                mark = '\\xmark'
            to_print.append('{}'.format('-'))
    print ' & '.join(to_print) + '\\\\'

def convert_date(date):
    if date == "":
        return "TODO"
    splitted_date = date.split('/')
    return '/'.join([splitted_date[0], splitted_date[1], splitted_date[2][2:4]])


def convert_diff_size(size_diff):
    return '{\\color{ForestGreen}{' + \
           str(size_diff[0]) + \
           '\\xspace}} / {\\color{red}{' + \
           str(size_diff[1]) + '\\xspace}}'


def print_header():
    to_print = ['', 'id']
    for i in range(0, len(random_generator.seeds)):
        to_print.append(str(i))
    print '&'.join(to_print) + '\\\\\n\\hline'

def get_diff_coverage_commit(path_to_commit_folder):
    with open(path_to_commit_folder + '/parent_coverage_testsThatExecuteTheChanges_coverage.csv') as coverage_cvs:
        for line in coverage_cvs:
            if line.startswith('total;'):
                splitted_line = line.split(';')
                if int(splitted_line[2]) == 0:
                    print path_to_commit_folder
                    return -1
                return compute_percentage(splitted_line[2], splitted_line[1])


def compute_percentage(total, actual):
    return float(actual) / float(total) * 100.0


def convert_time(time):
    time_in_second = time / 1000
    if time_in_second > 120:
        time_in_minute = float(time_in_second) / 60.0
        if time_in_minute > 120:
            time_in_hours = float(time_in_minute) / 60.0
            return "{0:.1f}".format(time_in_hours) + 'h'
        else:
            return "{0:.1f}".format(time_in_minute) + 'm'
    else:
        return "{0:.1f}".format(time_in_second) + 's'


def is_success(path_to_mode_result):
    for root, dirs, files in os.walk(path_to_mode_result):
        for file in files:
            if file.endswith('.java'):
                # print path_to_mode_result
                return True
    return False


def get_nb_test_amplified(path_to_mode_result):
    nb_test_amplified = 0
    if not os.path.isfile(path_to_mode_result + 'report.txt'):
        return nb_test_amplified
    with open(path_to_mode_result + 'report.txt') as report_file:
        lines = report_file.read()
        for line in lines.split('\n'):
            if line.endswith('amplified test fails on the new versions.'):
                nb_test_amplified = nb_test_amplified + int(line.split(' ')[0])
    return nb_test_amplified


def get_time(path_to_mode_result, project):
    time = 0
    if os.path.isfile(path_to_mode_result + project + '.json'):
        project_json_result = toolbox.get_json_file(path_to_mode_result + project)
        for classes_json in project_json_result['classTimes']:
            time = time + classes_json['timeInMs']
    return time


def get_nb_test_to_be_amplified(path_to_commit_folder):
    nb_test_to_be_amplified = 0
    with open(
            path_to_commit_folder + 'parent_coverage_' + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + ".csv") as csv_file:
        for line in csv_file:
            nb_test_to_be_amplified = nb_test_to_be_amplified + len(line.split(';')[1:])
    return nb_test_to_be_amplified


if __name__ == '__main__':
    build_table(projects=toolbox.projects)
