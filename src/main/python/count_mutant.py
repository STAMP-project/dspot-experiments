import csv

def countForTestClass(path):
    total = 0
    killed = 0
    with open(path, 'rb') as csvfile:
        mutations_csv = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in mutations_csv:
            if row[5] == "SURVIVED":
                total += 1
            elif row[5] == "KILLED":
                total += 1
                killed += 1
    return total, killed
