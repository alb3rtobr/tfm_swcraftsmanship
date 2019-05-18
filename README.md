[![Build Status](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship.svg?branch=master)](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship)

# tfm_swcraftsmanship

Our awesome TFM

## Architecture
![architecture draft](./images/architecture-draft.png "architecture draft")

## Installation

### Docker image preparations

`build.sh` script can be used to compile all the services and generate the Docker images.
When executed, the following steps are performed:
* Build `proto-idls` project
* Build `restapi` project & generate Docker image
* Build `stockchecker` project & generate Docker image
* Build `dal` project & generate Docker image

### Helm dependencies

The application chart has dependendecies in external Chart files for Kafka and Zookeeper services. It is needed, prior the application deployment, to update the helm dependencies in order to download the charts for these services.

First it is needed to install the Helm Incubator repository:

```bash
$ helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/
"incubator" has been added to your repositories
```

And update the dependencies:

```bash
$ cd $GIT_REPO/charts
$ helm dependency update tfm-almacar
Hang tight while we grab the latest from your chart repositories...
...Unable to get an update from the "local" chart repository (http://127.0.0.1:8879/charts):
	Get http://127.0.0.1:8879/charts/index.yaml: dial tcp 127.0.0.1:8879: connect: connection refused
...Successfully got an update from the "incubator" chart repository
...Successfully got an update from the "stable" chart repository
Update Complete. ⎈Happy Helming!⎈
Saving 1 charts
Downloading kafka from repo https://kubernetes-charts-incubator.storage.googleapis.com/
Deleting outdated charts
```



### Deployment

```bash
$ cd $GIT_REPO/charts
$ helm install --name=tfm-almacar tfm-almacar
```
The deployment is the following:

```
NAME                                            READY   STATUS    RESTARTS   AGE
pod/tfm-almacar-dal-84896976db-wc2cp            1/1     Running   2          23h
pod/tfm-almacar-kafka-0                         1/1     Running   3          23h
pod/tfm-almacar-mysql-fd97cb567-zbw8w           1/1     Running   0          23h
pod/tfm-almacar-restapi-bc6cfd455-2qhc8         1/1     Running   0          23h
pod/tfm-almacar-stockchecker-7ff8d66486-rlz6g   1/1     Running   4          23h
pod/tfm-almacar-zookeeper-0                     1/1     Running   0          23h
pod/tfm-almacar-zookeeper-1                     1/1     Running   0          23h
pod/tfm-almacar-zookeeper-2                     1/1     Running   0          23h

NAME                                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
service/kubernetes                       ClusterIP   10.96.0.1        <none>        443/TCP                      64d
service/tfm-almacar-dal                  ClusterIP   10.105.42.213    <none>        50057/TCP                    23h
service/tfm-almacar-kafka                ClusterIP   10.102.174.201   <none>        9092/TCP                     23h
service/tfm-almacar-kafka-headless       ClusterIP   None             <none>        9092/TCP                     23h
service/tfm-almacar-mysql                ClusterIP   10.101.230.4     <none>        3306/TCP                     23h
service/tfm-almacar-restapi              NodePort    10.100.175.116   <none>        8787:31034/TCP               23h
service/tfm-almacar-zookeeper            ClusterIP   10.100.139.119   <none>        2181/TCP                     23h
service/tfm-almacar-zookeeper-headless   ClusterIP   None             <none>        2181/TCP,3888/TCP,2888/TCP   23h

NAME                                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/tfm-almacar-dal            1/1     1            1           23h
deployment.apps/tfm-almacar-mysql          1/1     1            1           23h
deployment.apps/tfm-almacar-restapi        1/1     1            1           23h
deployment.apps/tfm-almacar-stockchecker   1/1     1            1           23h

NAME                                                  DESIRED   CURRENT   READY   AGE
replicaset.apps/tfm-almacar-dal-84896976db            1         1         1       23h
replicaset.apps/tfm-almacar-mysql-fd97cb567           1         1         1       23h
replicaset.apps/tfm-almacar-restapi-bc6cfd455         1         1         1       23h
replicaset.apps/tfm-almacar-stockchecker-7ff8d66486   1         1         1       23h

NAME                                     READY   AGE
statefulset.apps/tfm-almacar-kafka       1/1     23h
statefulset.apps/tfm-almacar-zookeeper   3/3     23h

```

### Delete the deployment

```bash
$ helm del --purge tfm-almacar
```
