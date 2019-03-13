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
package org.apache.camel.builder;


import java.util.List;
import org.apache.camel.Channel;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.TestSupport;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.apache.camel.processor.DeadLetterChannel;
import org.apache.camel.processor.FilterProcessor;
import org.apache.camel.processor.RedeliveryPolicy;
import org.apache.camel.processor.SendProcessor;
import org.junit.Assert;
import org.junit.Test;


public class ErrorHandlerTest extends TestSupport {
    @Test
    public void testOverloadingTheDefaultErrorHandler() throws Exception {
        // START SNIPPET: e1
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                // use logging error handler
                errorHandler(deadLetterChannel("log:com.mycompany.foo"));
                // here is our regular route
                from("seda:a").to("seda:b");
            }
        };
        // END SNIPPET: e1
        List<Route> list = TestSupport.getRouteList(builder);
        Assert.assertEquals(("Number routes created" + list), 1, list.size());
        for (Route route : list) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "seda://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumerRoute = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumerRoute.getProcessor());
            TestSupport.assertIsInstanceOf(DeadLetterChannel.class, channel.getErrorHandler());
            Processor processor = TestSupport.unwrap(channel.getNextProcessor());
            TestSupport.assertIsInstanceOf(SendProcessor.class, processor);
        }
    }

    @Test
    public void testOverloadingTheHandlerOnASingleRoute() throws Exception {
        // START SNIPPET: e2
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                // this route is using a nested logging error handler
                // and we continue with the routing here
                // here we configure the logging error handler
                from("seda:a").errorHandler(deadLetterChannel("log:com.mycompany.foo")).to("seda:b");
                // this route will use the default error handler (DeadLetterChannel)
                from("seda:b").to("seda:c");
            }
        };
        // END SNIPPET: e2
        List<Route> list = TestSupport.getRouteList(builder);
        Assert.assertEquals(("Number routes created" + list), 2, list.size());
    }

    @Test
    public void testConfigureDeadLetterChannel() throws Exception {
        // START SNIPPET: e3
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                // using dead letter channel with a seda queue for errors
                errorHandler(deadLetterChannel("seda:errors"));
                // here is our route
                from("seda:a").to("seda:b");
            }
        };
        // END SNIPPET: e3
        List<Route> list = TestSupport.getRouteList(builder);
        Assert.assertEquals(("Number routes created" + list), 1, list.size());
        for (Route route : list) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "seda://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumerRoute = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumerRoute.getProcessor());
            TestSupport.assertIsInstanceOf(SendProcessor.class, channel.getNextProcessor());
        }
    }

    @Test
    public void testConfigureDeadLetterChannelWithCustomRedeliveryPolicy() throws Exception {
        // START SNIPPET: e4
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                // configures dead letter channel to use seda queue for errors and use at most 2 redelveries
                // and exponential backoff
                errorHandler(deadLetterChannel("seda:errors").maximumRedeliveries(2).useExponentialBackOff());
                // here is our route
                from("seda:a").to("seda:b");
            }
        };
        // END SNIPPET: e4
        List<Route> list = TestSupport.getRouteList(builder);
        Assert.assertEquals(("Number routes created" + list), 1, list.size());
        for (Route route : list) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "seda://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumerRoute = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Processor processor = consumerRoute.getProcessor();
            Channel channel = TestSupport.unwrapChannel(processor);
            DeadLetterChannel deadLetterChannel = TestSupport.assertIsInstanceOf(DeadLetterChannel.class, channel.getErrorHandler());
            RedeliveryPolicy redeliveryPolicy = deadLetterChannel.getRedeliveryPolicy();
            Assert.assertEquals("getMaximumRedeliveries()", 2, redeliveryPolicy.getMaximumRedeliveries());
            Assert.assertEquals("isUseExponentialBackOff()", true, redeliveryPolicy.isUseExponentialBackOff());
        }
    }

    @Test
    public void testLoggingErrorHandler() throws Exception {
        // START SNIPPET: e5
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("seda:a").errorHandler(deadLetterChannel("log:FOO.BAR")).filter(TestSupport.body().isInstanceOf(String.class)).to("seda:b");
            }
        };
        // END SNIPPET: e5
        List<Route> routes = TestSupport.getRouteList(builder);
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "seda://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumerRoute = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumerRoute.getProcessor());
            TestSupport.assertIsInstanceOf(DeadLetterChannel.class, channel.getErrorHandler());
            TestSupport.assertIsInstanceOf(FilterProcessor.class, channel.getNextProcessor());
        }
    }
}

