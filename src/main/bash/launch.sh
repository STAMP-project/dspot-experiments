#!/usr/bin/env bash

project={$1}

./src/main/bash/run-mutant-original.sh
cp *.csv original/october-2017/${project}/
./src/main/bash/amplification.sh
cp -r dspot-report/ results/october-2017/${project}/
./src/main/bash/run-mutant-amplified.sh
cp *Ampl*.csv results/october-2017/${project}/