#!/usr/bin/env bash

project={$1}

export MAVEN_HOME=~/apache-maven-3.3.9/

./src/main/bash/run-mutant-original.sh
./src/main/bash/amplification.sh
./src/main/bash/run-mutant-amplified.sh
./src/main/bash/amplification-aampl.sh
./src/main/bash/run-mutant-amplified-aampl.sh