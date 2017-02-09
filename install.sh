#!/usr/bin/env bash

cd ..

git clone http://github.com/STAMP-project/dspot.git
cd dspot
chmod +x install.sh
./install.sh
mvn clean install -DskipTests

cd ../dspot-experiments