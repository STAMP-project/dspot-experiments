#!/usr/bin/env bash
project=javapoet
root_exp=${PWD}
python src/main/python/october-2017/install.py ${project}
cd dataset/${project}/
~/apache-maven-3.3.9/bin/mvn install -DskipTests
cd ${root_exp}
~/jdk1.8.0_121/bin/java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --maven-home ~/apache-maven-3.3.9/ --test com.squareup.javapoet.UtilTest --path-pit-result original/october-2017/javapoet/com.squareup.javapoet.UtilTest_mutations.csv
~/jdk1.8.0_121/bin/java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --maven-home ~/apache-maven-3.3.9/ --test com.squareup.javapoet.NameAllocatorTest --path-pit-result original/october-2017/javapoet/com.squareup.javapoet.NameAllocatorTest_mutations.csv
~/jdk1.8.0_121/bin/java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --maven-home ~/apache-maven-3.3.9/ --test com.squareup.javapoet.FieldSpecTest --path-pit-result original/october-2017/javapoet/com.squareup.javapoet.FieldSpecTest_mutations.csv
~/jdk1.8.0_121/bin/java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --maven-home ~/apache-maven-3.3.9/ --test com.squareup.javapoet.ParameterSpecTest --path-pit-result original/october-2017/javapoet/com.squareup.javapoet.ParameterSpecTest_mutations.csv
zip -r dspot-report.zip dspot-report
to_download=$(curl --upload-file dspot-report.zip https://transfer.sh/javapoet_dspot-report.zip)
echo "curl ${to_download} -o javapoet_dspot-report.zip"
