/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.jms;

import se.jsquad.qualifier.Log;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSSessionMode;
import javax.jms.Message;
import javax.jms.Queue;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.logging.Logger;

public class MessageSenderSessionJMS {
    @Inject @Log
    private Logger logger;

    @Resource(mappedName = "java:/jms/queue/callQ")
    private Queue queue;

    @Inject
    @JMSConnectionFactory("/ConnectionFactory")
    @JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext jmsContext;

    public void sendMessage(String body) throws JMSException {
        Message message = jmsContext.createObjectMessage(body);
        message.setJMSTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        message.setJMSCorrelationID(UUID.randomUUID().toString());

        jmsContext.createProducer().send(queue, message);
    }
}
