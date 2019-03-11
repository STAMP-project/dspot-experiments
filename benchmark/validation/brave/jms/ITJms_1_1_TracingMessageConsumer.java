package brave.jms;


import java.util.Collections;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * When adding tests here, also add to {@linkplain brave.jms.ITTracingJMSConsumer}
 */
public class ITJms_1_1_TracingMessageConsumer extends JmsTest {
    @Rule
    public TestName testName = new TestName();

    @Rule
    public JmsTestRule jms = newJmsTestRule(testName);

    Session tracedSession;

    MessageProducer messageProducer;

    MessageConsumer messageConsumer;

    QueueSession tracedQueueSession;

    QueueSender queueSender;

    QueueReceiver queueReceiver;

    TopicSession tracedTopicSession;

    TopicPublisher topicPublisher;

    TopicSubscriber topicSubscriber;

    TextMessage message;

    @Test
    public void messageListener_startsNewTrace() throws Exception {
        messageListener_startsNewTrace(() -> messageProducer.send(message), messageConsumer, Collections.singletonMap("jms.queue", jms.destinationName));
    }

    @Test
    public void messageListener_startsNewTrace_queue() throws Exception {
        messageListener_startsNewTrace(() -> queueSender.send(message), queueReceiver, Collections.singletonMap("jms.queue", jms.queueName));
    }

    @Test
    public void messageListener_startsNewTrace_topic() throws Exception {
        messageListener_startsNewTrace(() -> topicPublisher.send(message), topicSubscriber, Collections.singletonMap("jms.topic", jms.topicName));
    }

    @Test
    public void messageListener_resumesTrace() throws Exception {
        messageListener_resumesTrace(() -> messageProducer.send(message), messageConsumer);
    }

    @Test
    public void messageListener_resumesTrace_queue() throws Exception {
        messageListener_resumesTrace(() -> queueSender.send(message), queueReceiver);
    }

    @Test
    public void messageListener_resumesTrace_topic() throws Exception {
        messageListener_resumesTrace(() -> topicPublisher.send(message), topicSubscriber);
    }

    @Test
    public void receive_startsNewTrace() throws Exception {
        receive_startsNewTrace(() -> messageProducer.send(message), messageConsumer, Collections.singletonMap("jms.queue", jms.destinationName));
    }

    @Test
    public void receive_startsNewTrace_queue() throws Exception {
        receive_startsNewTrace(() -> queueSender.send(message), queueReceiver, Collections.singletonMap("jms.queue", jms.queueName));
    }

    @Test
    public void receive_startsNewTrace_topic() throws Exception {
        receive_startsNewTrace(() -> topicPublisher.send(message), topicSubscriber, Collections.singletonMap("jms.topic", jms.topicName));
    }

    @Test
    public void receive_resumesTrace() throws Exception {
        receive_resumesTrace(() -> messageProducer.send(message), messageConsumer);
    }

    @Test
    public void receive_resumesTrace_queue() throws Exception {
        receive_resumesTrace(() -> queueSender.send(message), queueReceiver);
    }

    @Test
    public void receive_resumesTrace_topic() throws Exception {
        receive_resumesTrace(() -> topicPublisher.send(message), topicSubscriber);
    }
}

