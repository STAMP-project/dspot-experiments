#!/usr/bin/env bash

mavenHome=${3}
javaHome=${4}
project=${1}
project_path=${2}
END=20
output=evosuite-tests

currentDir=$(echo ${PWD})
cd ${project_path}
${mavenHome}mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
cp=$(<cp.txt)
rm -f cp.txt
rm -rf evosuite-*
mvn clean test -DskipTests

for i in $(seq 1 ${END});
    do echo ${i};
     ${javaHome}java -jar ${currentDir}/evosuite-master-1.0.5-SNAPSHOT.jar -criterion mutation  -seed ${i} -projectCP ${cp} -target target/classes/
     cd ${currentDir}
     echo "${javaHome}java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.remover.FailingTestCasesRemover  ${project} ${output} ${i}"
     ${javaHome}java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.remover.FailingTestCasesRemover ${project} ${output} ${i}
done


