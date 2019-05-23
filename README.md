[![Build Status](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship.svg?branch=master)](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship)

# Microservice app development with Spring Cloud Kubernetes

1. [Introduction](#introduction)
	1. [Project description](#project-description)
	2. [Goals](#goals)
	3. [Motivation](#motivation)
2. [Project development](#project-development)
	1. [Methodology](#methodology)
	2. [Analysis](#analysis)
	3. [Design](#design)
	4. [Implementation & tests](#implementation-and-tests)
		1. Installation
		2. Docker image preparation
		3. Helm dependencies
		4. Deployment
		5. Delete the deployment
3. Conclusions and future work
4. References


## Introduction

### Project description
The current project aims to explore the development of a Kubernetes native application using Spring Cloud framework and study different alternatives for the problems identified.
We have implemented a simple application with the following requirements:
* REST API that expose the allowed operations
* Data Abstraction Layer service to access the data base via gRPC.
* Monitoring component who reacts to the operations performed in the data base.


### Goals


### Motivation


## Project development

### Methodology

Trying to take advantage of all the learning opportunities during the development of this project, we decided to use two tools that are widely used in software development, but we are not familiar with: Slack & GitHub.

Before this project, we were used to use GitHub as source coude repository, but we wanted to go one step beyond and use it also as project management tool, using features as the issue tracker and the usage of pull requests for code review. This allowed us to get experience on its usage and get familiar with the common GitHub way of working.

Slack (["Searchable Log of All Conversation and Knowledge"][1]) is a team collaboration tool, useful to coordinate distributed teams. We create our own Slack workspace, and it was our main communication mechanism during the project. In our workspace, we create a separate channel to talk about each component, so all the discussions, questions, issues... were properly organized.
We also took advantage of the different Slack plugins: we integrated both our GitHub repository and our continuous integration mechanism, so every activity generated in any of both platform was reported in its associated channel in Slack. This has proven to be very useful in a team which members are not working in the same physical place and not even at the same hours.

![slack and travis integration](./images/travis-in-slack.png)

*Travis CI reports in Slack.*

![slack and github integration](./images/github-in-slack.png "Slack and Github integration")

*Github activity reports in Slack.*

We defined a base architecture to be evolved. We added the required tasks as GitHub issues, and organized them in milestones.
When an issue was assigned, the developer worked on a separate branch, and once he was done, a pull request was opened in order to review the code before merging it to master branch.

### Analysis

This is the base architecture we decided to develop:

![architecture draft](./images/architecture-draft.png "architecture draft")

### Design

After prioritize which technologies we were interested on:

![architecture draft extended](./images/architecture-draft-extended.png "architecture draft")

### Implementation and tests

#### Installation

##### Docker image preparation

`build.sh` script can be used to compile all the services and generate the Docker images.
When executed, the following steps are performed:
* Build `proto-idls` project
* Build `restapi` project & generate Docker image
* Build `stockchecker` project & generate Docker image
* Build `dal` project & generate Docker image

##### Helm dependencies

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



##### Deployment

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

##### Delete the deployment

```bash
$ helm del --purge tfm-almacar
```

## References

Reference sites:
* [gRPC](https://grpc.io/)

Articles:
* [1]: https://www.businessinsider.com/where-did-slack-get-its-name-2016-9

Spring Kafka related links:
* [Spring Kafka - Spring Boot example](https://codenotfound.com/spring-kafka-boot-example.html)
* [Spring Kafka Consumer-Producer example](https://codenotfound.com/spring-kafka-consumer-producer-example.html)
* [Spring Kafka - JSON Serializer Deserializer Example](https://codenotfound.com/spring-kafka-json-serializer-deserializer-example.html)
* [Spring Kafka Embedded Unit Test Example](https://codenotfound.com/spring-kafka-embedded-unit-test-example.html)
