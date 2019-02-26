#!/usr/bin/env bash



# Clone code2vec
git clone https://github.com/tech-srl/code2vec
cd code2vec
git reset --hard a7873314a1066c945048ef25f8f843e4c932c207

# Copy our configured script files
cd ..
cp src/main/bash/march-2019/preprocess.sh code2vec/preprocess.sh
cp src/main/bash/march-2019/train.sh code2vec/train.sh

# retrieve the raw-benchmark from Zenodo
cd code2vec/
wget https://zenodo.org/record/2567792/files/benchmark.zip
unzip benchmark.zip

wget https://zenodo.org/record/2577346/files/trained_model.tar
tar -xvf trained_model.tar

export LD_LIBRARY_PATH=/usr/local/cuda/lib64:$LD_LIBRARY_PATH

# preprocess
sh preprocess.sh

# train
sh train.sh
