import profiling_test_class
import sys

if __name__ == '__main__':

    if len(sys.argv) > 1:
        projects = sys.argv[1:]
    else:
        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup",
                    "protostuff", "logback"]#, "retrofit"]

        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
                    "logback", "retrofit"]

        projects = ["javapoet", "mybatis", "traccar", "stream-lib", "twilio-java", "jsoup", "protostuff", "logback"]



    top, worst = profiling_test_class.profile(projects)

    gray = False
    for t in top:
        profiling_test_class.print_line(t, gray)
        gray = not gray
    print "\\hline"
    for t in worst:
        profiling_test_class.print_line(t, gray)
        gray = not gray

