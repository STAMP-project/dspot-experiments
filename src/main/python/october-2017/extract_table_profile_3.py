import profiling_test_class
import sys
import read_select_classes
import count_mutant

gray = False

def run(projects):

    PMS = []
    discardeds = []
    nb_classes_per_project = {"javapoet" : (0,0),
                              "mybatis": (0,0),
                              "traccar": (0,0),
                              "stream-lib": (0,0),
                              "mustache.java": (0,0),
                              "twilio-java": (0,0),
                              "jsoup": (0,0),
                              "protostuff": (0,0),
                              "logback": (0,0),
                              "retrofit": (0,0)
                            }
    for project in projects:
        top, worst, pr = read_select_classes.read([project])
        if computePMS(top[0], project) < 100.0:
            PMS.append( ((computePMS(top[0], project)), top[0], project))
        else:
            discardeds.append( (project , top[0], computePMS(top[0], project)) )
        if computePMS(top[1], project) < 100.0:
            PMS.append( ((computePMS(top[1], project)), top[1], project))
        else:
            discardeds.append( (project , top[1], computePMS(top[1], project)) )
        PMS.append( ((computePMS(worst[0], project)), worst[0], project))
        PMS.append( ((computePMS(worst[1], project)), worst[1], project))
        #if not pr == "":
            #PMS.append( ((computePMS(pr, project)), pr, project))

    nb_test = 0
    nb_test_ampl = 0
    mutation_score = []
    for array in PMS:
        if array[0] > 50.0:
            nb_classes_per_project[array[-1]] = (nb_classes_per_project[array[-1]][0] + 1, nb_classes_per_project[array[-1]][1])
            pms = line(array[1], array[2])
            nb_test += pms[2]
            nb_test_ampl += pms[4]
            mutation_score.append(pms[3])

    print "\\hline"
    print "&\scriptsize{Low $PMS$}\\\\"
    print "\\hline"
    for array in PMS:
        if array[0] <= 50.0:
            nb_classes_per_project[array[-1]] = (nb_classes_per_project[array[-1]][0], nb_classes_per_project[array[-1]][1] + 1)
            pms = line(array[1], array[2])
            nb_test += pms[2]
            nb_test_ampl += pms[4]
            mutation_score.append(pms[3])

    print mutation_score
    mutation_score.sort()
    print mutation_score
    print "median mutation score : ", mutation_score[len(mutation_score) / 2]
    print "total number of test : ", nb_test
    print "total number of amplified test : ", nb_test_ampl
    print "discarded project / classes (", len(discardeds), "): "
    for discarded in discardeds:
        print discarded
    for project in projects:
        print project, nb_classes_per_project[project]

def line(name, project, lowPMS=False):
    global gray
    prefix = "original/october-2017/"
    suffix = "_mutations.csv"
    t = count_mutant.countForTestClass(prefix + "/" + project + "/" + name + suffix)
    pms = profiling_test_class.print_line_3([t[0], t[1], name, project], gray)
    if pms:
        gray = not gray
    return pms

def computePMS(name, project):
    prefix = "original/october-2017/"
    suffix = "_mutations.csv"
    t = count_mutant.countForTestClass(prefix + "/" + project + "/" + name + suffix)
    return float(t[1]) / float(t[0]) * 100.0 if not float(t[0]) == 0.0 else 0.0



if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = [ "javapoet",
                     "mybatis",
                     "traccar",
                     "stream-lib",
                     "mustache.java",
                     "twilio-java",
                     "jsoup",
                     "protostuff",
                     "logback",
                     "retrofit"
        ]

    run(projects=projects)
