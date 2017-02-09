# Dspot-experiments [![Build Status](https://travis-ci.org/STAMP-project/dspot-experiments.svg?branch=master)](https://travis-ci.org/STAMP-project/dspot-experiments)

This repository is used to run experiments of [dspot](http://github.com/STAMP-project/dspot)

## Running

DSpot-experiments is using the main of DSpot. See [dspot](http://github.com/STAMP-project/dspot).

## Available Python script
 
* install.py: will clone and checkout on the specific commit ID the given repositories (look into dataset/dataset.json)
* best_fitness.py: will compute from json file test cases per class with the best fitness. We call best fitness the ratio nbMutantKilled / (NbInputAdded + nbAssertionAdded)
* top_killer.py: will compute from json file test cases per class with the highest mutation score.

## Folders

### Dataset

The folder `dataset` contains a json file which contains github url and commid id of the targeted files.

### original 

The folder `original` contains the results of the original test suite for pit test with ALL mutation operators enables

### results

The folder `results` contains results of the amplification of each project, in separate subfolder. Results are composed of one json file and one txt file per test Class and all generated test classes.
 
 