import sys
import toolbox
import os
import diff_size
import random_generator


def build_table(projects):
    print_header()
    gray = False
    coverage = []
    total_nb_test_mode = [0, 0]
    total_nb_test_to_be_amplified = 0
    total_time = [[], []]
    nb_success = [0, 0]
    time_mode_for_success = [ [], [] ]
    for project in projects:
        project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
        nb_commit = project_json["numberCommits"]  # DATA 1
        commits = project_json["commits"]
        nb_test_to_be_amplified = 0  # DATA 2
        nb_test_amplified = [[], [], [], []]  # DATA 4
        time = [[], [], [], []]
        nb_test_total_project = []
        print '\multirow{11}{*}{\\rotvertical{' + project + '}}'
        commits = commits[0:10]
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
            modes = ["assert_amplification"] #, "input_amplification/seeds/"]
            nb_test_amplified_mode = [0, 0]
            time_mode = [0, 0]
            success_mode = [0, 0]
            size_diff = diff_size.size(path_to_commit_folder + "commit_coverage_patch.diff")
            date = commit_json['date']
            nb_test_modified_commit = commit_json['nbModifiedTest']
            for mode in modes:
                is_already_saved = False
                path_to_mode_result = path_to_commit_folder + '/' + mode + '/'
                if os.path.isdir(path_to_mode_result):
                    if not commit_json['concernedModule'] == "":
                        time_to_be_added = get_time(path_to_mode_result, commit_json['concernedModule'].split('/')[-2])
                    else:
                        time_to_be_added = get_time(path_to_mode_result,project + toolbox.suffix_parent)
                    if is_success(path_to_mode_result):
                        success_mode[modes.index(mode)] = success_mode[modes.index(mode)] + 1
                        time_mode_for_success[modes.index(mode)].append(int(time_to_be_added))

                    nb_test_amplified_mode[modes.index(mode)] = nb_test_amplified_mode[modes.index(mode)] + get_nb_test_amplified(path_to_mode_result)
                    time_mode[modes.index(mode)] = time_mode[modes.index(mode)] + time_to_be_added
                else:
                    # the result has been splitted into different folder
                    base_path_to_mode_result = path_to_commit_folder + '/' + mode.split('/')[0] + '_'
                    time_for_success = 0
                    correct_range = range(0, 30, 5) if mode.split('/')[1] == '1' else range(0, 30, 2)
                    for x in correct_range:
                        path_to_mode_result = base_path_to_mode_result + str(x) + '_' + str(x+2) + '/' + mode.split('/')[1] + '/'
                        if os.path.isdir(path_to_mode_result):
                            if not commit_json['concernedModule'] == "":
                                time_to_be_added = get_time(path_to_mode_result, commit_json['concernedModule'].split('/')[-2])
                            else:
                                time_to_be_added = get_time(path_to_mode_result,project + toolbox.suffix_parent)
                            if is_success(path_to_mode_result) and not is_already_saved:
                                is_already_saved = True
                                success_mode[modes.index(mode)] = success_mode[modes.index(mode)] + 1
                                time_for_success = time_for_success + time_to_be_added
                            nb_test_amplified_mode[modes.index(mode)] = nb_test_amplified_mode[modes.index(mode)] + get_nb_test_amplified(path_to_mode_result)
                            time_mode[modes.index(mode)] = time_mode[modes.index(mode)] + time_to_be_added
                    time_mode_for_success[modes.index(mode)].append(int(time_for_success))
            nb_test_amplified_mode[1], time_mode[1] = \
                compute_avg_for_seeds(project, path_to_commit_folder + '/', commit_json)
            print_line(
                str(commit_json["sha"])[0:7],
                convert_date(date),
                str(nb_test_modified_commit),
                str(nb_test_total),
                convert_diff_size(size_diff),
                str(round(coverage_commit, 2)),
                get_nb_test_to_be_amplified(path_to_commit_folder),
                '\\cmark(' + str(nb_test_amplified_mode[0]) + ')' if success_mode[0] > 0 else "0",
                convert_time(time_mode[0]),
                '\\cmark(' + "{0:.2f}".format(nb_test_amplified_mode[1]) + ')' if nb_test_amplified_mode[1] > 0 else "0",
                convert_time(time_mode[1])
            )

            time[0].append(time_mode[0])
            time[1].append(time_mode[1])
            total_time[0].append(time_mode[0])
            total_time[1].append(time_mode[1])
            nb_success[0] = nb_success[0] + success_mode[0]
            nb_success[1] = nb_success[1] + success_mode[1]
            nb_test_total_project.append(nb_test_total)
            nb_test_amplified[0].append(nb_test_amplified_mode[0])
            nb_test_amplified[1].append(nb_test_amplified_mode[1])
            total_nb_test_mode[0] = total_nb_test_mode[0] + nb_test_amplified_mode[0]
            total_nb_test_mode[1] = total_nb_test_mode[1] + nb_test_amplified_mode[1]

        total_nb_test_to_be_amplified = total_nb_test_to_be_amplified + nb_test_to_be_amplified

        # percentage_success_aampl = compute_percentage(nb_commit, nb_success[0])
        # percentage_success_iampl = compute_percentage(nb_commit, nb_success[1])

        # time[0] = convert_time(time[0])
        # time[1] = convert_time(time[1])

        print '\\hline'

        print '\\rowcolor[HTML]{EFEFEF}'
        print_line(
            'total',
            '\\xspace{}',
            '\\xspace{}',
            '\\xspace{}',
            '\\xspace{}',
            '\\xspace{}',
            nb_test_to_be_amplified,
            int(sum(nb_test_amplified[0])),
            str(convert_time(sum(time[0]))),
            int(sum(nb_test_amplified[1])),
            str(convert_time(sum(time[1])))
        )
        print '\\hline'

    print '\\hline'
    print_line(
        'total',
        '\\xspace{}',
        '\\xspace{}',
        '\\xspace{}',
        '\\xspace{}',
        '\\xspace{}',
        total_nb_test_to_be_amplified,
        str(nb_success[0]) +  '(' + str(total_nb_test_mode[0]) + ')',
        'avg(' + str(convert_time(avg_value(total_time[0]))) + ')',
        str(nb_success[1]) +  '(' + str(total_nb_test_mode[1]) + ')',
        'avg(' + str(convert_time(avg_value(total_time[1]))) + ')'
    )
    print '\\hline'

    # some needed values
    cpt = 0
    for c in coverage:
        if float(c) > 75.0:
            cpt = cpt + 1
    print 'avg coverage ' + str(avg(coverage))
    print 'commit with more than 75% of coverage : ' + str(cpt)
    print 'avg success time aampl: ' + str(convert_time(avg_value(time_mode_for_success[0])))
    print 'avg success time sbampl: ' + str(convert_time(avg_value(time_mode_for_success[1])))
    total_success = nb_success[1]
    print 'success ' + str(total_success) + ' / 60 ' + "{0:.2f}".format(compute_percentage(60, total_success))

