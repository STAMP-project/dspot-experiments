#!/usr/bin/env bash

#
# install DSPOT
#

cd ..
git clone http://github.com/STAMP-project/dspot.git
cd dspot
~/apache-maven-3.3.9/bin/mvn clean package install -DskipTests
cd ../dspot-experiments