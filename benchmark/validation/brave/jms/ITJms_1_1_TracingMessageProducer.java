package brave.jms;


import brave.ScopedSpan;
import brave.propagation.TraceContext;
import java.util.Collections;
import java.util.Map;
import javax.jms.Message;
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
import zipkin2.Span;


/**
 * When adding tests here, also add to {@linkplain brave.jms.ITTracingJMSProducer}
 */
public class ITJms_1_1_TracingMessageProducer extends JmsTest {
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

    Map<String, String> existingProperties = Collections.singletonMap("tx", "1");

    @Test
    public void should_add_b3_single_property() throws Exception {
        messageProducer.send(jms.destination, message);
        assertHasB3SingleProperty(messageConsumer.receive());
    }

    @Test
    public void should_add_b3_single_property_queue() throws Exception {
        queueSender.send(jms.queue, message);
        assertHasB3SingleProperty(queueReceiver.receive());
    }

    @Test
    public void should_add_b3_single_property_topic() throws Exception {
        topicPublisher.publish(jms.topic, message);
        assertHasB3SingleProperty(topicSubscriber.receive());
    }

    @Test
    public void should_not_serialize_parent_span_id() throws Exception {
        ScopedSpan parent = tracing.tracer().startScopedSpan("main");
        try {
            messageProducer.send(jms.destination, message);
        } finally {
            parent.finish();
        }
        Message received = messageConsumer.receive();
        Span producerSpan = takeSpan();
        Span parentSpan = takeSpan();
        assertThat(producerSpan.parentId()).isEqualTo(parentSpan.id());
        assertThat(JmsTest.propertiesToMap(received)).containsAllEntriesOf(existingProperties).containsEntry("b3", ((((producerSpan.traceId()) + "-") + (producerSpan.id())) + "-1"));
    }

    @Test
    public void should_prefer_current_to_stale_b3_header() throws Exception {
        jms.setReadOnlyProperties(message, false);
        message.setStringProperty("b3", writeB3SingleFormat(TraceContext.newBuilder().traceId(1).spanId(1).build()));
        ScopedSpan parent = tracing.tracer().startScopedSpan("main");
        try {
            messageProducer.send(jms.destination, message);
        } finally {
            parent.finish();
        }
        Message received = messageConsumer.receive();
        Span producerSpan = takeSpan();
        Span parentSpan = takeSpan();
        assertThat(producerSpan.parentId()).isEqualTo(parentSpan.id());
        assertThat(JmsTest.propertiesToMap(received)).containsAllEntriesOf(existingProperties).containsEntry("b3", ((((producerSpan.traceId()) + "-") + (producerSpan.id())) + "-1"));
    }

    @Test
    public void should_record_properties() throws Exception {
        messageProducer.send(jms.destination, message);
        should_record_properties(Collections.singletonMap("jms.queue", jms.destinationName));
    }

    @Test
    public void should_record_properties_queue() throws Exception {
        queueSender.send(jms.queue, message);
        should_record_properties(Collections.singletonMap("jms.queue", jms.queueName));
    }

    @Test
    public void should_record_properties_topic() throws Exception {
        topicPublisher.send(jms.topic, message);
        should_record_properties(Collections.singletonMap("jms.topic", jms.topicName));
    }

    @Test
    public void should_record_error() throws Exception {
        should_record_error(() -> messageProducer.send(message));
    }

    @Test
    public void should_record_error_queue() throws Exception {
        should_record_error(() -> queueSender.send(message));
    }

    @Test
    public void should_record_error_topic() throws Exception {
        should_record_error(() -> topicPublisher.send(message));
    }
}

