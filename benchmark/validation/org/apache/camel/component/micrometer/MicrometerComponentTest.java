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
package org.apache.camel.component.micrometer;


import Meter.Type;
import MicrometerComponent.DEFAULT_METER_TYPE;
import MicrometerConstants.METRICS_REGISTRY_NAME;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.TypeConverter;
import org.apache.camel.spi.Registry;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MicrometerComponentTest {
    @Mock
    private CamelContext camelContext;

    @Mock
    private TypeConverter typeConverter;

    @Mock
    private Registry camelRegistry;

    @Mock
    private MeterRegistry metricRegistry;

    private InOrder inOrder;

    private MicrometerComponent component;

    @Test
    public void testCreateEndpoint() throws Exception {
        component.setCamelContext(camelContext);
        Mockito.when(camelContext.getRegistry()).thenReturn(camelRegistry);
        Mockito.when(camelContext.getTypeConverter()).thenReturn(typeConverter);
        Mockito.when(typeConverter.convertTo(String.class, "key=value")).thenReturn("key=value");
        Mockito.when(camelRegistry.lookupByNameAndType(METRICS_REGISTRY_NAME, MeterRegistry.class)).thenReturn(metricRegistry);
        Map<String, Object> params = new HashMap<>();
        params.put("tags", "key=value");
        Endpoint result = component.createEndpoint("micrometer:counter:counter", "counter:counter", params);
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.instanceOf(MicrometerEndpoint.class)));
        MicrometerEndpoint me = ((MicrometerEndpoint) (result));
        Assert.assertThat(me.getMetricsName(), Matchers.is("counter"));
        Assert.assertThat(me.getRegistry(), Matchers.is(metricRegistry));
        inOrder.verify(camelContext, Mockito.times(1)).getRegistry();
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType(METRICS_REGISTRY_NAME, MeterRegistry.class);
        inOrder.verify(camelContext, Mockito.times(1)).getTypeConverter();
        inOrder.verify(typeConverter, Mockito.times(1)).convertTo(String.class, "key=value");
        inOrder.verify(camelContext, Mockito.times(1)).getTypeConverter();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateNewEndpointForCounter() {
        Endpoint endpoint = new MicrometerEndpoint(null, null, metricRegistry, Type.COUNTER, "a name", Tags.empty());
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MicrometerEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForHistogram() {
        Endpoint endpoint = new MicrometerEndpoint(null, null, metricRegistry, Type.DISTRIBUTION_SUMMARY, "a name", Tags.empty());
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MicrometerEndpoint.class)));
    }

    @Test
    public void testCreateNewEndpointForTimer() {
        Endpoint endpoint = new MicrometerEndpoint(null, null, metricRegistry, Type.TIMER, "a name", Tags.empty());
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint, Matchers.is(Matchers.instanceOf(MicrometerEndpoint.class)));
    }

    @Test
    public void testGetMetricsType() {
        Meter[] supportedTypes = new Type[]{ Type.COUNTER, Type.DISTRIBUTION_SUMMARY, Type.TIMER };
        for (Meter.Type type : supportedTypes) {
            Assert.assertThat(component.getMetricsType(((MicrometerUtils.getName(type)) + ":metrics-name")), Matchers.is(type));
        }
    }

    @Test
    public void testGetMetricsTypeNotSet() {
        Assert.assertThat(component.getMetricsType("no-metrics-type"), Matchers.is(DEFAULT_METER_TYPE));
    }

    @Test(expected = RuntimeCamelException.class)
    public void testGetMetricsTypeNotFound() {
        component.getMetricsType("unknown-metrics:metrics-name");
    }

    @Test
    public void testGetOrCreateMetricRegistryFoundInCamelRegistry() {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MeterRegistry.class)).thenReturn(metricRegistry);
        MeterRegistry result = MicrometerUtils.getOrCreateMeterRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MeterRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetOrCreateMetricRegistryFoundInCamelRegistryByType() {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MeterRegistry.class)).thenReturn(null);
        Mockito.when(camelRegistry.findByType(MeterRegistry.class)).thenReturn(Collections.singleton(metricRegistry));
        MeterRegistry result = MicrometerUtils.getOrCreateMeterRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MeterRegistry.class);
        inOrder.verify(camelRegistry, Mockito.times(1)).findByType(MeterRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetOrCreateMetricRegistryNotFoundInCamelRegistry() {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MeterRegistry.class)).thenReturn(null);
        Mockito.when(camelRegistry.findByType(MeterRegistry.class)).thenReturn(Collections.emptySet());
        MeterRegistry result = MicrometerUtils.getOrCreateMeterRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(result, Matchers.is(Matchers.not(metricRegistry)));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MeterRegistry.class);
        inOrder.verify(camelRegistry, Mockito.times(1)).findByType(MeterRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetMetricRegistryFromCamelRegistry() {
        Mockito.when(camelRegistry.lookupByNameAndType("name", MeterRegistry.class)).thenReturn(metricRegistry);
        MeterRegistry result = MicrometerUtils.getMeterRegistryFromCamelRegistry(camelRegistry, "name");
        Assert.assertThat(result, Matchers.is(metricRegistry));
        inOrder.verify(camelRegistry, Mockito.times(1)).lookupByNameAndType("name", MeterRegistry.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateMetricRegistry() {
        MeterRegistry registry = MicrometerUtils.createMeterRegistry();
        Assert.assertThat(registry, Matchers.is(Matchers.notNullValue()));
    }
}

