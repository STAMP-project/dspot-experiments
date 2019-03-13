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


import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.metrics.GaugeProducer.CamelMetricsGauge;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { GaugeRouteTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class GaugeRouteTest {
    private static SortedMap<String, Gauge> mockGauges = new TreeMap<>();

    @EndpointInject(uri = "mock:out")
    private MockEndpoint endpoint;

    @Produce(uri = "direct:in-1")
    private ProducerTemplate producer1;

    @Produce(uri = "direct:in-2")
    private ProducerTemplate producer2;

    private MetricRegistry mockRegistry;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {
        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:in-1").to("metrics:gauge:A?subject=#mySubject").to("mock:out");
                    from("direct:in-2").setHeader(MetricsConstants.HEADER_METRIC_NAME, constant("B")).setHeader(MetricsConstants.HEADER_GAUGE_SUBJECT, constant("my overriding subject")).to("metrics:gauge:A?subject=#mySubject").to("mock:out");
                }
            };
        }

        @Bean(name = MetricsComponent.METRIC_REGISTRY_NAME)
        public MetricRegistry getMetricRegistry() {
            MetricRegistry registry = Mockito.mock(MetricRegistry.class);
            Mockito.when(registry.getGauges()).thenReturn(GaugeRouteTest.mockGauges);
            Mockito.when(registry.register(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then(new Answer<CamelMetricsGauge>() {
                @Override
                public CamelMetricsGauge answer(InvocationOnMock invocation) throws Throwable {
                    GaugeRouteTest.mockGauges.put(invocation.getArgument(0), invocation.getArgument(1));
                    return invocation.getArgument(1);
                }
            });
            return registry;
        }

        @Bean(name = "mySubject")
        public String getSubject() {
            return "my subject";
        }
    }

    @Test
    public void testDefault() throws Exception {
        endpoint.expectedMessageCount(1);
        producer1.sendBody(new Object());
        endpoint.assertIsSatisfied();
        Mockito.verify(mockRegistry, Mockito.times(1)).register(ArgumentMatchers.eq("A"), ArgumentMatchers.argThat(new ArgumentMatcher<CamelMetricsGauge>() {
            @Override
            public boolean matches(CamelMetricsGauge argument) {
                return "my subject".equals(argument.getValue());
            }
        }));
    }

    @Test
    public void testOverride() throws Exception {
        Mockito.verify(mockRegistry, Mockito.times(1)).register(ArgumentMatchers.eq("A"), ArgumentMatchers.argThat(new ArgumentMatcher<CamelMetricsGauge>() {
            @Override
            public boolean matches(CamelMetricsGauge argument) {
                return "my subject".equals(argument.getValue());
            }
        }));
        endpoint.expectedMessageCount(1);
        producer2.sendBody(new Object());
        endpoint.assertIsSatisfied();
        Mockito.verify(mockRegistry, Mockito.times(1)).register(ArgumentMatchers.eq("B"), ArgumentMatchers.argThat(new ArgumentMatcher<CamelMetricsGauge>() {
            @Override
            public boolean matches(CamelMetricsGauge argument) {
                return "my overriding subject".equals(argument.getValue());
            }
        }));
    }
}

