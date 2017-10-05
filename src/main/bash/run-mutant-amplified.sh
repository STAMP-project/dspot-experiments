#!/usr/bin/env bash
project=javapoet
root_exp=${PWD}
python src/main/python/install.py ${project}
cd dataset/${project}/
mvn install -DskipTests
cd ${root_exp}
cp ${root_exp}/results/per_class2/javapoet/com/squareup/javapoet/AmplUtilTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.AmplUtilTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.AmplUtilTest
to_download_top_1=$(curl --upload-file com.squareup.javapoet.AmplUtilTest_mutations.csv https://transfer.sh/com.squareup.javapoet.AmplUtilTest_mutations.csv)
cp ${root_exp}/results/per_class2/javapoet/com/squareup/javapoet/AmplNameAllocatorTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.AmplNameAllocatorTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.AmplNameAllocatorTest
to_download_top_2=$(curl --upload-file com.squareup.javapoet.AmplNameAllocatorTest_mutations.csv https://transfer.sh/com.squareup.javapoet.AmplNameAllocatorTest_mutations.csv)
cp ${root_exp}/results/per_class2/javapoet/com/squareup/javapoet/AmplFieldSpecTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.AmplFieldSpecTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.AmplFieldSpecTest
to_download_worst_1=$(curl --upload-file com.squareup.javapoet.AmplFieldSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.AmplFieldSpecTest_mutations.csv)
cp ${root_exp}/results/per_class2/javapoet/com/squareup/javapoet/AmplParameterSpecTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
cd dataset/javapoet/
mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -DwithHistory -DoutputFormats=CSV -Dmutators=ALL -DtimeoutConst=20000 -DjvmArgs=16G  -DreportsDirectory=target/pitest-reports -DtargetClasses=com.squareup.javapoet.*  -DtargetTests=com.squareup.javapoet.AmplParameterSpecTest >> /dev/null
cd ${root_exp}
python src/main/python/copy_pit_results.py dataset/javapoet/ com.squareup.javapoet.AmplParameterSpecTest
to_download_worst_2=$(curl --upload-file com.squareup.javapoet.AmplParameterSpecTest_mutations.csv https://transfer.sh/com.squareup.javapoet.AmplParameterSpecTest_mutations.csv)
echo "curl ${to_download_top_1} -o com.squareup.javapoet.AmplUtilTest_mutations.csv"
echo "curl ${to_download_top_2} -o com.squareup.javapoet.AmplNameAllocatorTest_mutations.csv"
echo "curl ${to_download_worst_1} -o com.squareup.javapoet.AmplFieldSpecTest_mutations.csv"
echo "curl ${to_download_worst_2} -o com.squareup.javapoet.AmplParameterSpecTest_mutations.csv"
