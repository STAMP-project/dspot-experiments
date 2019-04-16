import toolbox


def build_table(projects):
    print_header()
    gray = False
    for project in projects:
        json_project = toolbox.get_json_file('dataset/september-2018/' + project)
        json_project_blacklist = toolbox.get_json_file('dataset/september-2018/' + project + '_blacklist')
        nb_discarded = len(json_project_blacklist['blacklist'])
        nb_matching = len(filter(lambda x: x['cause'] == 'NoTestExecuteChanges', json_project_blacklist['blacklist']))
        LOC = json_project['LOC']
        start_date = json_project['commits'][9]['date']
        end_date = json_project['date']
        print_line(
            project,
            LOC,
            start_date,
            end_date,
            nb_discarded + 10,
            nb_discarded,
            nb_matching + 10,
            10,
            gray
        )
        gray = not gray


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
