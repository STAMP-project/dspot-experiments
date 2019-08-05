import toolbox


def build_table(projects):
    print_header()
    gray = False
    LOCs = []
    nb_discarded_tt = []
    nb_matching_tt = []
    total_tt = []
    total_matching_tt = []
    perc_matching_tt = []
    for project in projects:
        json_project = toolbox.get_json_file('dataset/april-2019/' + project)
        json_project_blacklist = toolbox.get_json_file('dataset/april-2019/' + project + '_blacklist')
        nb_discarded = len(json_project_blacklist['blacklist'])
        nb_matching = len(filter(lambda x: x['cause'] == 'NoTestExecuteChanges', json_project_blacklist['blacklist']))
        LOC = json_project['LOC']
        start_date = json_project['commits'][9]['date']
        end_date = json_project['date']
        total = nb_discarded + 10
        total_matching = nb_matching + 10
        perc_matching = float(total_matching) / float(total) * 100.0
        perc_matching_tt.append(perc_matching)
        print_line(
            project,
            LOC,
            start_date,
            end_date,
            total,
            nb_discarded,
            str(total_matching) + ('(' + "{0:.2f}".format(perc_matching) + '\%)'),
            10,
            gray
        )
        LOCs.append(LOC)
        nb_discarded_tt.append(nb_discarded)
        nb_matching_tt.append(nb_matching)
        total_tt.append(total)
        total_matching_tt.append(total_matching)
        gray = not gray

    print '\\hline'

    print_line(
        'summary',
        sum(LOCs),
        '9/10/2015',
        '04/18/2019',
        'avg(' + avg(total_tt) + ')',
        'avg(' + avg(nb_discarded_tt) + ')',
        'avg(' + avg(total_matching_tt) + ('(' + "{0:.2f}".format(avg_value(perc_matching_tt)) + '\%)') + ')',
        60,
        gray
    )

    print sum(total_tt)

def avg(table):
    return "{0:.2f}".format(avg_value(table))

def avg_value(table):
    if len(table) == 0:
        return 0.0
    return sum(table) / float(len(table))


def print_line(project, LOC, start_date, end_date, total, discarded, matching, selected, gray):
    if gray:
        print '\\rowcolor[HTML]{EFEFEF}'
    print '\\scriptsize{' + project + '}' \
          + '\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t&\t{}\t\\\\'.format(LOC, start_date, end_date,
                                                                             total, discarded, matching, selected)


def print_header():
    print '\\hline\n' + \
          'project &\n' + \
          'LOC &\n' + \
          '\\begin{tabular}{c}start\\\\date\end{tabular}&\n' + \
          '\\begin{tabular}{c}end\\\\date\end{tabular}&\n' + \
          '\\begin{tabular}{c}\#total\\\\commits\end{tabular}&\n' + \
          '\\begin{tabular}{c}\#discarded\\\\commits\end{tabular}&\n' + \
          '\\begin{tabular}{c}\#matching\\\\commits\end{tabular}&\n' + \
          '\\begin{tabular}{c}\#selected\\\\commits\end{tabular}\\\\\n' + \
          '\\hline'


if __name__ == '__main__':
    build_table(projects=toolbox.projects)
