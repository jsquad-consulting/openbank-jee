package se.jsquad.jms;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
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
    private static final Logger logger = Logger.getLogger(MessageMDB.class.getName());

    @Resource
    private MessageDrivenContext messageDrivenContext;

    @Override
    public void onMessage(Message message) {
        logger.log(Level.FINE, "onMessage(message: {0}", new Object[] {message});
        if (messageDrivenContext.getRollbackOnly()) {
            logger.log(Level.SEVERE, "Transaction is set for rollback, the message will 'redelivered' by default");
        }
    }
}
