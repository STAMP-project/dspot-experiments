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


import com.codahale.metrics.MetricRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AbstractMetricsProducerTest {
    public static final String METRIC_NAME = "a metric";

    @Mock
    private MetricsEndpoint endpoint;

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @Mock
    private MetricRegistry registry;

    private AbstractMetricsProducer okProducer;

    private AbstractMetricsProducer failProducer;

    private InOrder inOrder;

    @Test
    public void testDoProcess() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn(null);
        Mockito.when(in.removeHeaders(AbstractMetricsProducer.HEADER_PATTERN)).thenReturn(true);
        okProducer.process(exchange);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getMetricsName();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verify(endpoint, Mockito.times(1)).getRegistry();
        inOrder.verify(in, Mockito.times(1)).removeHeaders(AbstractMetricsProducer.HEADER_PATTERN);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDoProcessWithException() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn(null);
        Mockito.when(in.removeHeaders(AbstractMetricsProducer.HEADER_PATTERN)).thenReturn(true);
        failProducer.process(exchange);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(endpoint, Mockito.times(1)).getMetricsName();
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verify(endpoint, Mockito.times(1)).getRegistry();
        inOrder.verify(in, Mockito.times(1)).removeHeaders(AbstractMetricsProducer.HEADER_PATTERN);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetMetricsName() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn("A");
        Assert.assertThat(okProducer.getMetricsName(in, "value"), Matchers.is("A"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetMetricsNameNotSet() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn(null);
        Assert.assertThat(okProducer.getMetricsName(in, "name"), Matchers.is("name"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetStringHeader() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn("A");
        Assert.assertThat(okProducer.getStringHeader(in, MetricsConstants.HEADER_METRIC_NAME, "value"), Matchers.is("A"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetStringHeaderWithNullValue() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn(null);
        Assert.assertThat(okProducer.getStringHeader(in, MetricsConstants.HEADER_METRIC_NAME, "value"), Matchers.is("value"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetStringHeaderWithWhiteSpaces() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn(" ");
        Assert.assertThat(okProducer.getStringHeader(in, MetricsConstants.HEADER_METRIC_NAME, "value"), Matchers.is("value"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetStringHeaderWithEmptySrting() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class)).thenReturn("");
        Assert.assertThat(okProducer.getStringHeader(in, MetricsConstants.HEADER_METRIC_NAME, "value"), Matchers.is("value"));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetLongHeader() throws Exception {
        Mockito.when(in.getHeader(MetricsConstants.HEADER_HISTOGRAM_VALUE, 19L, Long.class)).thenReturn(201L);
        Assert.assertThat(okProducer.getLongHeader(in, MetricsConstants.HEADER_HISTOGRAM_VALUE, 19L), Matchers.is(201L));
        inOrder.verify(in, Mockito.times(1)).getHeader(MetricsConstants.HEADER_HISTOGRAM_VALUE, 19L, Long.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testClearMetricsHeaders() throws Exception {
        Mockito.when(in.removeHeaders(AbstractMetricsProducer.HEADER_PATTERN)).thenReturn(true);
        Assert.assertThat(okProducer.clearMetricsHeaders(in), Matchers.is(true));
        inOrder.verify(in, Mockito.times(1)).removeHeaders(AbstractMetricsProducer.HEADER_PATTERN);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testClearRealHeaders() throws Exception {
        Message msg = new org.apache.camel.support.DefaultMessage(new DefaultCamelContext());
        Object val = new Object();
        msg.setHeader(MetricsConstants.HEADER_HISTOGRAM_VALUE, 109L);
        msg.setHeader(MetricsConstants.HEADER_METRIC_NAME, "the metric");
        msg.setHeader("notRemoved", val);
        Assert.assertThat(msg.getHeaders().size(), Matchers.is(3));
        Assert.assertThat(msg.getHeader(MetricsConstants.HEADER_HISTOGRAM_VALUE, Long.class), Matchers.is(109L));
        Assert.assertThat(msg.getHeader(MetricsConstants.HEADER_METRIC_NAME, String.class), Matchers.is("the metric"));
        Assert.assertThat(msg.getHeader("notRemoved"), Matchers.is(val));
        okProducer.clearMetricsHeaders(msg);
        Assert.assertThat(msg.getHeaders().size(), Matchers.is(1));
        Assert.assertThat(msg.getHeader("notRemoved"), Matchers.is(val));
    }
}

