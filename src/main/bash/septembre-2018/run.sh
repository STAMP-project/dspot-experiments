#!/usr/bin/env bash

project=${1}
owner=${2}
goal=${3}

python src/main/python/september-2018/install.py ${owner} ${project}
./src/main/bash/find_candidates.sh ${project} ${owner} ${goal}
python src/main/python/september-2018/diff_coverage.py ${project} onClusty
python src/main/python/september-2018/run-pre-selected.py ${project} 0 ${goal} onClusty
python src/main/python/september-2018/run-pre-selected.py ${project} 0 ${goal} onClusty amplifiers
