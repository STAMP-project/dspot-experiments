#!/usr/bin/env bash

echo "mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -Dmutators=ALL -DoutputFormats=CSV,HTML -DwithHistory -DtargetClasses=${2}.* -DtargetTests=${2}.*"

cd dataset/${1} &&
    mvn clean test -DskipTests org.pitest:pitest-maven:mutationCoverage -Dmutators=ALL -DoutputFormats=CSV,HTML -DwithHistory -DtargetClasses=${2}.* -DtargetTests=${2}.*