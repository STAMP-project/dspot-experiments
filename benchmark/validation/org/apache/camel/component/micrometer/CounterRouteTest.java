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


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CounterRouteTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class CounterRouteTest {
    @EndpointInject(uri = "mock:out")
    private MockEndpoint endpoint;

    @Produce(uri = "direct:in-1")
    private ProducerTemplate producer1;

    @Produce(uri = "direct:in-2")
    private ProducerTemplate producer2;

    @Produce(uri = "direct:in-3")
    private ProducerTemplate producer3;

    @Produce(uri = "direct:in-4")
    private ProducerTemplate producer4;

    private MeterRegistry registry;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {
        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:in-1").to("micrometer:counter:A?increment=5").to("mock:out");
                    from("direct:in-2").to("micrometer:counter:B?decrement=9").to("mock:out");
                    from("direct:in-3").setHeader(MicrometerConstants.HEADER_COUNTER_INCREMENT, constant(417L)).to("micrometer:counter:C").to("mock:out");
                    from("direct:in-4").to("micrometer:counter:D?increment=${body.length}&tags=a=${body.length}").to("mock:out");
                }
            };
        }

        @Bean(name = MicrometerConstants.METRICS_REGISTRY_NAME)
        public MeterRegistry getMetricRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Test
    public void testOverrideMetricsName() throws Exception {
        endpoint.expectedMessageCount(1);
        producer1.sendBodyAndHeader(new Object(), MicrometerConstants.HEADER_METRIC_NAME, "A1");
        Assert.assertEquals(5.0, registry.find("A1").counter().count(), 0.01);
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testOverrideIncrement() throws Exception {
        endpoint.expectedMessageCount(1);
        producer1.sendBodyAndHeader(new Object(), MicrometerConstants.HEADER_COUNTER_INCREMENT, 14.0);
        Assert.assertEquals(14.0, registry.find("A").counter().count(), 0.01);
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testOverrideDecrement() throws Exception {
        endpoint.expectedMessageCount(1);
        producer2.sendBodyAndHeader(new Object(), MicrometerConstants.HEADER_COUNTER_DECREMENT, 7.0);
        Assert.assertEquals((-7.0), registry.find("B").counter().count(), 0.01);
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testOverrideUsingConstantValue() throws Exception {
        endpoint.expectedMessageCount(1);
        producer3.sendBody(new Object());
        Assert.assertEquals(417.0, registry.find("C").counter().count(), 0.01);
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testUsingScriptEvaluation() throws Exception {
        endpoint.expectedMessageCount(1);
        String message = "Hello from Camel Metrics!";
        producer4.sendBody(message);
        Counter counter = registry.find("D").counter();
        Assert.assertEquals(message.length(), counter.count(), 0.01);
        Assert.assertEquals(Integer.toString(message.length()), counter.getId().getTag("a"));
        endpoint.assertIsSatisfied();
    }
}

