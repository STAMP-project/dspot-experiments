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


import MetricsComponent.DEFAULT_METRICS_TYPE;
import MetricsComponent.METRIC_REGISTRY_NAME;
import com.codahale.metrics.MetricRegistry;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Registry;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static MetricsType.COUNTER;
import static MetricsType.GAUGE;
import static MetricsType.HISTOGRAM;
import static MetricsType.METER;
import static MetricsType.TIMER;


@RunWith(MockitoJUnitRunner.class)
public class MetricsComponentTest {
    @Mock
    private CamelContext camelContext;

    @Mock
    private Registry camelRegistry;

    @Mock
    private MetricRegistry metricRegistry;

    private InOrder inOrder;

    private MetricsComponent component;

    @Test
    public void testCreateEndpoint() throws Exception {
        component.setCamelContext(camelContext);
        Mockito.when(camelContext.getRegistry()).thenReturn(camelRegistry);
        Mockito.when(camelRegistry.lookupByNameAndType(METRIC_REGISTRY_NAME, MetricRegistry.class)).thenReturn(metricRegistry);
        Map<String, Object> params = new HashMap<>();
        Long value = System.currentTimeMillis();
        params.put("mark", value);
        Endpoint result = component.createEndpoint("metrics:meter:long.meter", "meter:long.meter", params);
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
        MetricsEndpoint me = ((MetricsEndpoint) (result));
        Assert.assertThat(me.getMark(), Matchers.is(value));
        Assert.assertThat(me.getMetricsName(), Matchers.is("long.meter"));
        Assert.assertThat(me.getRegistry(), Matchers.is(metricRegistry));
        inOrder.verify(camelContext, Mockito.times(1)).getRegistry();
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType(METRIC_REGISTRY_NAME, MetricRegistry.class);
        inOrder.verify(camelContext, Mockito.times(1)).getTypeConverter();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateEndpoints() throws Exception {
        component.setCamelContext(camelContext);
        Mockito.when(camelContext.getRegistry()).thenReturn(camelRegistry);
        Mockito.when(camelRegistry.lookupByNameAndType(METRIC_REGISTRY_NAME, MetricRegistry.class)).thenReturn(metricRegistry);
        Map<String, Object> params = new HashMap<>();
        Long value = System.currentTimeMillis();
        params.put("mark", value);
        Endpoint result = component.createEndpoint("metrics:meter:long.meter", "meter:long.meter", params);
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
        MetricsEndpoint me = ((MetricsEndpoint) (result));
        Assert.assertThat(me.getMark(), Matchers.is(value));
        Assert.assertThat(me.getMetricsName(), Matchers.is("long.meter"));
        Assert.assertThat(me.getRegistry(), Matchers.is(metricRegistry));
        params = new HashMap<>();
        params.put("increment", (value + 1));
        params.put("decrement", (value - 1));
        result = component.createEndpoint("metrics:counter:long.counter", "counter:long.counter", params);
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
        MetricsEndpoint ce = ((MetricsEndpoint) (result));
        Assert.assertThat(ce.getIncrement(), Matchers.is((value + 1)));
        Assert.assertThat(ce.getDecrement(), Matchers.is((value - 1)));
        Assert.assertThat(ce.getMetricsName(), Matchers.is("long.counter"));
        Assert.assertThat(ce.getRegistry(), Matchers.is(metricRegistry));
        inOrder.verify(camelContext, Mockito.times(1)).getRegistry();
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType(METRIC_REGISTRY_NAME, MetricRegistry.class);
        inOrder.verify(camelContext, Mockito.times(2)).getTypeConverter();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetMetricsName() throws Exception {
        Assert.assertThat(component.getMetricsName("meter:metric-a"), Matchers.is("metric-a"));
        Assert.assertThat(component.getMetricsName("meter:metric-a:sub-b"), Matchers.is("metric-a:sub-b"));
        Assert.assertThat(component.getMetricsName("metric-a"), Matchers.is("metric-a"));
        Assert.assertThat(component.getMetricsName("//metric-a"), Matchers.is("//metric-a"));
        Assert.assertThat(component.getMetricsName("meter://metric-a"), Matchers.is("//metric-a"));
    }

    @Test
    public void testCreateNewEndpointForCounter() throws Exception {
        Endpoint endpoint = new MetricsEndpoint(null, null, metricRegistry, COUNTER, "a name");
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForMeter() throws Exception {
        Endpoint endpoint = new MetricsEndpoint(null, null, metricRegistry, METER, "a name");
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForGauge() throws Exception {
        MetricsEndpoint endpoint = new MetricsEndpoint(null, null, metricRegistry, GAUGE, "a name");
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForHistogram() throws Exception {
        Endpoint endpoint = new MetricsEndpoint(null, null, metricRegistry, HISTOGRAM, "a name");
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForTimer() throws Exception {
        Endpoint endpoint = new MetricsEndpoint(null, null, metricRegistry, TIMER, "a name");
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MetricsEndpoint.class)));
    }

    @Test
    public void testGetMetricsType() throws Exception {
        for (MetricsType type : EnumSet.allOf(MetricsType.class)) {
            Assert.assertThat(component.getMetricsType(((type.toString()) + ":metrics-name")), Matchers.is(type));
        }
    }

    @Test
    public void testGetMetricsTypeNotSet() throws Exception {
        Assert.assertThat(component.getMetricsType("no-metrics-type"), Matchers.is(DEFAULT_METRICS_TYPE));
    }

    @Test(expected = RuntimeCamelException.class)
    public void testGetMetricsTypeNotFound() throws Exception {
        component.getMetricsType("unknown-metrics:metrics-name");
    }

    @Test
    public void testGetOrCreateMetricRegistryFoundInCamelRegistry() throws Exception {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MetricRegistry.class)).thenReturn(metricRegistry);
        MetricRegistry result = component.getOrCreateMetricRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MetricRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetOrCreateMetricRegistryFoundInCamelRegistryByType() throws Exception {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MetricRegistry.class)).thenReturn(null);
        Mockito.when(camelRegistry.findByType(MetricRegistry.class)).thenReturn(Collections.singleton(metricRegistry));
        MetricRegistry result = component.getOrCreateMetricRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MetricRegistry.class);
        inOrder.verify(camelRegistry, Mockito.times(1)).findByType(MetricRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetOrCreateMetricRegistryNotFoundInCamelRegistry() throws Exception {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MetricRegistry.class)).thenReturn(null);
        Mockito.when(camelRegistry.findByType(MetricRegistry.class)).thenReturn(Collections.<MetricRegistry>emptySet());
        MetricRegistry result = component.getOrCreateMetricRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.not(metricRegistry)));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MetricRegistry.class);
        inOrder.verify(camelRegistry, Mockito.times(1)).findByType(MetricRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetMetricRegistryFromCamelRegistry() throws Exception {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MetricRegistry.class)).thenReturn(metricRegistry);
        MetricRegistry result = component.getMetricRegistryFromCamelRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MetricRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMetricRegistry() throws Exception {
        MetricRegistry registry = component.createMetricRegistry();
        Assert.assertThat(registry, Matchers.is(Matchers.notNullValue()));
    }
}

