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
package org.apache.camel.component.aws.cw;


import CwConstants.METRIC_DIMENSIONS;
import CwConstants.METRIC_NAME;
import CwConstants.METRIC_NAMESPACE;
import CwConstants.METRIC_TIMESTAMP;
import CwConstants.METRIC_UNIT;
import CwConstants.METRIC_VALUE;
import ExchangePattern.InOnly;
import StandardUnit.Bytes;
import StandardUnit.Count;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class CwComponentTest extends CamelTestSupport {
    @BindToRegistry("now")
    private static final Date NOW = new Date();

    private static final Date LATER = new Date(((CwComponentTest.NOW.getTime()) + 1));

    @BindToRegistry("amazonCwClient")
    private AmazonCloudWatchClient cloudWatchClient = Mockito.mock(AmazonCloudWatchClient.class);

    @Test
    public void sendMetricFromHeaderValues() throws Exception {
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(METRIC_NAMESPACE, "camel.apache.org/overriden");
                exchange.getIn().setHeader(METRIC_NAME, "OverridenMetric");
                exchange.getIn().setHeader(METRIC_VALUE, Double.valueOf(3));
                exchange.getIn().setHeader(METRIC_UNIT, Bytes.toString());
                exchange.getIn().setHeader(METRIC_TIMESTAMP, CwComponentTest.LATER);
            }
        });
        ArgumentCaptor<PutMetricDataRequest> argument = ArgumentCaptor.forClass(PutMetricDataRequest.class);
        Mockito.verify(cloudWatchClient).putMetricData(argument.capture());
        assertEquals("camel.apache.org/overriden", argument.getValue().getNamespace());
        assertEquals("OverridenMetric", argument.getValue().getMetricData().get(0).getMetricName());
        assertEquals(Double.valueOf(3), argument.getValue().getMetricData().get(0).getValue());
        assertEquals(Bytes.toString(), argument.getValue().getMetricData().get(0).getUnit());
        assertEquals(CwComponentTest.LATER, argument.getValue().getMetricData().get(0).getTimestamp());
    }

    @Test
    public void sendManuallyCreatedMetric() throws Exception {
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                MetricDatum metricDatum = new MetricDatum().withMetricName("errorCount").withValue(Double.valueOf(0));
                exchange.getIn().setBody(metricDatum);
            }
        });
        ArgumentCaptor<PutMetricDataRequest> argument = ArgumentCaptor.forClass(PutMetricDataRequest.class);
        Mockito.verify(cloudWatchClient).putMetricData(argument.capture());
        assertEquals("errorCount", argument.getValue().getMetricData().get(0).getMetricName());
        assertEquals(Double.valueOf(0), argument.getValue().getMetricData().get(0).getValue());
    }

    @Test
    public void useDefaultValuesForMetricUnitAndMetricValue() throws Exception {
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(METRIC_NAME, "errorCount");
            }
        });
        ArgumentCaptor<PutMetricDataRequest> argument = ArgumentCaptor.forClass(PutMetricDataRequest.class);
        Mockito.verify(cloudWatchClient).putMetricData(argument.capture());
        assertEquals("errorCount", argument.getValue().getMetricData().get(0).getMetricName());
        assertEquals(Double.valueOf(1), argument.getValue().getMetricData().get(0).getValue());
        assertEquals(Count.toString(), argument.getValue().getMetricData().get(0).getUnit());
    }

    @Test
    public void setsMeticDimensions() throws Exception {
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(METRIC_NAME, "errorCount");
                Map<String, String> dimensionsMap = new LinkedHashMap<>();
                dimensionsMap.put("keyOne", "valueOne");
                dimensionsMap.put("keyTwo", "valueTwo");
                exchange.getIn().setHeader(METRIC_DIMENSIONS, dimensionsMap);
            }
        });
        ArgumentCaptor<PutMetricDataRequest> argument = ArgumentCaptor.forClass(PutMetricDataRequest.class);
        Mockito.verify(cloudWatchClient).putMetricData(argument.capture());
        List<Dimension> dimensions = argument.getValue().getMetricData().get(0).getDimensions();
        Dimension dimension = dimensions.get(0);
        assertThat(dimensions.size(), Is.is(2));
        assertEquals("keyOne", dimension.getName());
        assertEquals("valueOne", dimension.getValue());
    }
}

