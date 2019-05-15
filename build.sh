#!/bin/bash

BASE_DIR=`pwd`

SERVICES="restapi stockchecker dal"

function build_protos() {
    echo "---------------------------------------------"
    echo " BUILDING proto-idls"
    echo "---------------------------------------------"
    cd ${BASE_DIR}/proto-idls
    mvn clean install || exit $?
}

function build_service() {
    service=$1
    echo "---------------------------------------------"
    echo " BUILDING $service"
    echo "---------------------------------------------"
    cd ${BASE_DIR}/${service}
    mvn clean package || exit $?
}

function build_docker_image() {
    service=$1
    echo "---------------------------------------------"
    echo " CREATING DOCKER IMAGE: almacar_${service}"
    echo "---------------------------------------------"
    cd ${BASE_DIR}/${service}
    docker build --tag=almacar_${service}:0.1 --rm=true . || exit $?
}

function delete_docker_image() {
    service=$1
    cd ${BASE_DIR}/${service}
    if [ `docker image ls | grep almacar_${service}:0.1 | wc -l` -ne 0 ];then
        echo "---------------------------------------------"
        echo " DELETING DOCKER IMAGE: almacar_${service}"
        echo "---------------------------------------------"
        docker rmi almacar_${service}:0.1 || exit $?
    fi
}

build_protos

for service in $SERVICES; do
    build_service $service
    delete_docker_image $service
    build_docker_image $service
done

