package com.navercorp.pinpoint.plugin.kafka.interceptor;


import KafkaConstants.KAFKA_CLIENT_INTERNAL;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsumerRecordEntryPointInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private Trace trace;

    @Mock
    private SpanRecorder recorder;

    @Mock
    private SpanEventRecorder eventRecorder;

    @Mock
    private ConsumerRecord consumerRecord;

    @Mock
    private Headers headers;

    @Test
    public void doInBeforeTrace() {
        ConsumerRecordEntryPointInterceptor interceptor = new ConsumerRecordEntryPointInterceptor(traceContext, descriptor, 0);
        interceptor.doInBeforeTrace(eventRecorder, new Object(), new Object[]{  });
        Mockito.verify(eventRecorder).recordServiceType(KAFKA_CLIENT_INTERNAL);
    }

    @Test
    public void doInAfterTrace() {
        ConsumerRecordEntryPointInterceptor interceptor = new ConsumerRecordEntryPointInterceptor(traceContext, descriptor, 0);
        interceptor.doInAfterTrace(eventRecorder, new Object(), new Object[]{  }, null, null);
        Mockito.verify(eventRecorder).recordApi(descriptor);
        Mockito.verify(eventRecorder).recordException(null);
    }

    @Test
    public void createTrace() {
        Mockito.doReturn(trace).when(traceContext).newTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(recorder).when(trace).getSpanRecorder();
        Mockito.doReturn("Test").when(consumerRecord).topic();
        Mockito.doReturn(1L).when(consumerRecord).offset();
        Mockito.doReturn(0).when(consumerRecord).partition();
        Mockito.doReturn(headers).when(consumerRecord).headers();
        Mockito.doReturn(new Header[]{  }).when(headers).toArray();
        ConsumerRecordEntryPointInterceptor interceptor = new ConsumerRecordEntryPointInterceptor(traceContext, descriptor, 0);
        interceptor.createTrace(new Object(), new Object[]{ consumerRecord });
        Mockito.verify(recorder).recordAcceptorHost("Unknown");
        Mockito.verify(recorder).recordRpcName("kafka://topic=Test?partition=0&offset=1");
    }
}

