package com.navercorp.pinpoint.plugin.kafka.interceptor;


import KafkaConstants.KAFKA_BATCH_ANNOTATION_KEY;
import KafkaConstants.KAFKA_TOPIC_ANNOTATION_KEY;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsumerMultiRecordEntryPointInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private Trace trace;

    @Mock
    private SpanRecorder recorder;

    @Mock
    private ConsumerRecords consumerRecords;

    @Test
    public void createTraceTest1() {
        List<ConsumerRecord> consumerRecordList = new ArrayList<ConsumerRecord>();
        consumerRecordList.add(new ConsumerRecord("Test", 1, 1, "hello", "hello too"));
        Mockito.doReturn(trace).when(traceContext).newTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(recorder).when(trace).getSpanRecorder();
        Mockito.doReturn(consumerRecordList.iterator()).when(consumerRecords).iterator();
        ConsumerMultiRecordEntryPointInterceptor interceptor = new ConsumerMultiRecordEntryPointInterceptor(traceContext, descriptor, 0);
        interceptor.createTrace(new Object(), new Object[]{ consumerRecords });
        Mockito.verify(recorder).recordAcceptorHost("Unknown");
        Mockito.verify(recorder).recordAttribute(KAFKA_TOPIC_ANNOTATION_KEY, "Test");
        Mockito.verify(recorder).recordAttribute(KAFKA_BATCH_ANNOTATION_KEY, 1);
        Mockito.verify(recorder).recordRpcName("kafka://topic=Test?batch=1");
    }

    @Test
    public void createTraceTest2() {
        List<ConsumerRecord> consumerRecordList = new ArrayList<ConsumerRecord>();
        consumerRecordList.add(new ConsumerRecord("Test", 1, 1, "hello", "hello too"));
        consumerRecordList.add(new ConsumerRecord("Test2", 2, 1, "hello2", "hello too2"));
        Mockito.doReturn(trace).when(traceContext).newTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(recorder).when(trace).getSpanRecorder();
        Mockito.doReturn(consumerRecordList.iterator()).when(consumerRecords).iterator();
        ConsumerMultiRecordEntryPointInterceptor interceptor = new ConsumerMultiRecordEntryPointInterceptor(traceContext, descriptor, 0);
        interceptor.createTrace(new Object(), new Object[]{ consumerRecords });
        Mockito.verify(recorder).recordAcceptorHost("Unknown");
        Mockito.verify(recorder).recordAttribute(KAFKA_TOPIC_ANNOTATION_KEY, "[Test, Test2]");
        Mockito.verify(recorder).recordAttribute(KAFKA_BATCH_ANNOTATION_KEY, 2);
        Mockito.verify(recorder).recordRpcName("kafka://topic=[Test, Test2]?batch=2");
    }
}

