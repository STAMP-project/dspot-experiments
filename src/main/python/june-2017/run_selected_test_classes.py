import subprocess
import sys

import select_test_classes


def copyResult(project, suffix):
    return "cp -R " + project + "_" + suffix + "  ${HOME}/results/" + project + "_" + suffix

project = sys.argv[1]
path = sys.argv[2]

max1, max2, min1, min2, avg1, avg2 = select_test_classes.getCmd(project, path)

subprocess.call(max1, shell=True)
subprocess.call(copyResult(project, "max1"), shell=True)
subprocess.call(max2, shell=True)
subprocess.call(copyResult(project, "max2"), shell=True)
subprocess.call(min1, shell=True)
subprocess.call(copyResult(project, "min1"), shell=True)
subprocess.call(min2, shell=True)
subprocess.call(copyResult(project, "min2"), shell=True)
subprocess.call(avg1, shell=True)
subprocess.call(copyResult(project, "avg1"), shell=True)
subprocess.call(avg2, shell=True)
subprocess.call(copyResult(project, "avg2"), shell=True)
