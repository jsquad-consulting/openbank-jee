# OpenBank application

INSTALLATION
------------
docker build -t openbank .

# Sometimes it's nessesary to clean up the images and containers to 
# clean up old caches and to free up hardisk space before installing
# openbank
docker rm -vf $(docker ps -a -q)

docker rmi -f $(docker images -a -q)

START THE DOCKER CONTAINER TO TEST RESTFUL/SOAP CONTRACTS
---------------------------------------------------------
docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank

TEST THE RESTful/SOAP CONTRACTS
-------------------------------
Load the schema/src/main/resources/rest.yaml file with the 
http://editor.swagger.io/ editor to easily test the RESTful 
contracts. Be sure to use basic authorization with --user <user:pass>
flag to use the RESTful operations successfully.

SOAP contracts (Service) is tested through 
http://localhost:8080/soap-webservice endpoint with SoapUI utility.

LOGON TO JBoss CONSOLE
----------------------
http://localhost:8080/console

ACCESS THE ANGULAR 7+ CLIENT
----------------------------
http://localhost:8080/client/openbank
