# POC - Serverless, Quarkus Lambda Http, Spring, AWS Parameter Store (SSM)

Proof of concept aiming the integration of a *full fledged* Quarkus/Spring API with multiple endpoints running
from a single Lambda function, accessing AWS Parameter Store at the Systems Manager via SDK 2.15. The concept provides both JVM and native versions using the *native container build*,
so no local GraalVM installation is required.

## Contents
* [Introduction](#introduction)
* [Technology stack](#technology-stack)
* [Installation](#installation)
* [Set AWS service credentials](#set-aws-service-credentials)
* [Deploy and remove cloud stack](#deploy-and-remove-cloud-stack)
* [Testing the endpoints](#testing-the-endpoints)
* [Performance comparison](#performance-comparison)

## Introduction

The single most troublesome characteristic of cloud functions is the **cold start**, which adds uncomfortable
amounts of delay to the completion of a function's execution. After being dormant for longer periods
of time (usually around 5 minutes, depending on different factors) the function container shuts down 
and needs to be restarted. This feature manifests dramatically when writing cloud functions in 
*compiled languages*, such as Java or C#, making a single cold start worth over 10 or even 15 seconds 
of delay, rendering such solutions **impractical for latency sensitive applications**, such as APIs 
serving end users via mobile or web clients.

Another cumbersome aspect of cloud function programming, is the fact that in the standard approach of
writing APIs with them, each endpoint's verb ends up getting assigned to a single function, making 
such architecture **highly coupled to the infrastucture** and usually **less than standardized**.

The architectural attempt presented in this POC aims to approach these two topics through:
* **A single cloud function must serve the entire API**. This approach allows the usage of tried
  and tested frameworks, in this case *Spring*, in the form of Quarkus Spring Extensions, permiting 
  as such a standardized development pattern. This general concept would allow the API to be easily
  migrated to any other Spring compatible infra-structure, such as ECS or Elastic Beanstalk.
  

* **Usage of lightning fast application initialization solution.** Java/Spring applications were not 
  designed to have a fast startup procedure. Their original idea is to be instantiated once and 
  respond to requests while staying up. The [Quarkus](https://quarkus.io/) framework 
  supplies an incredibly fast boot time (usually under 0.5s) with a Spring "Façade" for a fraction 
  of the usual computational resources. It provides the startup agility one would need to mitigate the
  cold start issue and run a *smoothly auto-scaling* Java application on a cloud function.
  

The following instructions should allow the developer to build, execute and deploy a copy of this project locally and
on his AWS Account.


## Technology stack
* [Java JDK 1.8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) or [11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) - Required Java version. **1.8** will work, but **11** is preferred. 
* [Maven 3](https://maven.apache.org/) - Dependency and build management.
* [Lombok Plugin](https://projectlombok.org/) - Make sure your IDE supports **Lombok**.
* [IntelliJ IDEA](https://www.jetbrains.com/) - Or any IDE of your choice.
* [Git](https://git-scm.com/) - Versioning system.
* [NodeJS 12+](https://nodejs.org/en/download/) - Pre-requisite for the *Serverless Framework* and deployment scripts.
* [Serverless Framework](https://www.serverless.com/framework/docs/getting-started/) - Infra-structure as code.
* [Docker](https://www.docker.com/get-started) - Used to build native API version.

## Installation
Make sure you install and confirm the installation of the pre-required technologies if not already 
done *(Java JDK, Maven, IDE's Lombok Plugin, NodeJS 12+, Git)*. Details on how this should be done 
will not be covered on this doc. Execute from the command terminal:

1. Make a local copy of this repository.
    ```
    git clone https://github.com/RogerVFbr/serverless-quarkus-spring-lambda-ssm.git
    ```

2. Install the *Serverless Framework* if not already done.
    ```
    npm install -g serverless
    ```

3. From the project root, install the Node dependencies.
    ```
    npm install
    ```
4. Open the project in your preferred IDE and use your **pom.xml** to update the project's dependencies.

## Set AWS service credentials
Register your AWS credentials on the service's profile, as follows:

1. Obtain or generate the AWS IAM credentials to be used on this service and retrieve 
   it's **ACCESS KEY ID** and **SECRET ACCESS KEY**. If you're unsure on how to create an IAM user,
   [follow the AWS documentation here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html).
   You can also use your local credentials. You can check them by running from the terminal:
   ```
   # ON MAC & LINUX
   nano ~/.aws/credentials
   
   # ON WINDOWS
   notepad C:\Users\<YOUR_WINDOWS_USER_NAME>\.aws\credentials
    ```

2. Generate a local profile with the acquired credentials by running the 
   command below from the project's root folder. *Pay close attention to the syntax with double 
   dashes and spacing*. Replace **<ACCESS_KEY_ID>** and **<SECRET_ACCESS_KEY>** by your aqcuired 
   credentials.

    ```
    npm run set-service-credentials -- --key <ACCESS_KEY_ID> --secret <SECRET_ACCESS_KEY>
    ```

## Deploy and remove cloud stack
For simplicity and cross-platform availability, the command sequences were encapsulated on 
*package.json*'s scripts section. You can refer to it for further details. As a suggested 
workflow, proceed as follows:
* Deploy the project once to update the infrastructure.
* Work/Debug locally.
* When ready, deploy the project again, test remotely.

### Deploy stack to AWS
To deploy the service stack, execute from the project's root folder:
```
# LINUX & MAC
npm run deploy

# WINDOWS
npm run deploy-win
```
On Mac/Linux, the process will also attempt to set the terminal Java version to 11 before building. Full deployment
time including build (JVM/Native) and actual cloud deployment might be between 3-4 minutes.

### Running locally
To run the API locally, execute from the project's root:
```
# ALL PLATFORMS
npm run start-local
```
The stack must be deployed at least once before running locally. The script will parse the 
environment variables located in the **serverless.yml** definition and provide
them at runtime.

### Remove stack
To completely remove the stack from your AWS account, execute from the project's root:
```
# ALL PLATFORMS
npm run remove
```

## Testing the endpoints
After deploying the stack to AWS, the *Serverless Framework* will provide the endpoint base URLs.
Access them with CURL to test the newly deployed API. If running locally, use **http://localhost:8080/**
as base URL.

To **put a new parameter** on the parameter store, run the following command on the terminal:
```
# JVM ENDPOINT
curl -v -d '{"name": "jvm-parameter", "description": "Param description.", "value": "Param value."}' -H 'Content-Type: application/json' <REPLACE_BY_THE_BASE_URL>/dev/jvm/ssm

# NATIVE ENDPOINT
curl -v -d '{"name": "native-parameter", "description": "Param description.", "value": "Param value."}' -H 'Content-Type: application/json' <REPLACE_BY_THE_BASE_URL>/dev/native/ssm

# LOCAL
curl -v -d '{"name": "local-parameter", "description": "Param description.", "value": "Param value."}' -H 'Content-Type: application/json' http://localhost:8080/ssm
```
   **Note for Windows users.** CURL notation on Windows terminals replaces single quotes by double 
   quotes and inner double quotes must be escaped by a backslash: "{\"keyName\": \"value\"}"

To **get the parameter value by name**, run the following command on the terminal:
```
# JVM ENDPOINT
curl -v GET <REPLACE_BY_THE_BASE_URL>/dev/jvm/ssm?name=jvm-parameter

# NATIVE ENDPOINT
curl -v GET <REPLACE_BY_THE_BASE_URL>/dev/native/ssm?name=native-parameter

# LOCAL
curl -v GET http://localhost:8080/ssm?name=local-parameter
```

To **delete the parameter value by name**, run the following command on the terminal:
```
# JVM ENDPOINT
curl -v -X DELETE <REPLACE_BY_THE_BASE_URL>/dev/jvm/ssm?name=jvm-parameter

# NATIVE ENDPOINT
curl -v -X DELETE <REPLACE_BY_THE_BASE_URL>/dev/native/ssm?name=native-parameter

# LOCAL
curl -v -X DELETE http://localhost:8080/ssm?name=local-parameter
```

if you need to double check the endpoints' base URLs at AWS, run from the project root:
```
sls info --aws-profile quarkus_ssm
```

## Performance comparison:

### Local
![Local Log](img/local-log.png?raw=true)

### AWS, Native (Lambda logs)
![JVM Log](img/native-log.png?raw=true)

### AWS, JVM (Lambda logs)
![JVM Log](img/jvm-log.png?raw=true)


