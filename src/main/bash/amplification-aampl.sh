#!/usr/bin/env bash
project=mybatis
root_exp=${PWD}
python src/main/python/october-2017/install.py ${project}
cd dataset/${project}/
mvn install -DskipTests
cd ${root_exp}
    java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23  --amplifiers TestDataMutator:StatementAdd     --test org.apache.ibatis.jdbc.SqlBuilderTest --path-pit-result original/october-2017/mybatis/org.apache.ibatis.jdbc.SqlBuilderTest_mutations.csv

    java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23  --amplifiers TestDataMutator:StatementAdd     --test org.apache.ibatis.jdbc.SelectBuilderTest --path-pit-result original/october-2017/mybatis/org.apache.ibatis.jdbc.SelectBuilderTest_mutations.csv

    java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23  --amplifiers TestDataMutator:StatementAdd     --test org.apache.ibatis.binding.WrongNamespacesTest --path-pit-result original/october-2017/mybatis/org.apache.ibatis.binding.WrongNamespacesTest_mutations.csv

    java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23  --amplifiers TestDataMutator:StatementAdd     --test org.apache.ibatis.binding.WrongMapperTest --path-pit-result original/october-2017/mybatis/org.apache.ibatis.binding.WrongMapperTest_mutations.csv

cp -r dspot-report/* results/october-2017/mybatis/