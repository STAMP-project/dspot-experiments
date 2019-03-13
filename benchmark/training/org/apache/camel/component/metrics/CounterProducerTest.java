/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.metrics;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CounterProducerTest {
    private static final String METRICS_NAME = "metrics.name";

    private static final Long INCREMENT = 100000L;

    private static final Long DECREMENT = 91929199L;

    @Mock
    private MetricsEndpoint endpoint;

    @Mock
    private Exchange exchange;

    @Mock
    private MetricRegistry registry;

    @Mock
    private Counter counter;

    @Mock
    private Message in;

    private CounterProducer producer;

    private InOrder inOrder;

    @Test
    public void testCounterProducer() throws Exception {
        Assert.assertThat(producer.getEndpoint().equals(endpoint), Matchers.is(true));
    }

    @Test
    public void testProcessWithIncrementOnly() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getIncrement()).thenReturn(CounterProducerTest.INCREMENT);
        Mockito.when(endpoint.getDecrement()).thenReturn(null);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class)).thenReturn(CounterProducerTest.INCREMENT);
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, action, Long.class);
        inOrder.verify(counter, Mockito.times(1)).inc(CounterProducerTest.INCREMENT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessWithDecrementOnly() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getIncrement()).thenReturn(null);
        Mockito.when(endpoint.getDecrement()).thenReturn(CounterProducerTest.DECREMENT);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class)).thenReturn(CounterProducerTest.DECREMENT);
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, action, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class);
        inOrder.verify(counter, Mockito.times(1)).dec(CounterProducerTest.DECREMENT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDoProcessWithIncrementAndDecrement() throws Exception {
        Mockito.when(endpoint.getIncrement()).thenReturn(CounterProducerTest.INCREMENT);
        Mockito.when(endpoint.getDecrement()).thenReturn(CounterProducerTest.DECREMENT);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class)).thenReturn(CounterProducerTest.INCREMENT);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class)).thenReturn(CounterProducerTest.DECREMENT);
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class);
        inOrder.verify(counter, Mockito.times(1)).inc(CounterProducerTest.INCREMENT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessWithOutIncrementAndDecrement() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getIncrement()).thenReturn(null);
        Mockito.when(endpoint.getDecrement()).thenReturn(null);
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, action, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, action, Long.class);
        inOrder.verify(counter, Mockito.times(1)).inc();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessOverridingIncrement() throws Exception {
        Mockito.when(endpoint.getIncrement()).thenReturn(CounterProducerTest.INCREMENT);
        Mockito.when(endpoint.getDecrement()).thenReturn(CounterProducerTest.DECREMENT);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class)).thenReturn(((CounterProducerTest.INCREMENT) + 1));
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class)).thenReturn(CounterProducerTest.DECREMENT);
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, CounterProducerTest.INCREMENT, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class);
        inOrder.verify(counter, Mockito.times(1)).inc(((CounterProducerTest.INCREMENT) + 1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessOverridingDecrement() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getIncrement()).thenReturn(null);
        Mockito.when(endpoint.getDecrement()).thenReturn(CounterProducerTest.DECREMENT);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class)).thenReturn(((CounterProducerTest.DECREMENT) - 1));
        producer.doProcess(exchange, endpoint, registry, CounterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).counter(CounterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getIncrement();
        inOrder.verify(endpoint, Mockito.times(1)).getDecrement();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_INCREMENT, action, Long.class);
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_COUNTER_DECREMENT, CounterProducerTest.DECREMENT, Long.class);
        inOrder.verify(counter, Mockito.times(1)).dec(((CounterProducerTest.DECREMENT) - 1));
        inOrder.verifyNoMoreInteractions();
    }
}

