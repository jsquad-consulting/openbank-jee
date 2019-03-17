# Bank project

INSTALLATION
------------
docker build -t openbank .

docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank

TEST THE RESTful CONTRACTS
--------------------------
Load the schema/src/main/resources/rest.yaml file with the 
http://editor.swagger.io/ editor to easily test the RESTful 
contracts.

LOGON TO SITE
-------------
http://0.0.0.0:8080/console
