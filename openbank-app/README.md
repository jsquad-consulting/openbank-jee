# Bank project

INSTALLATION
------------
docker build -t openbank .

docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank

LOGON TO SITE
-------------
http://0.0.0.0:8080/console
