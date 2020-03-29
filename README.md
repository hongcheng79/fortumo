# Fortumo Sample Spring Boot Application
The sample Spring Boot application will do numerical calculation (session based) from Spring MVC controller using POST method using request parameter called number.
The same parameter when the value is end will get the sum of of number for the same session.
The application also use Tomcat 9.x embedded web server.

## Stucture
The project is using Gradle as the build system and with the follow folder structure
* src (standard gradle project source and test folder)
* load-testing (contain loading testing script)
* scripts (bash script to start the app for docker)

## Code Structure
It has the following main code structure :-
* chc.fortumo.MainApplication (Spring Boot Application)
* chc.fortumo.MainApplicationConfig (Spring Configution)
* chc.fortumo.interceptor.CounterInterceptor (Handler Interceptor Adapter to keep CounterSessionData in session)
* chc.fortumo.controller.CounterSessionData (Session object to keep track of total)
* chc.fortumo.controller.ProcessController (Controller that computer the number)
* chc.fortumo.controller.HomeController (Root/home controller that return current timestamp, use for health check as well)

## Building
Issue the following command to build
`gradle build`

To clean
`gradle clean`

There is a version param called 'version' in standard gradle build.gradle.
This version param is important to build docker deployment later

`src/main/resources/application.properties` contain parameter specific to the embedded Tomcat, server.port (9090) should not be changed as being used in docker deployment 

## Unit Testing
`chc.fortumo.test.ApplicationContentLoadingTest` is to test Spring context loading.

`chc.fortumo.test.ProcessControllerTest` is the main testing class for chc.fortumo.controller.ProcessController
Instead of testing the controller directly, it use Apache Http Client to test the application which cover single and multi-thread environment. Each test cases will have a new session. 
`src/test/resources/application.properties` contain set of parameters that could be use during Unit Testing

Besides that we could use curl command to test the URL by

`curl -s -i -d number=5 -X POST http://localhost:9090/process/compute`
this will return
```
HTTP/1.1 200 
Set-Cookie: JSESSIONID=896F4764AE6AA6E3BF6B2DB2FA1C17DB; Max-Age=60; Expires=Sun, 29-Mar-2020 11:55:54 GMT; Path=/; HttpOnly
Content-Type: text/plain;charset=UTF-8## Docker Deployment
Content-Length: 0
Date: Sun, 29 Mar 2020 11:54:54 GMT## AWS Deployment
```

Noticed the JSESSIONID, we need to use it for subsequent curl call

`curl -s -i --cookie 'JSESSIONID=DA0145DA4181290ED3DE4A668D8E2BAD' -d number=10 -X POST http://localhost:9090/process/compute`

Finally end it with to get the total

`curl -s -i --cookie 'JSESSIONID=896F4764AE6AA6E3BF6B2DB2FA1C17DB' -d number=end -X POST http://localhost:9090/process/compute`

## Deployment - Docker
We will be using `Dockerfile`, `docker-compose.yml` and `scripts` folder for the docker deployment.

Before we can build using docker-compose, we need to issue `gradle build` to build the latest jar file which located in `build/libs`

Docker build has an argument called APP_VERSION which need to be same from version in `build.gradle`.
Issue the command command to build

`docker-compose build --build-arg APP_VERSION="0.0.1" --force-rm`

Once that is done, we can issue the following command to test it

`docker-compose up`

## Deployment - AWS Beanstalk
We can do a docker deployment in AWS stacks using `Dockerrun.aws.json` with the assumption we will be using ECR as the private repository. Issue the following command to login

`$(aws ecr get-login --no-include-email --region ap-northeast-1)`

Note that you might have a different region

Once we do a docker build `docker-compose build --build-arg APP_VERSION="0.0.1" --force-rm` we can tag the local image to the ECR private repository by

`docker tag fortumo_app:latest 413535710414.dkr.ecr.ap-northeast-1.amazonaws.com/chclab:fortumo_app`

Finally we push it

`docker push 413535710414.dkr.ecr.ap-northeast-1.amazonaws.com/chclab:fortumo_app`

Before we can use it in Beanstalk with "Multi-container Docker" configuration
<p align="center">
  <img src="https://github.com/hongcheng79/fortumo/blob/master/AWS-Beanstalk-Config.png" width="500">
</p>

## Load Testing
We are using artillery.io for load testing. Refer to https://artillery.io/docs/ for getting started and install Artillery as global package

There are two load testing yml files in load-testing
* load.yml (use for load testing)
* load-aws.yml (use for AWS load testing) 

Run the following command for local testing

`artillery run load.yml`

Run the following command for AWS testing and generate report

`artillery run -o report-aws.json load-aws.yml`

Run the following command to convert the json report to html report

`artillery report report-aws.json`