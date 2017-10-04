#!/usr/bin/env bash

#
# install DSPOT
#

cd ..
git clone http://github.com/STAMP-project/dspot.git
cd dspot
mvn clean package -DskipTests
cd ../dspot-experiments