import csv
import sys
import count_mutant

def leaveKilledMore(path, amplPath):
    with open(path, 'rb') as csvfile:
        mutations_csv = csv.reader(csvfile, delimiter=',', quotechar='|')
        killed_original = []
        for row in mutations_csv:
            if row[5] == "KILLED" or row[5] == "SURVIVED":
                killed_original.append(row)
    with open(amplPath, 'rb') as csvfile:
        mutations_csv_ampl = csv.reader(csvfile, delimiter=',', quotechar='|')
        killed_ampl = []
        for row in mutations_csv_ampl:
            if (row[5] == "KILLED" or row[5] == "SURVIVED") and not rowInTable(row, killed_original):
                killed_ampl.append(row)
                print row

def rowInTable(row, table):
    for row_in_table in table:
        if row_in_table[0] == row[0] and\
            row_in_table[1] == row[1] and\
            row_in_table[2] == row[2] and\
            row_in_table[3] == row[3] and \
            row_in_table[4] == row[4]:
            return True
    return False

if __name__ == '__main__':

    if len(sys.argv) < 3:
        print "error need to give path to mutations files"
    else:
        leaveKilledMore(sys.argv[1], sys.argv[2])
        executed, killed = count_mutant.countForTestClass(sys.argv[1])
        executed_ampl, killed_ampl = count_mutant.countForTestClass(sys.argv[2])
        print executed, killed
        print executed_ampl, killed_ampl