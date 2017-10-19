import profiling_test_class
import sys
import read_select_classes
import count_mutant

gray = False

def run(projects):
    PMS = []
    for project in projects:
        top, worst = read_select_classes.read([project])
        PMS.append( ((computePMS(top[0], project)), top[0], project))
        PMS.append( ((computePMS(top[1], project)), top[1], project))
        PMS.append( ((computePMS(worst[0], project)), worst[0], project))
        PMS.append( ((computePMS(worst[1], project)), worst[1], project))
    for array in PMS:
        if array[0] > 50.0:
            line(array[1], array[2])
    print "\\hline"
    print "&& Low $PMS$\\\\"
    print "\\hline"
    for array in PMS:
        if array[0] <= 50.0:
            line(array[1], array[2])

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
