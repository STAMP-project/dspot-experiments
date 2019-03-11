package brave.jms;


import javax.jms.CompletionListener;
import javax.jms.JMSException;
import org.junit.Test;


/**
 * When adding tests here, also add to {@linkplain brave.jms.ITTracingJMSProducer}
 */
public class ITJms_2_0_TracingMessageProducer extends ITJms_1_1_TracingMessageProducer {
    @Test
    public void should_complete_on_callback() throws Exception {
        should_complete_on_callback(( listener) -> messageProducer.send(jms.destination, message, listener));
    }

    @Test
    public void should_complete_on_callback_queue() throws Exception {
        should_complete_on_callback(( listener) -> queueSender.send(jms.queue, message, listener));
    }

    @Test
    public void should_complete_on_callback_topic() throws Exception {
        should_complete_on_callback(( listener) -> topicPublisher.send(jms.topic, message, listener));
    }

    interface JMSAsync {
        void send(CompletionListener listener) throws JMSException;
    }
}

