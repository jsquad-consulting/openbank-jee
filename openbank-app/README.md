# OpenBank application

INSTALLATION
------------
docker build -t openbank .


START THE DOCKER CONTAINER TO TEST RESTFUL/SOAP CONTRACTS
---------------------------------------------------------
docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank

TEST THE RESTful/SOAP CONTRACTS
-------------------------------
Load the schema/src/main/resources/rest.yaml file with the 
http://editor.swagger.io/ editor to easily test the RESTful 
contracts. Be sure to use basic authorization with --user <user:pass>
flag to be use the RESTful operations.

SOAP contracts (Service) is tested through 
http://localhost:8080/soap-webservice endpoint with SoapUI utility.

LOGON TO SITE
-------------
http://0.0.0.0:8080/console