def get_range_and_step(path_to_commit_folder, seed):
    if os.path.isdir(path_to_commit_folder + '/seeds/' + seed + '_0_2/'):
        return range(0,30,2), 2
    elif os.path.isdir(path_to_commit_folder + '/seeds/' + seed + '_0_1/'):
        return range(0,30,1), 1
    else:
        return range(0,30,5), 5

def compute_avg_for_seeds(project, path_to_commit_folder, commit_json):
    nb_test_amplified_seed =  [0 for x in range(0, len(random_generator.seeds) + 1)]
    time_seed =  [0 for x in range(0, len(random_generator.seeds) + 1)]

    # initial run
    path_to_seed_result = path_to_commit_folder + "input_amplification/3"
    if os.path.isdir(path_to_seed_result):
        nb_test_amplified_seed[-1] = \
            nb_test_amplified_seed[-1] + get_nb_test_amplified(path_to_seed_result)
        if not commit_json['concernedModule'] == "":
            time_seed[-1] = time_seed[-1] + get_time(path_to_seed_result, commit_json['concernedModule'].split('/')[-2])
        else:
            time_seed[-1] = time_seed[-1] + get_time(path_to_seed_result, project + toolbox.suffix_parent)
    else:
        correct_range, correct_step = get_range_and_step(path_to_commit_folder, "input_amplification/3")
        # the result has been splitted into different folder
        for x in correct_range:
            path_to_seed_result = path_to_commit_folder + '/seeds/' + "input_amplification/3" + '_' + str(x) + '_' + str(x+correct_step) + '/'
            if os.path.isdir(path_to_seed_result):
                nb_test_amplified_seed[-1] = nb_test_amplified_seed[-1] + get_nb_test_amplified(path_to_seed_result)
                if not commit_json['concernedModule'] == "":
                    time_seed[-1] = time_seed[-1] + get_time(path_to_seed_result, commit_json['concernedModule'].split('/')[-2])
                else:
                    time_seed[-1] = time_seed[-1] + get_time(path_to_seed_result, project + toolbox.suffix_parent)
    # seeds
    for seed in random_generator.seeds:
        path_to_seed_result = path_to_commit_folder + "seeds/" + seed + '/'
        if os.path.isdir(path_to_seed_result):
            nb_test_amplified_seed[random_generator.seeds.index(seed)] = \
                nb_test_amplified_seed[random_generator.seeds.index(seed)] + get_nb_test_amplified(path_to_seed_result)
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

                    nb_test_amplified_seed[random_generator.seeds.index(seed)] = nb_test_amplified_seed[
                                                                                     random_generator.seeds.index(seed)] + get_nb_test_amplified(path_to_seed_result)
                    if not commit_json['concernedModule'] == "":
                        time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                                                                 commit_json['concernedModule'].split('/')[-2])

                    else:
                        time_seed[random_generator.seeds.index(seed)] = time_seed[random_generator.seeds.index(seed)] + get_time(path_to_seed_result,
                                                                                                                                 project + toolbox.suffix_parent)
    return avg_value(nb_test_amplified_seed), avg_value(time_seed)

def avg(table):
    return "{0:.2f}".format(avg_value(table))

def avg_value(table):
    if len(table) == 0:
        return 0.0
    return sum(table) / float(len(table))


def print_line(id,
               date,
               nb_modified_test,
               nb_test,
               size_diff,
               diff_coverage,
               number_test_to_be_amplified,
               number_aampl,
               time_aampl,
               number_iampl_it_1,
               time_iampl_it_1):
    print "&  {}  &  {} &  {}  &  {}  &  {}  &  {}  &  {}  &  {}  &  {}  &  {}  &  {}\\\\".format(
        id,
        date,
        nb_test,
        nb_modified_test,
        size_diff,
        diff_coverage,
        number_test_to_be_amplified,
        number_aampl,
        time_aampl,
        number_iampl_it_1,
        time_iampl_it_1
    )


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
    header = [
        '',
        'id',
        'date',
        '\\#Test',
        '\\begin{tabular}{c}\\#Modified\\\\Tests\end{tabular}',
        convert_diff_size(('+', '-')),
        'Cov',
        '\\begin{tabular}{c}\\#Selected\\\\Tests\end{tabular}',
        '\\begin{tabular}{c}\\#\\aampl\\\\Tests\end{tabular}',
        'Time',
        '\\begin{tabular}{c}\\#\\sbampl\\\\Tests\end{tabular}',
        'Time\\\\'
    ]
    print '&\n'.join(header) + '\\\\\n\\hline'

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
