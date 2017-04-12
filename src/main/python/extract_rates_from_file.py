import csv
import sys

def extract(path):
    indexState = 5
    nbTotal=0
    nbCoveredMutant=0
    nbKilledMutant=0
    nbError=0
    nbRun=0
    with open(path , 'rb') as csvfile:
        mutations_csv = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in mutations_csv:
            if row[indexState] == "KILLED":
                nbKilledMutant += 1
                nbCoveredMutant += 1
            elif row[indexState] == "SURVIVED":
                nbCoveredMutant += 1
            if row[indexState] == "TIMED_OUT" or \
               row[indexState] == "RUN_ERROR" or \
               row[indexState] == "MEMORY_ERROR" or \
               row[indexState] == "NON_VIABLE":
                nbError += 1
            else:
                nbRun += 1
            nbTotal += 1
        return nbTotal, nbRun, nbCoveredMutant, nbKilledMutant, nbError

if __name__ == '__main__':
    path = sys.argv[1]
    print extract(path)
