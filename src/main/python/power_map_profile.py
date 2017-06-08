import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import profiling_test_class

projects = ["javapoet", "mybatis", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff",
            "logback", "retrofit"]
projects = ["javapoet", "traccar", "stream-lib", "mustache.java", "twilio-java", "jsoup", "protostuff", "logback",
            "retrofit"]
markers = ['o', 's', '+', 'x', 'D', '>', '*', '^', '<', '1', ]

top_killers, oracle_lacks, worst_killers = profiling_test_class.profile(projects=projects)

fig = plt.figure()
ax = plt.gca()
for top_killer in top_killers:
    ax.scatter(top_killer[0],
               top_killer[1],
               c='green',
               marker=markers[projects.index(top_killer[-1])],
               label=top_killer[-2],
               s=60
               )
for oracle_lack in oracle_lacks:
    ax.scatter(oracle_lack[0],
               oracle_lack[1],
               c='blue',
               marker=markers[projects.index(oracle_lack[-1])],
               label=oracle_lack[-2],
               s=60
               )
for worst_killer in worst_killers:
    ax.scatter(worst_killer[0],
               worst_killer[1],
               c='red',
               marker=markers[projects.index(worst_killer[-1])],
               label=worst_killer[-2],
               s=60
               )

plt.xlabel('covered (absolute nb)')
plt.ylabel('score (%)')
# plt.axis([-50, max_xaxis  + 50, -10, 110])
fontP = FontProperties()
fontP.set_size('small')
plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.1),
          ncol=5, fancybox=True, shadow=True, prop=fontP)
plt.title("test suites\' power map")
plt.show()
