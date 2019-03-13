/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.codahalemetricspublisher;


import com.codahale.metrics.Metric;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test the ConfigurableCodaHaleMetricFilter
 *
 * @author Simon Irving
 */
public class ConfigurableCodaHaleMetricFilterTest {
    private Metric metric = Mockito.mock(Metric.class);

    private static final DynamicPropertyFactory archiausPropertyFactory = Mockito.mock(DynamicPropertyFactory.class);

    private static final DynamicBooleanProperty DYNAMIC_BOOLEAN_TRUE = Mockito.mock(DynamicBooleanProperty.class);

    private static final DynamicBooleanProperty DYNAMIC_BOOLEAN_FALSE = Mockito.mock(DynamicBooleanProperty.class);

    @Test
    public void testMetricConfiguredInFilterWithFilterEnabled() {
        Mockito.when(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory.getBooleanProperty(ArgumentMatchers.eq("filter.graphite.metrics"), ArgumentMatchers.any(Boolean.class))).thenReturn(ConfigurableCodaHaleMetricFilterTest.DYNAMIC_BOOLEAN_TRUE);
        ConfigurableCodaHaleMetricFilter filter = new ConfigurableCodaHaleMetricFilter(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory);
        Assert.assertTrue(filter.matches("this.metric.is.allowed", metric));
    }

    @Test
    public void testMetricConfiguredInFilterWithFilterDisabled() {
        Mockito.when(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory.getBooleanProperty(ArgumentMatchers.eq("filter.graphite.metrics"), ArgumentMatchers.any(Boolean.class))).thenReturn(ConfigurableCodaHaleMetricFilterTest.DYNAMIC_BOOLEAN_FALSE);
        ConfigurableCodaHaleMetricFilter filter = new ConfigurableCodaHaleMetricFilter(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory);
        Assert.assertTrue(filter.matches("this.metric.is.allowed", metric));
    }

    @Test
    public void testMetricNotConfiguredInFilterWithFilterEnabled() {
        Mockito.when(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory.getBooleanProperty(ArgumentMatchers.eq("filter.graphite.metrics"), ArgumentMatchers.any(Boolean.class))).thenReturn(ConfigurableCodaHaleMetricFilterTest.DYNAMIC_BOOLEAN_TRUE);
        ConfigurableCodaHaleMetricFilter filter = new ConfigurableCodaHaleMetricFilter(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory);
        Assert.assertFalse(filter.matches("this.metric.is.not.allowed", metric));
    }

    @Test
    public void testMetricNotConfiguredInFilterWithFilterDisabled() {
        Mockito.when(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory.getBooleanProperty(ArgumentMatchers.eq("filter.graphite.metrics"), ArgumentMatchers.any(Boolean.class))).thenReturn(ConfigurableCodaHaleMetricFilterTest.DYNAMIC_BOOLEAN_FALSE);
        ConfigurableCodaHaleMetricFilter filter = new ConfigurableCodaHaleMetricFilter(ConfigurableCodaHaleMetricFilterTest.archiausPropertyFactory);
        Assert.assertTrue(filter.matches("this.metric.is.not.allowed", metric));
    }
}

