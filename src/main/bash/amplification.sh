#!/usr/bin/env bash



#
#   Run amplification
#

project=javapoet
root_exp=${PWD}
python src/main/python/install.py ${project}
cd dataset/${project}/
bin/mvn install -DskipTests
cd ${root_exp}
java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --test com.squareup.javapoet.NameAllocatorTest --path-pit-result original/per_class2/javapoet/com.squareup.javapoet.NameAllocatorTest_mutations.csv
java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --test com.squareup.javapoet.FieldSpecTest --path-pit-result original/per_class2/javapoet/com.squareup.javapoet.FieldSpecTest_mutations.csv
java -Xms8G -Xmx16G -jar ../dspot/target/dspot-1.0.0-jar-with-dependencies.jar     --path-to-properties src/main/resources/${project}.properties     --iteration 3     --amplifiers TestDataMutator:StatementAdd     --test-criterion PitMutantScoreSelector     --verbose     --output-path dspot-report     --randomSeed 23   --test com.squareup.javapoet.ParameterSpecTest --path-pit-result original/per_class2/javapoet/com.squareup.javapoet.ParameterSpecTest_mutations.csv
zip -r dspot-report.zip dspot-report
to_download=$(curl --upload-file dspot-report.zip https://transfer.sh/javapoet_dspot-report.zip)
echo "curl ${to_download} -o javapoet_dspot-report.zip"