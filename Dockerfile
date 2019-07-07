#
# Copyright 2019 JSquad AB
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

FROM maven:3.6.0-jdk-11 as build

ENV WILDFLY_VERSION 16.0.0.Final
ENV WILDFLY_HOME /usr

RUN rm -fr /usr/wildfly

RUN cd $WILDFLY_HOME && curl https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
| tar zx && mv $WILDFLY_HOME/wildfly-$WILDFLY_VERSION $WILDFLY_HOME/wildfly

RUN /usr/wildfly/bin/add-user.sh --silent admin admin1234
RUN /usr/wildfly/bin/add-user.sh -a -g admin --silent root root
RUN /usr/wildfly/bin/add-user.sh -a  -g customer --silent john doe

ADD . /usr/openbank

RUN cp /usr/openbank/configuration/jboss/standalone.xml $WILDFLY_HOME/wildfly/standalone/configuration/.

FROM openjdk:11-jre-slim

COPY --from=build /usr/wildfly /usr/wildfly
COPY ear/target/openbank-1.0-SNAPSHOT.ear /usr/wildfly/standalone/deployments/openbank-1.0-SNAPSHOT.ear

EXPOSE 8080:8080
EXPOSE 9990:9990

CMD ["/usr/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]