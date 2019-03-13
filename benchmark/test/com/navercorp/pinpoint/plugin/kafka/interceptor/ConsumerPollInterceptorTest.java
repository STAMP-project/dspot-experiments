package com.navercorp.pinpoint.plugin.kafka.interceptor;


import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.plugin.kafka.field.accessor.RemoteAddressFieldAccessor;
import java.util.Iterator;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsumerPollInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private RemoteAddressFieldAccessor addressFieldAccessor;

    @Mock
    private ConsumerRecords consumerRecords;

    @Mock
    private Iterator iterator;

    @Test
    public void before() {
        ConsumerPollInterceptor interceptor = new ConsumerPollInterceptor(traceContext, descriptor);
        Object target = new Object();
        Object[] args = new Object[]{  };
        interceptor.before(target, args);
    }

    @Test
    public void after() {
        Mockito.doReturn("localhost:9092").when(addressFieldAccessor)._$PINPOINT$_getRemoteAddress();
        Mockito.doReturn(iterator).when(consumerRecords).iterator();
        Mockito.doReturn(false).when(iterator).hasNext();
        ConsumerPollInterceptor interceptor = new ConsumerPollInterceptor(traceContext, descriptor);
        interceptor.after(addressFieldAccessor, new Object[]{  }, consumerRecords, null);
        Mockito.verify(addressFieldAccessor)._$PINPOINT$_getRemoteAddress();
    }
}

