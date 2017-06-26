import profiling_test_class
import sys

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback", "retrofit"]

    top, worst = profiling_test_class.profile(projects)

    deltas_killed = []

    gray = False
    for t in top:
        deltas_killed.append(profiling_test_class.print_line_2(t, gray))
        gray = not gray
    print "\\hline"
    for t in worst:
        deltas_killed.append(profiling_test_class.print_line_2(t, gray))
        gray = not gray

    nb_increase = 0
    for delta_killed in deltas_killed:
        if delta_killed > 0:
            nb_increase += 1
    print "nb_increase", nb_increase
    print "avg increase", sum(deltas_killed) / len(deltas_killed)