#!/bin/bash

dirs=('restapi' 'stockchecker')

cd proto-idls
mvn clean install
cd ..

for dir in ${dirs[@]}; do
  cd $dir
  mvn test
  cd ..
done
