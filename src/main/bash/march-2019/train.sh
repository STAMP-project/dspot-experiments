#!/usr/bin/env bash
###########################################################
# Change the following values to train a new model.
# type: the name of the new model, only affects the saved file name.
# dataset: the name of the dataset, as was preprocessed using preprocess.sh
# test_data: by default, points to the validation set, since this is the set that
#   will be evaluated after each training iteration. If you wish to test
#   on the final (held-out) test set, change 'val' to 'test'.

export LD_LIBRARY_PATH=/usr/local/cuda/lib64:$LD_LIBRARY_PATH

type=dspot-prettifier-exp-march-2019
dataset_name=dspot-prettifier-exp-march-2019
data_dir=data/${dataset_name}
data=${data_dir}/${dataset_name}
test_data=${data_dir}/${dataset_name}.val.c2v
model_dir=models/${type}

# already trained model java14m
trained_model=trained_model/saved_model_iter8

mkdir -p models/${model_dir}
set -e
#python3 -u code2vec.py --data ${data} --test ${test_data} --save ${model_dir}/saved_model
python3 -u code2vec.py --load ${trained_model} --data ${data} --test ${test_data} --save ${model_dir}/saved_model