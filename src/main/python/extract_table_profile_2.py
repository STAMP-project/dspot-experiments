import profiling_test_class
import sys
import read_select_classes
import count_mutant

gray = False

def run(projects):
    executed_increase = []
    killed_increase = []
    for project in projects:
        top, worst = read_select_classes.read([project])
        deltas = line(top[0], project)
        if deltas:
            killed_increase.append(deltas[1])
            executed_increase.append(deltas[1])
        deltas = line(top[1], project)
        if deltas:
            killed_increase.append(deltas[1])
            executed_increase.append(deltas[1])
    print "\\hline"
    print "& RQ3: Low $PMS$\\\\"
    print "\\hline"
    for project in projects:
        top, worst = read_select_classes.read([project])
        deltas = line(worst[0], project, True)
        if deltas:
            killed_increase.append(deltas[1])
            executed_increase.append(deltas[1])
        deltas = line(worst[1], project, True)
        if deltas:
            killed_increase.append(deltas[1])
            executed_increase.append(deltas[1])
    executed_increase.sort()
    killed_increase.sort()
    print "{0:.2f}".format(executed_increase[len(executed_increase) / 2]), "{0:.2f}".format(killed_increase[len(killed_increase) / 2])

def line(name, project, lowPMS=False):
    global gray
    prefix = "original/per_class"
    suffix = "_mutations.csv"
    t = count_mutant.countForTestClass(prefix + "/" + project + "/" + name + suffix)
    pms = profiling_test_class.print_line_2([t[0], t[1], name, project], gray,  lowPMS)
    gray = not gray
    return pms

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    run(projects=projects)
