#!/bin/bash

if [ -z "$WILDFLY_HOME" ]; then
. ./configuration/properties_prod.properties;

export jboss_admin_user=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_password=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_group_user=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_group_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_group_password=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_group_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_customer_group_user=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_customer_group_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_customer_group_password=$(java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_customer_group_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

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
else
. $WILDFLY_HOME/scripts/properties.properties;

export jboss_admin_user=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_password=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_group_user=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_group_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_admin_group_password=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_admin_group_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_customer_group_user=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_customer_group_user" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export jboss_customer_group_password=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$jboss_customer_group_password" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export ROOT_USER=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_user_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export ROOT_PASSWORD=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_password_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export OB_USER=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_obuser_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')

export OB_PASSWORD=$(java -cp $WILDFLY_HOME/scripts/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar \
org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI \
input="$openbank_datasource_obpassword_encrypted" password=$MASTER_KEY algorithm=PBEWithMD5AndDES | tail -n3 | awk 'NF')
fi

$WILDFLY_HOME/bin/add-user.sh --silent $jboss_admin_user $jboss_admin_password || true
$WILDFLY_HOME/bin/add-user.sh -a -g admin --silent $jboss_admin_group_user $jboss_admin_group_password || true
$WILDFLY_HOME/bin/add-user.sh -a -g customer --silent $jboss_customer_group_user $jboss_customer_group_password || true

cp $WILDFLY_HOME/scripts/standalone.xml $WILDFLY_HOME/standalone/configuration/standalone.xml || true
sed -i 's/<user-name>hidden<\/user-name>/<user-name>'"$ROOT_USER"'<\/user-name>/' \
$WILDFLY_HOME/standalone/configuration/standalone.xml || true
sed -i 's/<password>hidden<\/password>/<password>'"$ROOT_PASSWORD"'<\/password>/' \
$WILDFLY_HOME/standalone/configuration/standalone.xml || true