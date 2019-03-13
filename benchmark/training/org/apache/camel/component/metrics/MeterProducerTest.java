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


import com.codahale.metrics.Meter;
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
public class MeterProducerTest {
    private static final String METRICS_NAME = "metrics.name";

    private static final Long MARK = 9919120L;

    @Mock
    private MetricsEndpoint endpoint;

    @Mock
    private MetricRegistry registry;

    @Mock
    private Meter meter;

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    private MeterProducer producer;

    private InOrder inOrder;

    @Test
    public void testMeterProducer() throws Exception {
        Assert.assertThat(producer, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(producer.getEndpoint(), Matchers.is(Matchers.equalTo(endpoint)));
    }

    @Test
    public void testProcessMarkSet() throws Exception {
        Mockito.when(endpoint.getMark()).thenReturn(MeterProducerTest.MARK);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METER_MARK, MeterProducerTest.MARK, Long.class)).thenReturn(MeterProducerTest.MARK);
        producer.doProcess(exchange, endpoint, registry, MeterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).meter(MeterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getMark();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METER_MARK, MeterProducerTest.MARK, Long.class);
        inOrder.verify(meter, Mockito.times(1)).mark(MeterProducerTest.MARK);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessMarkSetOverrideByHeaderValue() throws Exception {
        Mockito.when(endpoint.getMark()).thenReturn(MeterProducerTest.MARK);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METER_MARK, MeterProducerTest.MARK, Long.class)).thenReturn(((MeterProducerTest.MARK) + 101));
        producer.doProcess(exchange, endpoint, registry, MeterProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(registry, Mockito.times(1)).meter(MeterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getMark();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METER_MARK, MeterProducerTest.MARK, Long.class);
        inOrder.verify(meter, Mockito.times(1)).mark(((MeterProducerTest.MARK) + 101));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessMarkNotSet() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getMark()).thenReturn(null);
        producer.doProcess(exchange, endpoint, registry, MeterProducerTest.METRICS_NAME);
        inOrder.verify(registry, Mockito.times(1)).meter(MeterProducerTest.METRICS_NAME);
        inOrder.verify(endpoint, Mockito.times(1)).getMark();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METER_MARK, action, Long.class);
        inOrder.verify(meter, Mockito.times(1)).mark();
        inOrder.verifyNoMoreInteractions();
    }
}

