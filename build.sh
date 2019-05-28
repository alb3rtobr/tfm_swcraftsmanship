#!/bin/bash

BASE_DIR=`pwd`

SERVICES="restapi stockchecker dal"

function usage() {
    echo "Usage:"
    echo "    -h | --help"
    echo "    --no-test : Do not execute unit test"
    echo "    --no-docker : Do not generate docker images"
}

function build_protos() {
    echo "---------------------------------------------"
    echo " BUILDING proto-idls"
    echo "---------------------------------------------"
    cd ${BASE_DIR}/proto-idls
    mvn clean ${NOTEST} install || exit $?
}

function build_service() {
    service=$1
    echo "---------------------------------------------"
    echo " BUILDING $service"
    echo "---------------------------------------------"
    cd ${BASE_DIR}/${service}
    mvn clean ${NOTEST} package || exit $?
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

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -h | --help)
    usage
    exit 0
    ;;
    --no-test)
    NOTEST="-DskipTests"
    shift # past argument
    ;;
    --no-docker)
    DOCKER="no"
    shift # past argument
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

build_protos

for service in $SERVICES; do
    build_service $service
    if [[ ${DOCKER} != "no" ]]; then
        delete_docker_image $service
        build_docker_image $service
    fi
done

