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


import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { HistogramRouteTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class HistogramRouteTest {
    @EndpointInject(uri = "mock:out")
    private MockEndpoint endpoint;

    @Produce(uri = "direct:in")
    private ProducerTemplate producer;

    private MetricRegistry mockRegistry;

    private Histogram mockHistogram;

    private InOrder inOrder;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {
        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:in").to("metrics:histogram:A?value=332491").to("mock:out");
                }
            };
        }

        @Bean(name = MetricsComponent.METRIC_REGISTRY_NAME)
        public MetricRegistry getMetricRegistry() {
            return Mockito.mock(MetricRegistry.class);
        }
    }

    @Test
    public void testOverrideMetricsName() throws Exception {
        Mockito.when(mockRegistry.histogram("B")).thenReturn(mockHistogram);
        endpoint.expectedMessageCount(1);
        producer.sendBodyAndHeader(new Object(), MetricsConstants.HEADER_METRIC_NAME, "B");
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).histogram("B");
        inOrder.verify(mockHistogram, Mockito.times(1)).update(332491L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOverrideValue() throws Exception {
        Mockito.when(mockRegistry.histogram("A")).thenReturn(mockHistogram);
        endpoint.expectedMessageCount(1);
        producer.sendBodyAndHeader(new Object(), MetricsConstants.HEADER_HISTOGRAM_VALUE, 181L);
        endpoint.assertIsSatisfied();
        inOrder.verify(mockRegistry, Mockito.times(1)).histogram("A");
        inOrder.verify(mockHistogram, Mockito.times(1)).update(181L);
        inOrder.verifyNoMoreInteractions();
    }
}

