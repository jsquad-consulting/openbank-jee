#!/bin/bash

if [ -z "$WILDFLY_HOME" ]; then
. ./configuration/properties_local.properties;
else
. $WILDFLY_HOME/scripts/properties.properties;
fi

export ROOT_USER=$openbank_datasource_user_encrypted

export ROOT_PASSWORD=$openbank_datasource_password_encrypted

export OB_USER=$openbank_datasource_obuser_encrypted

export OB_PASSWORD=$openbank_datasource_obpassword_encrypted

export JBOSS_ROOT_USER=$jboss_admin_group_user
export JBOSS_ROOT_PASSWORD=$jboss_admin_group_password

export WEBSERVICE_WSDL_URL="http://localhost:8080/soap-webservice/GetClientService?wsdl"
export WEBSERVICE_QURL="http://jsquad.se/"
export WEBSERVICE_QSERVICE="GetClientWSService"

$WILDFLY_HOME/bin/add-user.sh --silent $jboss_admin_user $jboss_admin_password || true
$WILDFLY_HOME/bin/add-user.sh -a -g admin --silent $jboss_admin_group_user $jboss_admin_group_password || true
$WILDFLY_HOME/bin/add-user.sh -a -g customer --silent $jboss_customer_group_user $jboss_customer_group_password || true

cp $WILDFLY_HOME/scripts/standalone.xml $WILDFLY_HOME/standalone/configuration/standalone.xml || true
sed -i 's/<user-name>hidden<\/user-name>/<user-name>'"$ROOT_USER"'<\/user-name>/' \
$WILDFLY_HOME/standalone/configuration/standalone.xml || true
sed -i 's/<password>hidden<\/password>/<password>'"$ROOT_PASSWORD"'<\/password>/' \
$WILDFLY_HOME/standalone/configuration/standalone.xml || true