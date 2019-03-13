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


import MicrometerTimerAction.start;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.Producer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TimerEndpointTest {
    private static final String METRICS_NAME = "metrics.name";

    @Mock
    private MeterRegistry registry;

    private MicrometerEndpoint endpoint;

    private InOrder inOrder;

    @Test
    public void testTimerEndpoint() {
        Assert.assertThat(endpoint, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(endpoint.getRegistry(), Matchers.is(registry));
        Assert.assertThat(endpoint.getMetricsName(), Matchers.is(TimerEndpointTest.METRICS_NAME));
    }

    @Test
    public void testCreateProducer() throws Exception {
        Producer producer = endpoint.createProducer();
        Assert.assertThat(producer, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(producer, Matchers.is(Matchers.instanceOf(TimerProducer.class)));
    }

    @Test
    public void testGetAction() {
        Assert.assertThat(endpoint.getAction(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSetAction() {
        Assert.assertThat(endpoint.getAction(), Matchers.is(Matchers.nullValue()));
        endpoint.setAction(start.name());
        Assert.assertThat(MicrometerTimerAction.valueOf(endpoint.getAction()), Matchers.is(start));
    }
}

