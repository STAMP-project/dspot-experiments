package com.navercorp.pinpoint.plugin.kafka.interceptor;


import KafkaConstants.KAFKA_CLIENT;
import KafkaConstants.KAFKA_TOPIC_ANNOTATION_KEY;
import KafkaConstants.UNKNOWN;
import com.navercorp.pinpoint.plugin.kafka.field.accessor.RemoteAddressFieldAccessor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProducerSendInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private Trace trace;

    @Mock
    private TraceId traceId;

    @Mock
    private TraceId nextId;

    @Mock
    private SpanEventRecorder recorder;

    @Mock
    private ProducerRecord record;

    @Mock
    private Headers headers;

    @Mock
    private RemoteAddressFieldAccessor addressFieldAccessor;

    @Test
    public void before() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Mockito.doReturn(headers).when(record).headers();
        Mockito.doReturn(new Header[]{  }).when(headers).toArray();
        Mockito.doReturn("test").when(nextId).getTransactionId();
        Mockito.doReturn(0L).when(nextId).getSpanId();
        Mockito.doReturn(0L).when(nextId).getParentSpanId();
        short s = 0;
        Mockito.doReturn(s).when(nextId).getFlags();
        ProducerSendInterceptor interceptor = new ProducerSendInterceptor(traceContext, descriptor);
        Object target = new Object();
        Object[] args = new Object[]{ record };
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(KAFKA_CLIENT);
    }

    @Test
    public void after() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        Mockito.doReturn("test").when(record).topic();
        ProducerSendInterceptor interceptor = new ProducerSendInterceptor(traceContext, descriptor);
        Object[] args = new Object[]{ record };
        interceptor.after(addressFieldAccessor, args, null, null);
        Mockito.verify(recorder).recordEndPoint(UNKNOWN);
        Mockito.verify(recorder).recordDestinationId("Unknown");
        Mockito.verify(recorder).recordAttribute(KAFKA_TOPIC_ANNOTATION_KEY, "test");
    }
}

