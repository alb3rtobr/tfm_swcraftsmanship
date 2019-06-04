#!/bin/bash

BASEDIR=`pwd`
CHARTNAME="tfm-almacar"

function usage() {
    echo "Usage:"
    echo "    -s | --start"
    echo "    -h | --halt | --stop"
}

function update_helm_dependecies() {
    cd ${BASEDIR}/charts
    find . -iname "*.tgz" | xargs rm
    helm dependency update ${CHARTNAME}
    cd ${BASEDIR}
}

function delete_prometheus_spillsovers() {
    echo "Deleting Prometheus spillsovers"
    kubectl delete crd prometheuses.monitoring.coreos.com
    kubectl delete crd prometheusrules.monitoring.coreos.com
    kubectl delete crd servicemonitors.monitoring.coreos.com
    kubectl delete crd alertmanagers.monitoring.coreos.com
}

function start_cluster() {
    cd ${BASEDIR}/charts
    helm install --name ${CHARTNAME} ${CHARTNAME}
    cd ${BASEDIR}
}

function stop_cluster() {
    helm delete --purge ${CHARTNAME}
    delete_prometheus_spillsovers
}

if [[ $# -ne 1 ]]; then
    echo "Bad options"
    usage
fi

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -s|--start)
    START="yes"
    shift # past argument
    ;;
    -h|--halt|--stop)
    STOP="yes"
    shift # past argument
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

if [[ ${START} == "yes" ]]; then
    echo "Starting cluster..."
    update_helm_dependecies
    start_cluster
fi

if [[ ${STOP} == "yes" ]]; then
    echo "Stopping cluster..."
    stop_cluster
fi
