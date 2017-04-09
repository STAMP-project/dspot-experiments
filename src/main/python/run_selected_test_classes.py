import sys
import subprocess
import select_test_classes

def copyResult(project, suffix):
    return "cp -R " + project + "_" + suffix + "  ${HOME}/results/" + project + "_" + suffix

project = sys.argv[1]
print project

max1, max2, min1, min2, avg1, avg2 = select_test_classes.select(project)

subprocess.call(max1, shell=True)
subprocess.call(copyResult(project, "max1"), shell=True)
subprocess.call(max2, shell=True)
subprocess.call(copyResult(project, "max2"), shell=True)
subprocess.call(min1, shell=True)
subprocess.call(copyResult(project, "min1"), shell=True)
subprocess.call(min2, shell=True)
subprocess.call(copyResult(project, "min2"), shell=True)
subprocess.call(avg1, shell=True)
subprocess.call(copyResult(project, "avg&"), shell=True)
subprocess.call(avg2, shell=True)
subprocess.call(copyResult(project, "avg2"), shell=True)