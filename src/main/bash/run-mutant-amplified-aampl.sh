#!/usr/bin/env bash
project=mybatis
root_exp=${PWD}
python src/main/python/october-2017/install.py ${project}
cd dataset/mybatis
mvn install -DskipTests
cd ${root_exp}

cp ${root_exp}/results/october-2017/mybatis/org/apache/ibatis/binding/AmplWrongNamespacesTest.java ${root_exp}/dataset/mybatis/src/test/java/org/apache/ibatis/binding/
java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties org.apache.ibatis.binding.AmplWrongNamespacesTest
python src/main/python/october-2017/copy_pit_results.py dataset/mybatis/ results/october-2017/mybatis/org.apache.ibatis.binding.AmplWrongNamespacesTest

cp ${root_exp}/results/october-2017/mybatis/org/apache/ibatis/binding/AmplWrongMapperTest.java ${root_exp}/dataset/mybatis/src/test/java/org/apache/ibatis/binding/
java -cp target/dspot-experiment-1.0.0-jar-with-dependencies.jar fr.inria.stamp.RunPitMutationAnalysis src/main/resources/${project}.properties org.apache.ibatis.binding.AmplWrongMapperTest
python src/main/python/october-2017/copy_pit_results.py dataset/mybatis/ results/october-2017/mybatis/org.apache.ibatis.binding.AmplWrongMapperTest

