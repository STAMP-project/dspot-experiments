package brave.jms;


import Propagation.B3_SINGLE_STRING;
import Propagation.B3_STRING;
import brave.Span;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.TraceContext;
import java.util.Arrays;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XAQueueConnection;
import javax.jms.XATopicConnection;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.mockito.Mockito;

import static zipkin2.Span;


public class JmsTracingTest extends JmsTest {
    ActiveMQTextMessage message = new ActiveMQTextMessage();

    @Test
    public void connectionFactory_wrapsInput() {
        assertThat(jmsTracing.connectionFactory(Mockito.mock(ConnectionFactory.class))).isInstanceOf(TracingConnectionFactory.class);
    }

    @Test
    public void connectionFactory_doesntDoubleWrap() {
        ConnectionFactory wrapped = jmsTracing.connectionFactory(Mockito.mock(ConnectionFactory.class));
        assertThat(jmsTracing.connectionFactory(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void connectionFactory_wrapsXaInput() {
        abstract class Both implements ConnectionFactory , XAConnectionFactory {}
        assertThat(jmsTracing.connectionFactory(Mockito.mock(Both.class))).isInstanceOf(XAConnectionFactory.class);
    }

    @Test
    public void connection_wrapsInput() {
        assertThat(jmsTracing.connection(Mockito.mock(Connection.class))).isInstanceOf(TracingConnection.class);
    }

    @Test
    public void connection_doesntDoubleWrap() {
        Connection wrapped = jmsTracing.connection(Mockito.mock(Connection.class));
        assertThat(jmsTracing.connection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void connection_wrapsXaInput() {
        abstract class Both implements Connection , XAConnection {}
        assertThat(jmsTracing.connection(Mockito.mock(Both.class))).isInstanceOf(XAConnection.class);
    }

    @Test
    public void queueConnection_wrapsInput() {
        assertThat(jmsTracing.queueConnection(Mockito.mock(QueueConnection.class))).isInstanceOf(TracingConnection.class);
    }

    @Test
    public void queueConnection_doesntDoubleWrap() {
        QueueConnection wrapped = jmsTracing.queueConnection(Mockito.mock(QueueConnection.class));
        assertThat(jmsTracing.queueConnection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void queueConnection_wrapsXaInput() {
        abstract class Both implements QueueConnection , XAQueueConnection {}
        assertThat(jmsTracing.queueConnection(Mockito.mock(Both.class))).isInstanceOf(XAQueueConnection.class);
    }

    @Test
    public void topicConnection_wrapsInput() {
        assertThat(jmsTracing.topicConnection(Mockito.mock(TopicConnection.class))).isInstanceOf(TracingConnection.class);
    }

    @Test
    public void topicConnection_doesntDoubleWrap() {
        TopicConnection wrapped = jmsTracing.topicConnection(Mockito.mock(TopicConnection.class));
        assertThat(jmsTracing.topicConnection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void topicConnection_wrapsXaInput() {
        abstract class Both implements TopicConnection , XATopicConnection {}
        assertThat(jmsTracing.topicConnection(Mockito.mock(Both.class))).isInstanceOf(XATopicConnection.class);
    }

    @Test
    public void xaConnectionFactory_wrapsInput() {
        assertThat(jmsTracing.xaConnectionFactory(Mockito.mock(XAConnectionFactory.class))).isInstanceOf(TracingXAConnectionFactory.class);
    }

    @Test
    public void xaConnectionFactory_doesntDoubleWrap() {
        XAConnectionFactory wrapped = jmsTracing.xaConnectionFactory(Mockito.mock(XAConnectionFactory.class));
        assertThat(jmsTracing.xaConnectionFactory(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void xaConnection_wrapsInput() {
        assertThat(jmsTracing.xaConnection(Mockito.mock(XAConnection.class))).isInstanceOf(TracingXAConnection.class);
    }

    @Test
    public void xaConnection_doesntDoubleWrap() {
        XAConnection wrapped = jmsTracing.xaConnection(Mockito.mock(XAConnection.class));
        assertThat(jmsTracing.xaConnection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void xaQueueConnection_wrapsInput() {
        assertThat(jmsTracing.xaQueueConnection(Mockito.mock(XAQueueConnection.class))).isInstanceOf(TracingXAConnection.class);
    }

    @Test
    public void xaQueueConnection_doesntDoubleWrap() {
        XAQueueConnection wrapped = jmsTracing.xaQueueConnection(Mockito.mock(XAQueueConnection.class));
        assertThat(jmsTracing.xaQueueConnection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void xaTopicConnection_wrapsInput() {
        assertThat(jmsTracing.xaTopicConnection(Mockito.mock(XATopicConnection.class))).isInstanceOf(TracingXAConnection.class);
    }

    @Test
    public void xaTopicConnection_doesntDoubleWrap() {
        XATopicConnection wrapped = jmsTracing.xaTopicConnection(Mockito.mock(XATopicConnection.class));
        assertThat(jmsTracing.xaTopicConnection(wrapped)).isSameAs(wrapped);
    }

    @Test
    public void messageListener_traces() throws Exception {
        jmsTracing.messageListener(Mockito.mock(MessageListener.class), false).onMessage(message);
        assertThat(takeSpan().name()).isEqualTo("on-message");
    }

    @Test
    public void messageListener_traces_addsConsumerSpan() throws Exception {
        jmsTracing.messageListener(Mockito.mock(MessageListener.class), true).onMessage(message);
        assertThat(Arrays.asList(takeSpan(), takeSpan())).extracting(Span::name).containsExactly("receive", "on-message");
    }

    @Test
    public void messageListener_wrapsInput() {
        assertThat(jmsTracing.messageListener(Mockito.mock(MessageListener.class), false)).isInstanceOf(TracingMessageListener.class);
    }

    @Test
    public void messageListener_doesntDoubleWrap() {
        MessageListener wrapped = jmsTracing.messageListener(Mockito.mock(MessageListener.class), false);
        assertThat(jmsTracing.messageListener(wrapped, false)).isSameAs(wrapped);
    }

    @Test
    public void nextSpan_prefers_b3_header() throws Exception {
        message.setStringProperty("b3", "0000000000000001-0000000000000002-1");
        Span child;
        try (Scope ws = tracing.currentTraceContext().newScope(TraceContext.newBuilder().traceId(1).spanId(1).build())) {
            child = jmsTracing.nextSpan(message);
        }
        assertThat(child.context().parentId()).isEqualTo(2L);
    }

    @Test
    public void nextSpan_uses_current_context() {
        Span child;
        try (Scope ws = tracing.currentTraceContext().newScope(TraceContext.newBuilder().traceId(1).spanId(1).build())) {
            child = jmsTracing.nextSpan(message);
        }
        assertThat(child.context().parentId()).isEqualTo(1L);
    }

    @Test
    public void nextSpan_should_use_span_from_headers_as_parent() throws Exception {
        message.setStringProperty("b3", "0000000000000001-0000000000000002-1");
        Span span = jmsTracing.nextSpan(message);
        assertThat(span.context().parentId()).isEqualTo(2L);
    }

    @Test
    public void nextSpan_should_create_span_if_no_headers() {
        Span span = jmsTracing.nextSpan(message);
        assertThat(span).isNotNull();
    }

    @Test
    public void nextSpan_should_tag_queue_when_no_incoming_context() throws Exception {
        message.setDestination(createDestination("foo", QUEUE_TYPE));
        jmsTracing.nextSpan(message).start().finish();
        assertThat(takeSpan().tags()).containsOnly(entry("jms.queue", "foo"));
    }

    /**
     * We assume the queue is already tagged by the producer span. However, we can change this policy
     * now, or later when dynamic policy is added to JmsTracing
     */
    @Test
    public void nextSpan_shouldnt_tag_queue_when_incoming_context() throws Exception {
        message.setStringProperty("b3", "0000000000000001-0000000000000002-1");
        message.setDestination(createDestination("foo", QUEUE_TYPE));
        jmsTracing.nextSpan(message).start().finish();
        assertThat(takeSpan().tags()).isEmpty();
    }

    @Test
    public void nextSpan_should_clear_propagation_headers() throws Exception {
        TraceContext context = TraceContext.newBuilder().traceId(1L).parentId(2L).spanId(3L).debug(true).build();
        B3_STRING.injector(brave.jms.SETTER).inject(context, message);
        B3_SINGLE_STRING.injector(brave.jms.SETTER).inject(context, message);
        jmsTracing.nextSpan(message);
        assertThat(JmsTest.propertiesToMap(message)).isEmpty();
    }

    @Test
    public void nextSpan_should_not_clear_other_headers() throws Exception {
        message.setIntProperty("foo", 1);
        jmsTracing.nextSpan(message);
        assertThat(message.getIntProperty("foo")).isEqualTo(1);
    }
}

