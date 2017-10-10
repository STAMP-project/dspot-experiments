import sys
import csv

def row_to_mutant(row):
    return row[2] + ":" + row[3] + ":" + str(row[4])

def replace(csv, rows_mutant_killed):
    cpt = 0
    for row_mutant_killed in rows_mutant_killed:
        mutant = row_to_mutant(row_mutant_killed)
        for current_row in csv:
            if current_row[index_state] == "SURVIVED" and \
                            row_to_mutant(current_row) == mutant:
                    csv[csv.index(current_row)] = row_mutant_killed
                    cpt += 1
                    break
    print cpt
    return csv

def merge(path_file1, path_file2):
    # must take all killed of 2, keep only that are remained alive in 1
    killed_in_2 = []

    with open(path_file1, 'rb') as csvfile1:
        mutations_csv1 = []
        for row in csv.reader(csvfile1, delimiter=',', quotechar='|'):
            mutations_csv1.append(row)
        with open(path_file2, 'rb') as csvfile:
            mutations_csv2 = csv.reader(csvfile, delimiter=',', quotechar='|')
            for row in mutations_csv2:
                if row[index_state] == "KILLED":
                    killed_in_2.append(row)

            new_mutations_csv1 = replace(mutations_csv1, killed_in_2)
            print len(mutations_csv1), len(new_mutations_csv1)
            writer = csv.writer(open('output.csv', 'w'))
            writer.writerows(new_mutations_csv1)

if __name__ == '__main__':
    index_state = 5
    merge(sys.argv[1], sys.argv[2])

