#!/usr/bin/env bash

project=javapoet

python src/main/python/install.py ${project}
cd dataset/${project}/
mvn install -DskipTests
cd ../..

root_exp=${PWD}
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.UtilTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.UtilTest
to_download_top_1=$(curl --upload-file com.squareup.javapoet.UtilTest_mutations.csv https://transfer.sh/com.squareup.javapoet.UtilTest_mutations.csv)
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.NameAllocatorTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.NameAllocatorTest
to_download_top_2=$(curl --upload-file com.squareup.javapoet.NameAllocatorTest_mutations.csv https://transfer.sh/com.squareup.javapoet.NameAllocatorTest_mutations.csv)
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.FieldSpecTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.FieldSpecTest
to_download_worst_1=$(curl --upload-file com.squareup.javapoet.FieldSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.FieldSpecTest_mutations.csv)
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.ParameterSpecTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.ParameterSpecTest
to_download_worst_2=$(curl --upload-file com.squareup.javapoet.ParameterSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.ParameterSpecTest_mutations.csv)
echo "curl ${to_download_top_1} -o com.squareup.javapoet.UtilTest_mutations.csv"
echo "curl ${to_download_top_2} -o com.squareup.javapoet.NameAllocatorTest_mutations.csv"
echo "curl ${to_download_worst_1} -o com.squareup.javapoet.FieldSpecTest_mutations.csv"
echo "curl ${to_download_worst_2} -o com.squareup.javapoet.ParameterSpecTest_mutations.csv"
