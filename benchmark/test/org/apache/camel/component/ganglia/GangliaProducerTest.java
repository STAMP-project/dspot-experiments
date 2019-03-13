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
package org.apache.camel.component.ganglia;


import GangliaConstants.GROUP_NAME;
import GangliaConstants.METRIC_DMAX;
import GangliaConstants.METRIC_NAME;
import GangliaConstants.METRIC_SLOPE;
import GangliaConstants.METRIC_TMAX;
import GangliaConstants.METRIC_TYPE;
import GangliaConstants.METRIC_UNITS;
import info.ganglia.gmetric4j.Publisher;
import info.ganglia.gmetric4j.gmetric.GMetricSlope;
import info.ganglia.gmetric4j.gmetric.GMetricType;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GangliaProducerTest {
    private static final String BODY = "2.0";

    private static final String PREFIX = "prefix";

    private static final String GROUP_NAME = "groupName";

    private static final String METRIC_NAME = "wheelbase";

    private static final GMetricType TYPE = GMetricType.DOUBLE;

    private static final GMetricSlope SLOPE = GMetricSlope.POSITIVE;

    private static final String UNITS = "meter";

    private static final int T_MAX = 1;

    private static final int D_MAX = 2;

    private static final String CONF_GROUP_NAME = "confGroupName";

    private static final String CONF_METRIC_NAME = "confWheelbase";

    private static final GMetricType CONF_TYPE = GMetricType.INT8;

    private static final GMetricSlope CONF_SLOPE = GMetricSlope.NEGATIVE;

    private static final String CONF_UNITS = "kelvin";

    private static final int CONF_T_MAX = 3;

    private static final int CONF_D_MAX = 4;

    @Mock
    private Publisher mockPublisher;

    @Mock
    private GangliaEndpoint mockEndpoint;

    @Mock
    private Exchange mockExchange;

    @Mock
    private Message mockMessage;

    @Mock
    private GangliaConfiguration mockConf;

    private Map<String, Object> mockHeaders;

    private GangliaProducer gangliaProducer;

    @Test
    public void processMessageHeadersShouldSucceed() throws Exception {
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processEmptyDoubleShouldPublishNan() throws Exception {
        Mockito.when(mockMessage.getBody(String.class)).thenReturn("");
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, "NaN", GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processWithPrefixShouldPublishPrefix() throws Exception {
        Mockito.when(mockConf.getPrefix()).thenReturn(GangliaProducerTest.PREFIX);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, (((GangliaProducerTest.PREFIX) + "_") + (GangliaProducerTest.METRIC_NAME)), GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutGroupNameShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(GangliaConstants.GROUP_NAME);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.CONF_GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutMetricNameShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(GangliaConstants.METRIC_NAME);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.CONF_METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutMetricTypeShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(METRIC_TYPE);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.CONF_TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutMetricSlopeShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(METRIC_SLOPE);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.CONF_SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutMetricUnitsShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(METRIC_UNITS);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.CONF_UNITS);
    }

    @Test
    public void processMessageWithoutMetricTMaxShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(METRIC_TMAX);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.CONF_T_MAX, GangliaProducerTest.D_MAX, GangliaProducerTest.UNITS);
    }

    @Test
    public void processMessageWithoutMetricDMaxShouldPublishEndpointLevelConfiguration() throws Exception {
        mockHeaders.remove(METRIC_DMAX);
        gangliaProducer.process(mockExchange);
        Mockito.verify(mockPublisher).publish(GangliaProducerTest.GROUP_NAME, GangliaProducerTest.METRIC_NAME, GangliaProducerTest.BODY, GangliaProducerTest.TYPE, GangliaProducerTest.SLOPE, GangliaProducerTest.T_MAX, GangliaProducerTest.CONF_D_MAX, GangliaProducerTest.UNITS);
    }
}

