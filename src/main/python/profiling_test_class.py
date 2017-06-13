from os import walk

import count_mutant


def profile(projects):
    top = []
    worst = []
    prefix = "original/per_class/"

    blacklist = ["com.github.mustachejavabenchmarks.simple.SimpleBenchmarkTest_mutations.csv",
                 "io.protostuff.ProtostuffDelimiterTest_mutations.csv",
                 "org.traccar.protocol.ApelProtocolDecoderTest_mutations.csv"
                 ]

    for project in projects:
        results = []
        for (dirpath, dirnames, filenames) in walk(prefix + project):
            if filenames:
                for filename in filenames:
                    if filename not in blacklist:
                        print filename
                        total, killed = count_mutant.countForTestClass(prefix + project + "/" + filename)
                        if 0 < total <= 1000:
                            results.append((total, killed, filename.split('_')[0], project))
        sorted_results = sorted(results, key=lambda result: float(result[1]) / float(result[0]) * 100.0)
        worst.append(sorted_results[0])
        worst.append(sorted_results[1])
        top.append(sorted_results[-1])
        top.append(sorted_results[-2])

    return top, worst


if __name__ == '__main__':
    projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
                "logback", "retrofit"]
    top, worst = profile(projects)

    gray = False
    for line in worst:
        green = (float(line[1]) / float(line[0]) * 100.0) >= 50
        print ("\\rowcolor[HTML]{66FF66}" + "\n" if green else "") + \
              ("\\rowcolor[HTML]{EFEFEF}" + "\n" if not green and gray else "") + \
              line[-1] + " & " + line[-2] + " & " + str(line[0]) + " & " + str(line[1]) + " & " + "{0:.2f}".format(float(line[1]) / float(line[0]) * 100.0) + "\\\\"
        gray = not gray
    print "\\hline\\hline"
    gray = False
    for line in top:
        red = (float(line[1]) / float(line[0]) * 100.0) >= 50
        print ("\\rowcolor[HTML]{FF6666}" + "\n" if red else "") + \
              ("\\rowcolor[HTML]{EFEFEF}" + "\n" if not red and gray else "") + \
              line[-1] + " & " + line[-2] + " & " + str(line[0]) + " & " + str(line[1]) + " & " + "{0:.2f}".format(float(line[1]) / float(line[0]) * 100.0) + "\\\\"
        gray = not gray
