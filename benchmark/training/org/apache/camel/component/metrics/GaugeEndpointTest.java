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
import org.apache.camel.Producer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GaugeEndpointTest {
    private static final String METRICS_NAME = "metrics.name";

    private static final Object VALUE = "subject";

    @Mock
    private MetricRegistry registry;

    private MetricsEndpoint endpoint;

    @Test
    public void testGaugeEndpoint() throws Exception {
        Assert.assertThat(endpoint.getRegistry(), Matchers.is(registry));
        Assert.assertThat(endpoint.getMetricsName(), Matchers.is(GaugeEndpointTest.METRICS_NAME));
        Assert.assertThat(endpoint.getSubject(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testCreateProducer() throws Exception {
        Producer producer = endpoint.createProducer();
        Assert.assertThat(producer, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(producer, Matchers.is(Matchers.instanceOf(GaugeProducer.class)));
    }

    @Test
    public void testGetSubject() throws Exception {
        Assert.assertThat(endpoint.getSubject(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSetSubject() throws Exception {
        endpoint.setSubject(GaugeEndpointTest.VALUE);
        Assert.assertThat(endpoint.getSubject(), Matchers.is(GaugeEndpointTest.VALUE));
    }
}

