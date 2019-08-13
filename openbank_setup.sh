#!/bin/bash

export MASTER_KEY=SECRET_JSQUAD_AB_KEY # For demo purpose only, normally only stored at the CI/hidden environment

. ./configuration/properties.properties

export ROOT_USER=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_user_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export ROOT_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_password_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export OB_USER=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_obuser_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export OB_PASSWORD=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_obpassword_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

sed -i 's/<user-name>hidden<\/user-name>/<user-name>'"$ROOT_USER"'<\/user-name>/' configuration/jboss/standalone.xml
sed -i 's/<password>hidden<\/password>/<password>'"$ROOT_PASSWORD"'<\/password>/' configuration/jboss/standalone.xml