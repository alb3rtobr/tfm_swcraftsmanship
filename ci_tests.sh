#!/bin/bash

dirs=('restapi' 'stockchecker')

for dir in ${dirs[@]}; do
  cd $dir
  mvn test
  cd ..
done
