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


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.concurrent.TimeUnit;
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
@ContextConfiguration(classes = { TimerRouteTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class TimerRouteTest {
    private static final long DELAY = 20L;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint endpoint;

    @Produce(uri = "direct:in-1")
    private ProducerTemplate producer1;

    @Produce(uri = "direct:in-2")
    private ProducerTemplate producer2;

    @Produce(uri = "direct:in-3")
    private ProducerTemplate producer3;

    private MeterRegistry registry;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {
        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:in-1").setHeader(MicrometerConstants.HEADER_METRIC_NAME, constant("B")).to("micrometer:timer:A?action=start").delay(TimerRouteTest.DELAY).setHeader(MicrometerConstants.HEADER_METRIC_NAME, constant("B")).to("micrometer:timer:A?action=stop").to("mock:out");
                    from("direct:in-2").setHeader(MicrometerConstants.HEADER_TIMER_ACTION, constant(MicrometerTimerAction.start)).to("micrometer:timer:A").delay(TimerRouteTest.DELAY).setHeader(MicrometerConstants.HEADER_TIMER_ACTION, constant(MicrometerTimerAction.stop)).to("micrometer:timer:A").to("mock:out");
                    from("direct:in-3").to("micrometer:timer:C?action=start").delay(TimerRouteTest.DELAY).to("micrometer:timer:C?action=stop&tags=a=${body}").to("mock:out");
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
        Object body = new Object();
        endpoint.expectedBodiesReceived(body);
        producer1.sendBody(body);
        Timer timer = registry.find("B").timer();
        Assert.assertEquals(1L, timer.count());
        Assert.assertTrue(((timer.max(TimeUnit.MILLISECONDS)) > 0.0));
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testOverrideNoAction() throws Exception {
        Object body = new Object();
        endpoint.expectedBodiesReceived(body);
        producer2.sendBody(body);
        Timer timer = registry.find("A").timer();
        Assert.assertEquals(1L, timer.count());
        Assert.assertTrue(((timer.max(TimeUnit.MILLISECONDS)) > 0.0));
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testNormal() throws Exception {
        int count = 10;
        String body = "Hello";
        endpoint.expectedMessageCount(count);
        for (int i = 0; i < count; i++) {
            producer3.sendBody(body);
        }
        Timer timer = registry.find("C").timer();
        Assert.assertEquals(count, timer.count());
        Assert.assertTrue(((timer.max(TimeUnit.MILLISECONDS)) > (TimerRouteTest.DELAY)));
        Assert.assertTrue(((timer.mean(TimeUnit.MILLISECONDS)) > (TimerRouteTest.DELAY)));
        Assert.assertTrue(((timer.totalTime(TimeUnit.MILLISECONDS)) > ((TimerRouteTest.DELAY) * count)));
        Assert.assertEquals(body, timer.getId().getTag("a"));
        endpoint.assertIsSatisfied();
    }
}

