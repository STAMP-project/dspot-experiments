import count_original_mutant_per_class
from os import walk
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.cm as cm
from matplotlib.font_manager import FontProperties
from matplotlib.backends.backend_pdf import PdfPages


def buildScatters(projects):
    indexState = 5

    colors_array = cm.rainbow(np.linspace(0, 1, len(projects)))
    markers = ['o', 's', '+', 'x', 'D', '>', '*', '^', '<', '1', ]

    prefix = "original/per_class/"

    z = 0
    scatters = []

    for project in projects:
        xaxis = []
        yaxis = []
        colors = []
        z += 1
        for (dirpath, dirnames, filenames) in walk(prefix + project):
            if filenames:
                for filename in filenames:
                    total , killed = count_original_mutant_per_class.countForTestClass(prefix + project + "/" + filename)
                    if total > 0:#skipping abstract test class that can not kill
                        xaxis.append(total)
                        yaxis.append(float(float(killed) / float(total) if total > 0 else 0) * 100)
                        colors.append(colors_array[projects.index(project)])
        scatters.append((xaxis, yaxis, colors, project, markers[projects.index(project)]))
    return scatters

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]
projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback",
            "retrofit"]

scatters = buildScatters(projects)

fig = plt.figure()
ax = plt.gca()
max_xaxis = -1
for (xaxis, yaxis, colors, project, marker) in scatters:
    ax.scatter(xaxis, yaxis, c=colors, s=60, label=project, marker=marker)
    if max(xaxis) > max_xaxis :
        max_xaxis  = max(xaxis)

plt.xlabel('covered (absolute nb)')
plt.ylabel('score (%)')
plt.axis([-50, max_xaxis + 50, -10, 110])
fontP = FontProperties()
fontP.set_size('small')
plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
           ncol=5, fancybox=True, shadow=True, prop=fontP)
plt.title("test suites\' power map")
fig.savefig("original/power_map_all.pdf", bbox_inches='tight')
fig.savefig("original/power_map_all.jpeg", bbox_inches='tight')
