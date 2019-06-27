# OpenBank
A demonstration of an simple bank application made with Java Enterprise Edition 8 platform (not JAKARTA EE) for the 
backend part and with Angular 7 for the frontend part.

## Table of contents
* [Requirements](#requirements)
* [Java EE 7+ Stack Related To Code](#java-ee-7-stack-related-to-code)
* [Installation](#installation)
* [Start OpenBank](#start-openbank)
* [Test RESTful/SOAP Contracts](#test-restfulsoap-contracts)
* [Restful Testing](#restful-testing)
* [SOAP Testing](#soap-testing)
* [Start The Client](#start-the-client)
* [Administrate The Wildfly Application Server](#administrate-wildly-application-server)

## Requirements
To be able to compile and run the OpenBank application the following dependicies are required:

To run application without Docker:

* Maven version 3.6.0 or newer
* JDK 11 or newer
* NodeJS version 10.15.3 or newer
* Angular CLI version 7.3.6 or newer
* Wildfly version 16.0.0 or newer

To run application with Docker:

* Docker Engine version 18.09.2 or later is recommended

## Java EE 7+ Stack related to code

### Context and Dependency Injection

#### Context

[Singleton Enterprise JavaBean](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/ejb/SystemStartupEjb.java#L35)

#### Dependency injection

[Injected Logger](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/ejb/OpenBankBusinessEJB.java#L34)

[Dependent Injected EntityManager](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/repository/EntityManagerProducer.java#L25)

#### Interceptor and Produces

[Logger Producer](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/producer/LoggerProducer.java)

[Interceptor Logger](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/interceptor/GenericLoggerInterceptor.java)

#### Scopes

[Dependent EntityManager](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/repository/EntityManagerProducer.java#L23)

### Bean Validation

#### Contraints and Validation

[ClientValidator](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/validator/ClientValidator.java)

### Java Persistence API (JPA)

#### Entity

[Client Entity](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java)

#### Quering entity

[Client](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L34)

#### Perstistence Unit

[Persistence Unit Definition](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/resources/META-INF/persistence.xml)

#### Persistence Context

[Persistence Context](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/repository/EntityManagerProducer.java#L25)

### Object-Relational Mapping

#### Attributes and Access Type Annotations

Annotations that can be applied to both field and method.

#### Table Annotation

[Table](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L33)

#### Primary Key Annotation

[Primary Key](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L40)

#### Column Annotation

[Column](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L42)

#### Enumerated Annotation

[Enumerated](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/AccountTransaction.java#L38)

#### Relational Mapping

[OneToOne](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L48)

[OneToMany](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Client.java#L51)

[ManyToOne](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Account.java#L50)

[ManyToMany](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/Person.java#L65)

#### Inheritance Mapping

[Single Table Inheritance](https://github.com/jsquad-consulting/openbank-jee/blame/master/persistence/src/main/java/se/jsquad/entity/ClientType.java#L34)

### Managing Persistence Objects

#### Obtaining an EntityManager
[EntityManager](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/repository/EntityManagerProducer.java#L26)

#### Save an Entity

[Persist](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/ejb/AccountTransactionEJB.java#L58)

#### JPQL and Named Queries

[Named Query](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/repository/ClientRepository.java#L33)

### Enterprise JavaBeans

#### Stateless EJB

[AccountTransactionEJB](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/ejb/AccountTransactionEJB.java#L30)

### Transactions (Required, Supports)
Enterprise JavaBeans are required by default.

#### Required transaction

[Transaction.Required](https://github.com/jsquad-consulting/openbank-jee/blame/master/rest/src/main/java/se/jsquad/ClientInformationRest.java#L58)

#### Supported transaction

[Transaction.Supports](https://github.com/jsquad-consulting/openbank-jee/blame/master/rest/src/main/java/se/jsquad/ClientInformationRest.java#L90)

### Messaging

#### Java Messaging Service API

[JMSContext as Producer](https://github.com/jsquad-consulting/openbank-jee/blame/master/business/src/main/java/se/jsquad/jms/MessageSenderSessionJMS.java#L44)

#### Message-Driven Bean as Asyncronous JMSContext Consumer

[Message-Driven Bean](business/src/main/java/se/jsquad/jms/MessageMDB.java)

### SOAP Web Service

[GetClientWS](soap/src/main/java/se/jsquad/GetClientWS.java)

### RESTful Web Service

[ClientInformationRest](rest/src/main/java/se/jsquad/ClientInformationRest.java)

### Dynamic Injection Point Interceptor

To be able to dynamically add Interceptor annotation to all Java CDI beans not included in the excluded list.

[DynamicInjectionPointInterceptor](business/src/main/java/se/jsquad/interceptor/DynamicInjectionPointInterceptor.java)

## Installation

### Without Docker
Be sure you have at least Maven version 3.6.0, JDK 11, NodeJS version 10.15.3, Angular CLI version 7.3.6 and Wildfly 
version 16.0.0 installed.

Execute the following commands in Bash (Linux & MacOS platforms):
```bash
npm rebuild node-sass # Be sure to rebind the node-sass package to right platform (MacOS, Windows or 
# Linux library dependicies
mvn clean install # For packaging the ear file

# Execute these wildfly commands for the first time only
$WILDFLY_HOME/bin/add-user.sh --silent admin admin1234 # setup admin super user for testing purpose
$WILDFLY_HOME/bin/add-user.sh -a -g admin --silent root root # setup admin group user with user 
# root and password root

$WILDFLY_HOME/bin/add-user.sh -a  -g customer --silent john doe # setup customer group user john 
# and password doe

# Copy the ./configuration/jboss/standalone.xml to the proper wildfly directory
cp ./configuration/jboss/standalone.xml $WILDFLY_HOME/wildfly/standalone/configuration/.

# Either deploy the ear file with your favourite IDE and configured Wildfly or deploy the ear file 
# and start the Wildfly application with the following commands:
cp ./ear/target/openbank-1.0-SNAPSHOT.ear $WILDFLY_HOME/wildfly/standalone/deployments/.
```

### Wìth Docker (Linux, MacOS & Windows)

```bash
docker build -t openbank .

# Sometimes it is nessesary to clean up images and containers to be able to run OpenBank because 
# of little memory space left and even hard disk space left, only execute this commands below if 
# command above gives errors.
docker rm -vf $(docker ps -a -q) # Clean up containers
docker rmi -f $(docker images -a -q) # Clean up all images
```

## Start OpenBank

### Without Docker

````bash
./$WILDFLY_HOME/usr/wildfly/bin/standalone.sh" -b 0.0.0.0 -bmanagement 0.0.0.0
````

### With Docker

````bash
docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank
````

## Test RESTful/SOAP contracts

### RESTful testing

Load the [rest.yaml](schema/src/main/resources/rest.yaml) file with the http://editor.swagger.io/
editor to easily test the RESTful contracts. Be sure to use basic authorization with 
--user <user:pass> flag to use the RESTful operations successfully.

Example:
````bash
curl --user john:doe -X GET "http://localhost:8080/restful-webservice/api/client/info/191212121212" \
-H  "accept: application/json"
````

### SOAP testing
SOAP contracts (Service) is tested through http://localhost:8080/soap-webservice endpoint with SoapUI 
utility ![GetClient][A1]

## Start the client

Goto http://localhost:8080/client/openbank and basic authorization is simplest to test through the Chrome browser.

## Administrate Wildly application server

http://localhost:8080/console


## Annex

[A1]: ./image/GetClientSoapContract.png "GetClient"
