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
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "callQ"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName =
        "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class MessageMDB implements MessageListener {
    @Inject @Log
    private Logger logger;

    @Resource
    private MessageDrivenContext messageDrivenContext;

    @Override
    public void onMessage(Message message) {
        if (messageDrivenContext.getRollbackOnly()) {
            logger.log(Level.SEVERE, "Transaction is set for rollback, the message will 'redelivered' by default");
        }
    }
}
