import sys
import toolbox
import os


def build_table(projects):
    print_header()
    gray = False
    for project in projects:
        project_json = toolbox.get_json_file(toolbox.get_absolute_path(toolbox.prefix_current_dataset + project))
        nb_commit = project_json["numberCommits"]  # DATA 1
        commits = project_json["commits"]
        nb_test_to_be_amplified = 0  # DATA 2
        nb_success = [0, 0]  # DATA 3
        nb_test_amplified = [0, 0]  # DATA 4
        time = [0, 0]
        coverage = []
        for commit_json in commits[0:10]:
            path_to_commit_folder = toolbox.get_absolute_path(
                toolbox.prefix_result + project + '/' + toolbox.get_output_folder_for_commit(commit_json, commits)
            ) + '/'
            nb_test_to_be_amplified = nb_test_to_be_amplified + get_nb_test_to_be_amplified(path_to_commit_folder)
            modes = ["assert_amplification", "input_amplification"]
            nb_test_amplified_mode = [0, 0]
            time_mode = [0, 0]
            success_mode = [0, 0]
            coverage_commit = get_diff_coverage_commit(path_to_commit_folder)
            coverage.append(coverage_commit)
            for mode in modes:
                path_to_mode_result = path_to_commit_folder + '/' + mode + '/'
                if os.path.isdir(path_to_mode_result):
                    if is_success(path_to_mode_result):
                        success_mode[modes.index(mode)] = success_mode[modes.index(mode)] + 1
                    nb_test_amplified_mode[modes.index(mode)] = nb_test_amplified_mode[
                                                                    modes.index(mode)] + get_nb_test_amplified(
                        path_to_mode_result)
                    if not commit_json['concernedModule'] == "":
                        time_mode[modes.index(mode)] = time_mode[modes.index(mode)] + get_time(path_to_mode_result,
                                                                                               commit_json[
                                                                                                   'concernedModule'].split(
                                                                                                   '/')[
                                                                                                   -2])
                    else:
                        time_mode[modes.index(mode)] = time_mode[modes.index(mode)] + get_time(path_to_mode_result,
                                                                                                project + toolbox.suffix_parent)
            '''
            if gray:
                print '\\rowcolor[HTML]{EFEFEF}'
            gray = not gray
            print_line(
                str(commit_json["sha"])[0:7],
                str(coverage_commit),
                get_nb_test_to_be_amplified(path_to_commit_folder),
                nb_test_amplified_mode[0],
                "\\cmark" if success_mode[0] == 1 else "\\xmark",
                convert_time(time_mode[0]),
                nb_test_amplified_mode[1],
                "\\cmark" if success_mode[1] == 1 else "\\xmark",
                convert_time(time_mode[1])
            )'''

            time[0] = time[0] + time_mode[0]
            time[1] = time[1] + time_mode[1]
            nb_test_amplified[0] = nb_test_amplified[0] + nb_test_amplified_mode[0]
            nb_test_amplified[1] = nb_test_amplified[1] + nb_test_amplified_mode[1]
            nb_success[0] = nb_success[0] + success_mode[0]
            nb_success[1] = nb_success[1] + success_mode[1]

        #percentage_success_aampl = compute_percentage(nb_commit, nb_success[0])
        #percentage_success_iampl = compute_percentage(nb_commit, nb_success[1])

        time[0] = convert_time(time[0])
        time[1] = convert_time(time[1])


        if gray:
            print '\\rowcolor[HTML]{EFEFEF}'
        gray = not gray
        print_line(
            '\\tiny{\\textsc{' + project + '}}',
            '\\tiny{' + str(round(avg(coverage), 2)) + '}',
            '\\tiny{' + str(nb_test_to_be_amplified) + '}',
            '\\tiny{' + str(nb_test_amplified[0]) + '}',
            '\\tiny{' + str(nb_success[0]) + '}',
            '\\tiny{' + str(time[0]) + '}',
            '\\tiny{' + str(nb_test_amplified[1]) + '}',
            '\\tiny{' + str(nb_success[1]) + '}',
            '\\tiny{' + str(time[1]) + '}'
        )

def avg(table):
    if len(table) == 0:
        return 0.0
    return sum(table) / float(len(table))

def print_line(id, diff_coverage, number_test_to_be_amplified, number_aampl, success_mark_aampl, time_aampl, number_iampl,
               success_mark_iampl,
               time_iampl):
    print "\t{}\t&\t{}&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\\\\".format(
        id,
        diff_coverage,
        number_test_to_be_amplified,
        number_aampl,
        success_mark_aampl,
        time_aampl,
        number_iampl,
        success_mark_iampl,
        time_iampl
    )

def print_header():
    print 'id\t&\tCov\t&\t\\#Test\t&\t\\#Aampl Tests\t&\tSuccess\t&\tTime(min)\t&\t\\#Iampl Tests\t&\tSuccess\t&\tTime(min)\\\\'
    print '\\hline'

def get_diff_coverage_commit(path_to_commit_folder):
    if os.path.isfile(path_to_commit_folder + '/commit_coverage_testsThatExecuteTheChanges_coverage.csv'):
        with open(path_to_commit_folder + '/commit_coverage_testsThatExecuteTheChanges_coverage.csv') as coverage_cvs:
            for line in coverage_cvs:
                if line.startswith('total;'):
                    splitted_line = line.split(';')
                    if int(splitted_line[2]) == 0:
                        print path_to_commit_folder
                        return 0.0
                    return compute_percentage(splitted_line[2], splitted_line[1])
    else:
        print path_to_commit_folder
        return 0.0

def compute_percentage(total, actual):
    return float(actual) / float(total) * 100.0


def convert_time(time):
    return time / 1000 / 60  # from ms to minute


def is_success(path_to_mode_result):
    for root, dirs, files in os.walk(path_to_mode_result):
        for file in files:
            if file.endswith('.java'):
                # print path_to_mode_result
                return True
    return False

def get_nb_test_amplified(path_to_mode_result):
    nb_test_amplified = 0
    for file in os.listdir(path_to_mode_result):
        if file.endswith('_change_detector.json'):
            file_json = toolbox.get_json_file(path_to_mode_result + "/" + file)
            nb_test_amplified = nb_test_amplified + len(file_json['testCases'])
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
            path_to_commit_folder + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + ".csv") as csv_file:
        for line in csv_file:
            nb_test_to_be_amplified = nb_test_to_be_amplified + len(line.split(';')[1:])
    return nb_test_to_be_amplified


if __name__ == '__main__':
    build_table(projects=toolbox.projects)
