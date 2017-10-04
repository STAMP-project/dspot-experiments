#!/usr/bin/env bash

project=javapoet

#
#   First install the target project
#

python src/main/python/install.py ${project}
cd dataset/${project}/
mvn install -DskipTests
cd ../..

#
#   Then, run mutation analysis, on the desired module if needed
#

root_exp=${PWD}
cd dataset/javapoet/
~/apache-maven-3.3.9/bin/mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.UtilTest
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.UtilTest
cd dataset/javapoet/
~/apache-maven-3.3.9/bin/mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.NameAllocatorTest
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.NameAllocatorTest
cd dataset/javapoet/
~/apache-maven-3.3.9/bin/mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.FieldSpecTest
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.FieldSpecTest
cd dataset/javapoet/
~/apache-maven-3.3.9/bin/mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.ParameterSpecTest
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.ParameterSpecTest
echo "curl --upload-file com.squareup.javapoet.UtilTest_mutations.csv https://transfer.sh/com.squareup.javapoet.UtilTest_mutations.csv"
echo "curl --upload-file com.squareup.javapoet.NameAllocatorTest_mutations.csv https://transfer.sh/com.squareup.javapoet.NameAllocatorTest_mutations.csv"
echo "curl --upload-file com.squareup.javapoet.FieldSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.FieldSpecTest_mutations.csv"
echo "curl --upload-file com.squareup.javapoet.ParameterSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.ParameterSpecTest_mutations.csv"

