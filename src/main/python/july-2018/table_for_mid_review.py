import profiling_test_class
import sys
import read_select_classes
import count_mutant
import numpy as np
import matplotlib.pyplot as plt

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

    killed_vector = []
    killed_ampl_vector= []
    name_vector = []
    for array in PMS:
        nb_classes_per_project[array[-1]] = (nb_classes_per_project[array[-1]][0] + 1, nb_classes_per_project[array[-1]][1])
        l = line(array[1], array[2])
        if not l == "":
            name_vector.append(l[0])
            killed_vector.append(l[1])
            killed_ampl_vector.append(l[2])
    print name_vector, killed_vector, killed_ampl_vector
    build_bar_chart(name_vector, killed_vector, killed_ampl_vector)

def build_bar_chart(names, killed, killed_ampl):
    ind = np.arange(len(killed))  # the x locations for the groups
    width = 0.35       # the width of the bars

    fig = plt.figure()
    ax = fig.add_subplot(111)
    rects1 = ax.bar(ind, killed, width, color='seagreen')

    rects2 = ax.bar(ind+width, killed_ampl, width, color='red')

    # add some
    ax.set_ylabel('Mutation Scores')
    ax.set_title('')
    ax.set_xticks(ind + width / 2)
    ax.set_xticklabels( names, size="xx-small",rotation="vertical")
    for tick in ax.get_xaxis().get_major_ticks():
        tick.set_pad(0.8)

    ax.legend( (rects1[0], rects2[0]), ('Original', 'Amplified') )

    plt.show()

def line(name, project, lowPMS=False):
    global gray
    prefix = "original/october-2017/"
    suffix = "_mutations.csv"
    t = count_mutant.countForTestClass(prefix + "/" + project + "/" + name + suffix)
    pms = profiling_test_class.print_line_5([t[0], t[1], name, project], gray)
    if not pms == "":
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
