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


import MetricsTimerAction.start;
import MetricsTimerAction.stop;
import Timer.Context;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
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
public class TimerProducerTest {
    private static final String METRICS_NAME = "metrics.name";

    private static final String PROPERTY_NAME = ("timer" + ":") + (TimerProducerTest.METRICS_NAME);

    @Mock
    private MetricsEndpoint endpoint;

    @Mock
    private Exchange exchange;

    @Mock
    private MetricRegistry registry;

    @Mock
    private Timer timer;

    @Mock
    private Context context;

    @Mock
    private Message in;

    private TimerProducer producer;

    @Mock
    private InOrder inOrder;

    @Test
    public void testTimerProducer() throws Exception {
        Assert.assertThat(producer, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(producer.getEndpoint().equals(endpoint), Matchers.is(true));
    }

    @Test
    public void testProcessStart() throws Exception {
        Mockito.when(endpoint.getAction()).thenReturn(start);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_TIMER_ACTION, start, MetricsTimerAction.class)).thenReturn(start);
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(null);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, start, MetricsTimerAction.class);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(registry, Mockito.times(1)).timer(TimerProducerTest.METRICS_NAME);
        inOrder.verify(timer, Mockito.times(1)).time();
        inOrder.verify(exchange, Mockito.times(1)).setProperty(TimerProducerTest.PROPERTY_NAME, context);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessStartWithOverride() throws Exception {
        Mockito.when(endpoint.getAction()).thenReturn(start);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_TIMER_ACTION, start, MetricsTimerAction.class)).thenReturn(stop);
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(context);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, start, MetricsTimerAction.class);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(context, Mockito.times(1)).stop();
        inOrder.verify(exchange, Mockito.times(1)).removeProperty(TimerProducerTest.PROPERTY_NAME);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessStop() throws Exception {
        Mockito.when(endpoint.getAction()).thenReturn(stop);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_TIMER_ACTION, stop, MetricsTimerAction.class)).thenReturn(stop);
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(context);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, stop, MetricsTimerAction.class);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(context, Mockito.times(1)).stop();
        inOrder.verify(exchange, Mockito.times(1)).removeProperty(TimerProducerTest.PROPERTY_NAME);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessStopWithOverride() throws Exception {
        Mockito.when(endpoint.getAction()).thenReturn(stop);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_TIMER_ACTION, stop, MetricsTimerAction.class)).thenReturn(start);
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(null);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, stop, MetricsTimerAction.class);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(registry, Mockito.times(1)).timer(TimerProducerTest.METRICS_NAME);
        inOrder.verify(timer, Mockito.times(1)).time();
        inOrder.verify(exchange, Mockito.times(1)).setProperty(TimerProducerTest.PROPERTY_NAME, context);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessNoAction() throws Exception {
        Mockito.when(endpoint.getAction()).thenReturn(null);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, ((Object) (null)), MetricsTimerAction.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessNoActionOverride() throws Exception {
        Object action = null;
        Mockito.when(endpoint.getAction()).thenReturn(null);
        Mockito.when(in.getHeader(MetricsConstants.HEADER_TIMER_ACTION, action, MetricsTimerAction.class)).thenReturn(start);
        producer.doProcess(exchange, endpoint, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getAction();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_TIMER_ACTION, action, MetricsTimerAction.class);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(registry, Mockito.times(1)).timer(TimerProducerTest.METRICS_NAME);
        inOrder.verify(timer, Mockito.times(1)).time();
        inOrder.verify(exchange, Mockito.times(1)).setProperty(TimerProducerTest.PROPERTY_NAME, context);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testHandleStart() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(null);
        producer.handleStart(exchange, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(registry, Mockito.times(1)).timer(TimerProducerTest.METRICS_NAME);
        inOrder.verify(timer, Mockito.times(1)).time();
        inOrder.verify(exchange, Mockito.times(1)).setProperty(TimerProducerTest.PROPERTY_NAME, context);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testHandleStartAlreadyRunning() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(context);
        producer.handleStart(exchange, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testHandleStop() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(context);
        producer.handleStop(exchange, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verify(context, Mockito.times(1)).stop();
        inOrder.verify(exchange, Mockito.times(1)).removeProperty(TimerProducerTest.PROPERTY_NAME);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testHandleStopContextNotFound() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(null);
        producer.handleStop(exchange, registry, TimerProducerTest.METRICS_NAME);
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetPropertyName() throws Exception {
        Assert.assertThat(producer.getPropertyName(TimerProducerTest.METRICS_NAME), Matchers.is((("timer" + ":") + (TimerProducerTest.METRICS_NAME))));
    }

    @Test
    public void testGetTimerContextFromExchange() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(context);
        Assert.assertThat(producer.getTimerContextFromExchange(exchange, TimerProducerTest.PROPERTY_NAME), Matchers.is(context));
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetTimerContextFromExchangeNotFound() throws Exception {
        Mockito.when(exchange.getProperty(TimerProducerTest.PROPERTY_NAME, Context.class)).thenReturn(null);
        Assert.assertThat(producer.getTimerContextFromExchange(exchange, TimerProducerTest.PROPERTY_NAME), Matchers.is(Matchers.nullValue()));
        inOrder.verify(exchange, Mockito.times(1)).getProperty(TimerProducerTest.PROPERTY_NAME, Context.class);
        inOrder.verifyNoMoreInteractions();
    }
}

