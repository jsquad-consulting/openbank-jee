package se.jsquad.jms;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageSenderSessionJMS {
    private static final Logger logger = Logger.getLogger(MessageSenderSessionJMS.class.getName());

    @Resource(mappedName = "java:/jms/queue/callQ")
    private Queue queue;

    @Inject
    @JMSConnectionFactory("/ConnectionFactory")
    @JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext jmsContext;

    public void sendMessage(String body) throws JMSException {
        logger.log(Level.FINE, "sendMessage(message: {0})", new Object[] {body});

        Message message = jmsContext.createObjectMessage(body);
        message.setJMSTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        message.setJMSCorrelationID(UUID.randomUUID().toString());

        jmsContext.createProducer().send(queue, message);
    }
}
