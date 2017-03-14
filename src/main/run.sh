#!/usr/bin/env bash

# usage: ./run.sh <nameOfProject> , e.g. ./run.sh javapoet

project=${1}
echo ${project}

cp dspot-1.0.0-jar-with-dependencies.jar /tmp/
cd /tmp/

python src/main/python install.py ${project}

echo "java -Xms14G -Xmx24G -jar ../dspot-1.0.0-jar-with-dependencies.jar --project dspot-experiments/src/main/resources/${project}.properties --amplifiers MethodAdd:TestDataMutator:StatementAdderOnAssert --iteration 3 --output-path results/${project} --path-pit-result original/${project}/mutations.csv"
java -Xms14G -Xmx24G -jar ../dspot-1.0.0-jar-with-dependencies.jar --project dspot-experiments/src/main/resources/${project}.properties --amplifiers MethodAdd:TestDataMutator:StatementAdderOnAssert --iteration 3 --output-path results/${project} --path-pit-result original/${project}/mutations.csv

cp -R results/${project} ${HOME}/${project}