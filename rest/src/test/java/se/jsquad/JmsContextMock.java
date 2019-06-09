package se.jsquad;

import org.apache.activemq.artemis.jms.client.ActiveMQQueue;

import javax.inject.Named;
import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.Serializable;

@Named("jmsContextMock")
public class JmsContextMock implements JMSContext {
    @Override
    public JMSContext createContext(int i) {
        return null;
    }

    @Override
    public JMSProducer createProducer() {
        return null;
    }

    @Override
    public String getClientID() {
        return null;
    }

    @Override
    public void setClientID(String s) {
        // NO SONAR
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return null;
    }

    @Override
    public ExceptionListener getExceptionListener() {
        return null;
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) {
        // NO SONAR
    }

    @Override
    public void start() {
        // NO SONAR
    }

    @Override
    public void stop() {
        // NO SONAR
    }

    @Override
    public void setAutoStart(boolean b) {
        // NO SONAR
    }

    @Override
    public boolean getAutoStart() {
        return false;
    }

    @Override
    public void close() {
        // NO SONAR
    }

    @Override
    public BytesMessage createBytesMessage() {
        return null;
    }

    @Override
    public MapMessage createMapMessage() {
        return null;
    }

    @Override
    public Message createMessage() {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage() {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable) {
        return null;
    }

    @Override
    public StreamMessage createStreamMessage() {
        return null;
    }

    @Override
    public TextMessage createTextMessage() {
        return null;
    }

    @Override
    public TextMessage createTextMessage(String s) {
        return null;
    }

    @Override
    public boolean getTransacted() {
        return false;
    }

    @Override
    public int getSessionMode() {
        return 0;
    }

    @Override
    public void commit() {
        // NO SONAR
    }

    @Override
    public void rollback() {
        // NO SONAR
    }

    @Override
    public void recover() {
        // NO SONAR
    }

    @Override
    public JMSConsumer createConsumer(Destination destination) {
        return null;
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String s) {
        return null;
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String s, boolean b) {
        return null;
    }

    @Override
    public Queue createQueue(String s) {
        return new ActiveMQQueue();
    }

    @Override
    public Topic createTopic(String s) {
        return null;
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String s) {
        return null;
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String s, String s1, boolean b) {
        return null;
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String s) {
        return null;
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String s, String s1) {
        return null;
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String s) {
        return null;
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String s, String s1) {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String s) {
        return null;
    }

    @Override
    public TemporaryQueue createTemporaryQueue() {
        return null;
    }

    @Override
    public TemporaryTopic createTemporaryTopic() {
        return null;
    }

    @Override
    public void unsubscribe(String s) {
        // NO SONAR
    }

    @Override
    public void acknowledge() {
        // NO SONAR
    }
}
