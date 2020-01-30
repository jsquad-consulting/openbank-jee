#
# Copyright 2020 JSquad AB
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM debian:stretch as module

RUN  apt-get update \
  && apt-get install -y wget unzip maven
RUN mvn -DgroupId=org.jasypt -DartifactId=jasypt -Dversion=1.9.3 dependency:get
RUN mvn -DgroupId=org.eclipse.persistence -DartifactId=eclipselink -Dversion=2.7.4 dependency:get
RUN mvn -DgroupId=mysql -DartifactId=mysql-connector-java -Dversion=8.0.17 dependency:get

FROM jboss/wildfly:18.0.1.Final

ENV WILDFLY_HOME /opt/jboss/wildfly

RUN mkdir -p $WILDFLY_HOME/scripts
COPY configuration/jboss/template/standalone.xml $WILDFLY_HOME/scripts/standalone.xml
COPY configuration/jboss/module/mysql $WILDFLY_HOME/modules/system/layers/base/com/mysql
COPY configuration/jboss/module/eclipselink $WILDFLY_HOME/modules/system/layers/base/org/eclipse/persistence/
COPY --from=module /root/.m2/repository/mysql/mysql-connector-java/8.0.17/mysql-connector-java-8.0.17.jar \
$WILDFLY_HOME/modules/system/layers/base/com/mysql/main/.
COPY --from=module /root/.m2/repository/org/eclipse/persistence/eclipselink/2.7.4/eclipselink-2.7.4.jar \
$WILDFLY_HOME/modules/system/layers/base/org/eclipse/persistence/main/eclipselink.jar
COPY --from=module /root/.m2/repository/org/jasypt $WILDFLY_HOME/scripts/repository/org/jasypt

COPY ear/target/openbank-1.0-SNAPSHOT.ear $WILDFLY_HOME/standalone/deployments/openbank-1.0-SNAPSHOT.ear

EXPOSE 8080 9990

CMD ["/bin/bash", "-c", "/opt/jboss/wildfly/scripts/openbank_setup.sh && /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0"]