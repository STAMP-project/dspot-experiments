#!/usr/bin/env bash
project=javapoet
root_exp=${PWD}
python src/main/python/february-2018/install.py ${project}
cd dataset/javapoet
~/apache-maven-3.3.9/bin/mvn install -DskipTests
cd ${root_exp}
cp ${root_exp}/results/february-2018/javapoet_aampl/com/squareup/javapoet/AmplTypeNameTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
~/jdk1.8.0_121/bin/java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties com.squareup.javapoet.AmplTypeNameTest
python src/main/python/february-2018/copy_pit_results.py dataset/javapoet/ results/february-2018/javapoet_aampl/com.squareup.javapoet.AmplTypeNameTest

cp ${root_exp}/results/february-2018/javapoet_aampl/com/squareup/javapoet/AmplNameAllocatorTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
~/jdk1.8.0_121/bin/java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties com.squareup.javapoet.AmplNameAllocatorTest
python src/main/python/february-2018/copy_pit_results.py dataset/javapoet/ results/february-2018/javapoet_aampl/com.squareup.javapoet.AmplNameAllocatorTest

cp ${root_exp}/results/february-2018/javapoet_aampl/com/squareup/javapoet/AmplFieldSpecTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
~/jdk1.8.0_121/bin/java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties com.squareup.javapoet.AmplFieldSpecTest
python src/main/python/february-2018/copy_pit_results.py dataset/javapoet/ results/february-2018/javapoet_aampl/com.squareup.javapoet.AmplFieldSpecTest

cp ${root_exp}/results/february-2018/javapoet_aampl/com/squareup/javapoet/AmplParameterSpecTest.java ${root_exp}/dataset/javapoet/src/test/java/com/squareup/javapoet/
~/jdk1.8.0_121/bin/java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties com.squareup.javapoet.AmplParameterSpecTest
python src/main/python/february-2018/copy_pit_results.py dataset/javapoet/ results/february-2018/javapoet_aampl/com.squareup.javapoet.AmplParameterSpecTest

