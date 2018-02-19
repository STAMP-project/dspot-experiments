# Dspot-experiments [![Build Status](https://travis-ci.org/STAMP-project/dspot-experiments.svg?branch=master)](https://travis-ci.org/STAMP-project/dspot-experiments)

This repository contains the open-science experimental results of the evaluation of the [Dspot test amplification system](http://github.com/STAMP-project/dspot).

## Structure and Folders

### Dataset

The folder `dataset` contains a json file which contains the github url and commit identifiers of the targeted files.

### original 

The folder `original` contains the results of the mutation analysis of the original test using pit.

### results

The folder `results` contains the results of the amplification of each project, in separate subfolders. The results are composed of one json file and one txt file per test class, as well as all amplified test classes (eg [the one for javapoet's FieldSpecTest](https://github.com/STAMP-project/dspot-experiments/blob/master/results/october-2017/javapoet/com/squareup/javapoet/AmplFieldSpecTest.java)).
 

## Available Python script
 
* install.py: will clone and checkout on the specific commit ID the given repositories (look into dataset/dataset.json)
* best_fitness.py: will compute from json file test cases per class with the best fitness. We call best fitness the ratio nbMutantKilled / (NbInputAdded + nbAssertionAdded)
* top_killer.py: will compute from json file test cases per class with the highest mutation score.
