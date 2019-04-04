import sys
import toolbox
import numpy as np
import matplotlib.pyplot as plt

def run(project):
    root_project_folder_result = toolbox.prefix_result + project
    json_project = toolbox.get_json_file(toolbox.prefix_current_dataset + project)
    coverage_project = []
    commits = json_project["commits"]
    for commit in commits[0:10]:
        file_name = toolbox.get_output_folder_for_commit(commits=commits, commit=commit)
        commit_result_directory = root_project_folder_result + "/" + file_name + "/" + \
                                  "commit_coverage_" + toolbox.name_of_csv_with_list_of_test_that_execute_the_changes + "_coverage.csv"
        with open(commit_result_directory) as data_file:
            for line in data_file:
                if line.startswith("total"):
                    splitted = line.split(";")
                    executed = int(splitted[1])
                    total = int(splitted[2])
                    if total == 0 or executed > total:
                        print commit_result_directory
                        continue
                    coverage = (float(executed) / float(total)) * 100.0
                    coverage_project.append(coverage)
    return coverage_project


def plot(bins):
    colors = ["#FF0000", "#FF8800", "#FFFF00", "#00FF00", "#008800"]
    labels = ["0-25%", "25-50%", "50-75%", "75-100%", "100%"]
    indices = np.arange(len(toolbox.projects))
    total = [sum(bin) for bin in bins]
    current_bin = [bin[0] for bin in bins]
    current_proportion = np.true_divide(current_bin, total) * 100
    ax = plt.subplot(111)
    plots = [ax.bar(indices, current_proportion, width=0.8, label=labels[0], color=colors[0])]
    previous_proportion = current_proportion
    print current_bin, current_proportion, previous_proportion
    for index in range(1, len(bins[0])):
        current_bin = [bin[index] for bin in bins]
        current_proportion = np.true_divide(current_bin, total) * 100
        print current_bin, current_proportion, previous_proportion
        plots.append(
        ax.bar(indices,
                current_proportion,
                width=0.8,
                label=labels[index],
                color=colors[index],
                bottom=previous_proportion
        ))
        previous_proportion = previous_proportion + current_proportion
    plt.xticks()
    plt.xticks(indices, toolbox.projects)
    plt.setp(plt.gca().get_xticklabels(), rotation=30, horizontalalignment='right')
    ax.legend(plots, labels, fancybox=True, shadow=True, loc='upper center', ncol=5,  bbox_to_anchor=(0.5, 1.1))
    plt.show()


def sort(coverages):
    bin_0_25 = []
    bin_25_50 = []
    bin_50_75 = []
    bin_75_100 = []
    bin_100 = []
    for coverage in coverages:
        if coverage <= 25.0:
            bin_0_25.append(coverage)
        elif coverage <= 50.0:
            bin_25_50.append(coverage)
        elif coverage <= 75.0:
            bin_50_75.append(coverage)
        elif coverage < 100.0:
            bin_75_100.append(coverage)
        else:
            bin_100.append(coverage)
    table_result = [len(bin_0_25), len(bin_25_50), len(bin_50_75), len(bin_75_100), len(bin_100)]
    return table_result


if __name__ == '__main__':
    bins = []
    for project in toolbox.projects:
        coverage_project = run(project)
        bins.append(sort(coverage_project))
    plot(bins)
