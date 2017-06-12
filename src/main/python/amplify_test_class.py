import profiling_test_class
import subprocess
import sys

projects = sys.argv[1:]

cmd = "~/jdk1.8.0_121/jre/bin/java -Xms16G -Xmx32G" \
      " -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar" \
      " --path-to-properties src/main/resources/{}.properties" \
      " --amplifiers MethodAdd:TestDataMutator:StatementAdderOnAssert" \
      " --iteration 3" \
      " --output-path {}" \
      " --maven-home /home/spirals/danglot/apache-maven-3.3.9/" \
      " -t {}" \
      " -m original/per_class/{}/{}_mutations.csv"

for project in projects:
    top, worst = profiling_test_class.profile(projects=[project])
    print cmd.format(project, project, top[0][2], project, top[0][2])
    subprocess.call(cmd.format(project, project, top[0][2], project, top[0][2]),
                    shell=True)
    print cmd.format(project, project, top[1][2], project, top[1][2])
    subprocess.call(cmd.format(project, project, top[1][2], project, top[1][2]),
                    shell=True)
    print cmd.format(project, project, worst[0][2], project, worst[0][2])
    subprocess.call(cmd.format(project, project, worst[0][2], project, worst[0][2])
                    , shell=True)
    print cmd.format(project, project, worst[1][2], project, worst[1][2])
    subprocess.call(cmd.format(project, project, worst[0][2], project, worst[1][2]), shell=True)
