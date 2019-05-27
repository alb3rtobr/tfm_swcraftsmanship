[![Build Status](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship.svg?branch=master)](https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship)

# Master Thesis: Microservice app development with Spring Cloud Kubernetes

1. [Introduction](#introduction)
	1. [Project description](#project-description)
	2. [Goals](#goals)
	3. [Motivation](#motivation)
2. [Theoretical framework](#theoretical-framework)
3. [State of the art](#state-of-the-art)
4. [Project development](#project-development)
	1. [Methodology](#methodology)
	2. [Analysis](#analysis)
	3. [Design](#design)
	4. [Implementation & tests](#implementation-and-tests)
	5. [Deployment](#deployment)
		1. [Installation](#installation)
		2. [Docker image preparation](#docker-image-preparation)
		3. [Helm dependencies](#helm-dependencies)
		4. [Application deployment](#application-deployment)
		5. [Delete the deployment](#delete-the-deployment)
5. [Results](#results)
6. [Conclusions and future work](#conclusions-and-future-work)
7. [References](#references)


## Introduction

### Project description
The current project aims to explore the development of a Kubernetes native application using Spring Cloud framework and study different alternatives for the problems identified.
We have implemented a simple application with the following requirements:
* REST API that expose the allowed operations
* Data Abstraction Layer service to access the data base via gRPC.
* Monitoring component who reacts to the operations performed in the data base.

Our application is a draft of a stock system that could be found on a shop or a warehouse. The application allows CRUD operations over generic items, create orders with items in stock, and it automatically performs order of new items to an external end point when the stock of an item is below a given threshold.


### Goals
* **Implement a cloud native application from scratch, offering a REST API.**

* **Allow communication between microservices using a message bus service.**

* **Implement a service offering its functionality via gRPC.**

* **Develop our application in an incremental way, having functional versions after each iteration.**

### Motivation
From the different topics we have covered during the Master, we found that Kubernetes and cloud applications were quite interesting. We also started working on cloud-related issues in our jobs, so we decided it would be very appropiate to explore this topic in our Master Thesis and take advantage of the learning opportunities we could find.

## Theoretical framework

As starting point to understand our project theoretical environment, it is necessary to talk about microservices. A simple introductory definition would say that "Microservices are small, autonomous services that work together."[[1](#1)]. A software project that follows a microservices architecture is design in such way that its functionality is divided into smaller, loosely coupled components called services. Each service can be treated as a separate application, running its own process or processes. Services needs to communicate with each other, and they use lightweight protocols, being REST the most used.

This architecture has different advantages: reinforces modularity, it improves the reutilization of code, as services can be used in more than one application. Also, the codification of each service can be paralelized, reducing the implementation time of the whole application. Each service can use different programming languages or framework, so we can chose the solution that fits most with the service functionality, without being attached to a specific language.

On the other hand, these architectures also introduces several problems or issues that have to be addressed for a correct implementation of a microservices application. Distributed systems introduce complexity: the more services the application has, the harder is to coordinate all of them. As Martin Fowler points out, "Microservice proponents like to point out that since each service is smaller it's easier to understand. But the danger is that complexity isn't eliminated, it's merely shifted around to the interconnections between services."[[2](#2)]. Communication between services is key, so interfaces has to be well design, and infrastructure has to guarantee the appropriate latency in the message interchange process. The communication issue can impacts also in the delay of the transactions to be performed in the application: a given operation could need the answer for a bunch of microservices to be considered as done. If this process is not fast enough it can lead to a poor user experience.


## State of the art

Microservice architectures is not a new paradigm, but it has exponential importance specially due to the wide adoption of technologies such as Kubernetes. First, Docker popularized the usage of containers for implementation, testing, and distribution of applications, which contributed to the design of microservice applications. As commented in previous chapter, coordination of microservices (containers) was an issue to solve, and Kubernetes was the Google's answer: "Kubernetes is an open source system for managing containerized applications across multiple hosts; providing basic mechanisms for deployment, maintenance, and scaling of applications."[[3](#3)]
The first version was released by Google in 2014. After that, Google donated the product to the Linux Foundation, which created the Cloud Native Computing Foundation setting Kubernetes as the main technology behind. Actually, Kubernetes is the most used container orchestration tool and could being consider the de facto standard.

For implementing our project we have use Minikube, a tool that allows to run Kubernetes locally on our laptops. This tool starts a minimum Kubernetes cluster, which fits perfectly for testing purposes or small applications.

We have already mentioned microservice communication is done through lightweight protocols. REST (Representational State Transfer) is the most used and we are using it too in this project. Services that follow this approach are called RESTful Web Services (RWS), and they provide an API to manipulate web resources.

In our project we are also using other communication framework, called gRPC (Google Remote Procedure Call). It allows a client to call methods on a server application located on a different host transparently, as it is was a local object in the same machine. After defining the interface of the service to be implemented, the implementation of that interface has to be stored in a host which will handle external client calls using a gRPC server.
The clients then can use a stub which provides the same interface, and it will be in charge to communicate with the gRPC server.
Server and client applications could be written in different languages, but they will communicate thanks to sharing the same interface.

![](https://grpc.io/img/landing-2.svg)

*gRPC server and clients. (gRPC official site)*

By default, gRPC uses `protocol buffers` as mechanism to serialize structured data. With the specification of how your the data will be structured, it is possible to automatically generate source code to write and read that from different data streams and using different languages. `Protocol buffers` uses `.proto` files to define messages in its interface definition language (IDL), representing your data. Our `proto` file is located in the `proto-idls` project in the source repository.

A third communication mechanism we are using in our project is Apache Kafka, an open source distributed streaming platform. Kafka allows services to publish and subscribe to stream of data, acting like a message queue. It can be used to build real-time streaming pipelines to collect data between different microservices. In our case, we implmented a produced-consumer model using Kafka as communication tool. Kafka works as a microservice in the Kubernetes cluster.

We also named the management of the different services as a drawback of microservices architectures. Helm is a package manager that is used to define and manage services that run on Kubernetes. Applications and its resources are defined using yaml files called Helm charts. Our application include its own Helm charts since the first version.

## Project development

### Methodology

Cloud technologies evolve so fast and there are so many alternatives available, that when we were defining the scope of this project we elaborated a huge list of "nice-to-have" features. We decided that the best approach to develop our application was following an incremental approach, and it would allow us to keep focus on the tasks we have to perform, and prioritize issues accordingly. As each phase of our application has to provide functionality, we forced us not to start to many issues at the same time, and focus on finishing the open ones. This approach has resulted to be very useful for us, because although it was clear that we were not going to finish all the items of our first list, we are releasing an application that at least can provide some functionality.

Trying to take advantage of all the learning opportunities during the development of this project, we decided to use two tools that are widely used in software development, but we are not familiar with: Slack & GitHub.

Before this project, we were used to work with GitHub as source code repository, but we wanted to go one step beyond and use it also as project management tool, using features as the issue tracker and the usage of pull requests for code review. This allowed us to get experience on its usage and get familiar with the common GitHub way of working.

Slack ("Searchable Log of All Conversation and Knowledge" [[4](#4)]) is a team collaboration tool, useful to coordinate distributed teams. We create our own Slack workspace, and it was our main communication mechanism during the project. In our workspace, we create a separate channel to talk about each component, so all the discussions, questions, issues... were properly organized.
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

We decided to have the following components/services:
* `Gateway` : handling cluster access.
* `Application server` : main logic of the application.
* `Monitor` : in charge of monitor the server activity, and send a notification to an external end point if a given condition is fulfilled.
* `Message bus` : communication mechanism used by the server to publish events, and used by the monitor to consume those events.
* `DAL` : data abstraction layer to isolate the business model from the persistence of the model itself.
* `Data base` : the persistence of the model.

At this phase, we draft our main use case as follows:
![base use case](./uml/analysis-usecase.png "analysis base use case")

### Design

After prioritize which technologies we were interested on, the architecture draft was completed to look as follows:

![architecture draft extended](./images/architecture-draft-extended.png "architecture draft")

The application is composed of the following services:
* `API gateway` : we used Kubernetes Ingress functionality as first approach.
* `restapi` : in charge of offering our application functionality via REST API.
* `dal` : using gRPC to access the model
* `stockchecker` : whenever an item is solved, if the remaining stock is less than a given threshold, it will raise a notification to a external REST end point.

### Implementation and tests

We have implemented our application on a incremental way.

#### Version 0.1

Main characteristics:
* Basic functionality of all the components
* Kafka setup
* Helm charts
* ConfigMaps
* Automatic test execution for every commit

In this first version of the application we setup the Github repository, and the different projects. The application can be started using Helm charts, and configured using configmaps. ConfigMaps are a Kubernetes utility that allows inject the containers configuration, splitting the services definition and the values used for configuration.

An important we solved was the Kafka configuration to communicate the `restapi` & `stockchecker` services. Thanks to Helm, the configuration of the Kafka cluster was very straightforward, but we spent quite some time with the setup of the both services to use Kafka.

At this stage of the application development, the model is very simple, containing just one entity, `Item`, which has an `id` and a `description` as attributes.

![Model of version v0.1](./uml/model-v01.png "Model of version v0.1")

 The `restapi` component offers CRUD operations for `Item`:
 * POST `api/v1/items` : create an item
 * GET `api/v1/items` : list all items
 * GET `api/v1/items/{id}` : get an item
 * PUT `api/v1/items/{id}` : update an item
 * DELETE `api/v1/items/{id}` : delete an item

![Example of creating an Item](./images/postman-1.png "Example of creating an Item")

*Screenshoot of Postman while creating an item*

Although our `stockchecker` is able to send external REST notifications, taking into account the return of time invested, we decided to configure it just to log the notifications. Otherwise it would force us to implement that external end point in our tests.

Finally, one of the features we thought that would be nice to have, was a continuous integration (CI) setup. Although this was not a priority due to the topic of the project, being this Master about Software Craftsmanship, we decided to give it a chance and check how far we could go without spending too much time. During the course we learnt there are several CI tools that could be integrated with Github projects. We selected one of them, Travis CI, to automatically run our tests when a commit is sent to our repository. The `.travis.yml` file contains the different stages we run for every commit. Our Travis dashboard can be found in `https://travis-ci.org/alb3rtobr/tfm_swcraftsmanship`.

#### Version 0.2

Main characteristics:
* Model extension to include more than one relation
* Ingress configuration

The second version included a significant change in the model:

![Model of version v0.2](./uml/model-v02.png "Model of version v0.2")

The `restapi` component offers the same operations than previous version for `Item` objects and new operations due to the new model:
* POST `api/v2/items` : create an item
* GET `api/v2/items` : list all items
* GET `api/v2/items/{id}` : get an item
* PUT `api/v2/items/{id}` : update an item
* DELETE `api/v2/items/{id}` : delete an item
* POST `api/v2/orders` : create a order (`DomainOrder`)
* GET `api/v2/orders` : list all orders
* GET `api/v2/orders/{id}` : list an orders
* PUT `api/v2/orders/{id}` : update an order
* DELETE `api/v2/orders/{id}` : delete an order

In this version it is possible to create orders of item

We also implemented a quick improvement for the API gateway. In previous version, access to `restapi` was performed using the `NodePort` option available in Kubernetes services. This automatically creates an IP which is accessed from outside the Kubernetes cluster, and together with the `NodePort` provides access to the `restapi` service. In version 0.2 we complemented this by configuring `Ingress`, a Kubernetes functionality that manages external access to the cluster services, and provides load balancing.

Before configuring ingress, it is necessary to configure an ingress controller. In our case, we used Nginx Ingress Controller. It can be installed using Helm:
```
$> helm install --name nginx-ingress stable/nginx-ingress
```
In case of using Minikube, as it was our case, it is necessary to enable ingress:
```
$> minikube addons enable ingress
```



#### Version 0.3

*Under development*

### Deployment

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

First it is needed to add the Helm Incubator repository:

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
Saving 2 charts
Downloading kafka from repo https://kubernetes-charts-incubator.storage.googleapis.com/
Downloading elastic-stack from repo https://kubernetes-charts.storage.googleapis.com/
Deleting outdated charts
```



##### Deployment

```bash
$ cd $GIT_REPO/charts
$ helm install --name=tfm-almacar tfm-almacar
```
The deployment is the following:

```
NAME:   tfm-almacar
LAST DEPLOYED: Tue May 28 00:17:22 2019
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/ClusterRoleBinding
NAME                    AGE
rbac-default-namespace  2s

==> v1/ConfigMap
NAME                           DATA  AGE
tfm-almacar-dal                1     2s
tfm-almacar-elasticsearch      4     2s
tfm-almacar-kibana             1     2s
tfm-almacar-kibana-test        1     2s
tfm-almacar-logstash-patterns  0     2s
tfm-almacar-logstash-pipeline  2     2s
tfm-almacar-restapi            1     2s
tfm-almacar-stockchecker       1     2s

==> v1/Deployment
NAME                      READY  UP-TO-DATE  AVAILABLE  AGE
tfm-almacar-dal           0/1    1           0          2s
tfm-almacar-mysql         0/1    1           0          2s
tfm-almacar-restapi       0/1    1           0          2s
tfm-almacar-stockchecker  0/1    1           0          2s

==> v1/PersistentVolume
NAME             CAPACITY  ACCESS MODES  RECLAIM POLICY  STATUS  CLAIM                   STORAGECLASS  REASON  AGE
mysql-pv-volume  1Gi       RWO           Retain          Bound   default/mysql-pv-claim  manual        2s

==> v1/PersistentVolumeClaim
NAME            STATUS  VOLUME           CAPACITY  ACCESS MODES  STORAGECLASS  AGE
mysql-pv-claim  Bound   mysql-pv-volume  1Gi       RWO           manual        2s

==> v1/Pod(related)
NAME                                              READY  STATUS             RESTARTS  AGE
tfm-almacar-dal-fdc745558-wgclm                   0/1    Init:0/1           0         2s
tfm-almacar-elasticsearch-client-9cddc5d7d-4m2sq  0/1    Init:0/1           0         2s
tfm-almacar-elasticsearch-client-9cddc5d7d-x9sxs  0/1    Init:0/1           0         2s
tfm-almacar-elasticsearch-data-0                  0/1    Pending            0         1s
tfm-almacar-elasticsearch-master-0                0/1    Pending            0         1s
tfm-almacar-kafka-0                               0/1    Pending            0         1s
tfm-almacar-kibana-7689c69d7c-5jjh7               0/1    Pending            0         2s
tfm-almacar-logstash-0                            0/1    Pending            0         1s
tfm-almacar-mysql-5fb5d7cd84-d5ntl                0/1    ContainerCreating  0         2s
tfm-almacar-restapi-6f9c98dfbc-jzzmn              0/1    Pending            0         2s
tfm-almacar-stockchecker-868564579d-5t7pd         0/1    Pending            0         2s
tfm-almacar-zookeeper-0                           0/1    Pending            0         1s

==> v1/Service
NAME                                 TYPE       CLUSTER-IP      EXTERNAL-IP  PORT(S)                     AGE
tfm-almacar-dal                      ClusterIP  10.104.172.47   <none>       50057/TCP                   2s
tfm-almacar-elasticsearch-client     ClusterIP  10.106.190.155  <none>       9200/TCP                    2s
tfm-almacar-elasticsearch-discovery  ClusterIP  None            <none>       9300/TCP                    2s
tfm-almacar-kafka                    ClusterIP  10.100.145.84   <none>       9092/TCP                    2s
tfm-almacar-kafka-headless           ClusterIP  None            <none>       9092/TCP                    2s
tfm-almacar-kibana                   ClusterIP  10.106.194.22   <none>       443/TCP                     2s
tfm-almacar-logstash                 ClusterIP  10.102.55.20    <none>       5044/TCP                    2s
tfm-almacar-mysql                    ClusterIP  10.110.79.5     <none>       3306/TCP                    2s
tfm-almacar-restapi                  NodePort   10.105.30.112   <none>       8080:31476/TCP              2s
tfm-almacar-zookeeper                ClusterIP  10.110.117.0    <none>       2181/TCP                    2s
tfm-almacar-zookeeper-headless       ClusterIP  None            <none>       2181/TCP,3888/TCP,2888/TCP  2s

==> v1/ServiceAccount
NAME                              SECRETS  AGE
tfm-almacar-elasticsearch-client  1        2s
tfm-almacar-elasticsearch-data    1        2s
tfm-almacar-elasticsearch-master  1        2s

==> v1beta1/Deployment
NAME                              READY  UP-TO-DATE  AVAILABLE  AGE
tfm-almacar-elasticsearch-client  0/2    2           0          2s
tfm-almacar-kibana                0/1    1           0          2s

==> v1beta1/Ingress
NAME                         HOSTS  ADDRESS  PORTS  AGE
tfm-almacar-restapi-ingress  *      80       1s

==> v1beta1/PodDisruptionBudget
NAME                   MIN AVAILABLE  MAX UNAVAILABLE  ALLOWED DISRUPTIONS  AGE
tfm-almacar-logstash   N/A            1                0                    2s
tfm-almacar-zookeeper  N/A            1                0                    2s

==> v1beta1/StatefulSet
NAME                              READY  AGE
tfm-almacar-elasticsearch-data    0/2    2s
tfm-almacar-elasticsearch-master  0/3    1s
tfm-almacar-kafka                 0/1    1s
tfm-almacar-zookeeper             0/1    1s

==> v1beta2/StatefulSet
NAME                  READY  AGE
tfm-almacar-logstash  0/1    1s

```

##### Delete the deployment

```bash
$ helm del --purge tfm-almacar
```

## Results

*TBD*

## Conclusions and future work

*TBD*

## References

* [1]: "Building Microservices", Sam Newman, O'Reilly Media
* [2]: [Microservice Trade-Offs](https://www.martinfowler.com/articles/microservice-trade-offs.html), Martin Fowler
* [3]: [Kubernetes Github repository](https://github.com/kubernetes/kubernetes)
* [3]: [Slack, the red hot $3.8 billion startup, has a hidden meaning behind its name"](https://www.businessinsider.com/where-did-slack-get-its-name-2016-9), Bussiness Insider

Reference sites:
* [Kubernetes](https://kubernetes.io/)
* [Spring Cloud](https://spring.io/projects/spring-cloud)
* [gRPC](https://grpc.io/)
* [Apache Kafka](https://kafka.apache.org/)

Spring Kafka related links:
* [Spring Kafka - Spring Boot example](https://codenotfound.com/spring-kafka-boot-example.html)
* [Spring Kafka Consumer-Producer example](https://codenotfound.com/spring-kafka-consumer-producer-example.html)
* [Spring Kafka - JSON Serializer Deserializer Example](https://codenotfound.com/spring-kafka-json-serializer-deserializer-example.html)
* [Spring Kafka Embedded Unit Test Example](https://codenotfound.com/spring-kafka-embedded-unit-test-example.html)
