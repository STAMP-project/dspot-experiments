import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import power_map

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]
projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback",
            "retrofit"]

scatters = power_map.buildScatters(projects)

for (xaxis, yaxis, colors, project, marker) in scatters:
    fig = plt.figure()
    ax = plt.gca()
    ax.scatter(xaxis, yaxis, c=colors, s=60, label=project, marker=marker)
    plt.xlabel('covered (absolute nb)')
    plt.ylabel('score (%)')
    fontP = FontProperties()
    fontP.set_size('small')
    plt.axis([-50, max(xaxis)  + 50, -10, 110])
    plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
               ncol=5, fancybox=True, shadow=True, prop=fontP)
    fig.savefig("original/power_map_"+ project + ".pdf", bbox_inches='tight')
    fig.savefig("original/power_map_"+ project + ".jpeg", bbox_inches='tight')
