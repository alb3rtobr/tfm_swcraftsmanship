#!/bin/bash

dirs=('stockchecker' 'restapi' 'dal')

cd proto-idls
mvn clean install
if [ $? -ne 0 ];then
  echo "Error while running 'mvn clean install' in proto-idls project."
  return 1
fi
cd ..


err=0
for dir in ${dirs[@]}; do
  cd ${dir}
  mvn test
  exit_code=$?
  if [ ${exit_code} -ne 0 ];then
    echo "Error while running 'mvn test' in ${dir} project."
    err=$((err + exit_code))
  fi
  cd ..
done

if [ ${err} -ne 0 ];then
  return 1
fi
