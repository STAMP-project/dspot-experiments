#!/usr/bin/env bash

project=${1}
pathToSourceTestProject=${2}

testClasses=($(java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.ListJavaTestClasses ${pathToSourceTestProject} | sed 's/:.*//'))

echo ${project}
for testClass in "${testClasses[@]}"
do
    echo ${testClass}
    echo "java -jar target/dspot-experiment-1.0.0-jar-with-dependencies.jar -p ${project}.properties -a MethodAdd:TestDataMutator:StatementAdderOnAssert -i 3 -o results/${project} -m original/${project}/mutations.csv --descartes -t ${testClass}"
    java -jar target/dspot-experiment-1.0.0-jar-with-dependencies.jar -p src/main/resources/${project}.properties -a MethodAdd:TestDataMutator:StatementAdderOnAssert -i 3 -o results/${project} -m original/${project}/mutations.csv --descartes -t ${testClass}
done