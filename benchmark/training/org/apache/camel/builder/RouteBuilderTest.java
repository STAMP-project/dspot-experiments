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


import java.util.Iterator;
import java.util.List;
import org.apache.camel.Channel;
import org.apache.camel.DelegateProcessor;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.TestSupport;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.apache.camel.processor.ChoiceProcessor;
import org.apache.camel.processor.DeadLetterChannel;
import org.apache.camel.processor.EvaluateExpressionProcessor;
import org.apache.camel.processor.FilterProcessor;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.processor.Pipeline;
import org.apache.camel.processor.RecipientList;
import org.apache.camel.processor.SendProcessor;
import org.apache.camel.processor.Splitter;
import org.apache.camel.processor.ThreadsProcessor;
import org.apache.camel.processor.idempotent.IdempotentConsumer;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.junit.Assert;
import org.junit.Test;


public class RouteBuilderTest extends TestSupport {
    protected Processor myProcessor = new MyProcessor();

    protected DelegateProcessor interceptor1;

    protected DelegateProcessor interceptor2;

    @Test
    public void testSimpleRoute() throws Exception {
        List<Route> routes = buildSimpleRoute();
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            SendProcessor sendProcessor = TestSupport.assertIsInstanceOf(SendProcessor.class, channel.getNextProcessor());
            Assert.assertEquals("Endpoint URI", "direct://b", sendProcessor.getDestination().getEndpointUri());
        }
    }

    @Test
    public void testSimpleRouteWithHeaderPredicate() throws Exception {
        List<Route> routes = buildSimpleRouteWithHeaderPredicate();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            FilterProcessor filterProcessor = TestSupport.assertIsInstanceOf(FilterProcessor.class, channel.getNextProcessor());
            SendProcessor sendProcessor = TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(filterProcessor).getNextProcessor());
            Assert.assertEquals("Endpoint URI", "direct://b", sendProcessor.getDestination().getEndpointUri());
        }
    }

    @Test
    public void testSimpleRouteWithChoice() throws Exception {
        List<Route> routes = buildSimpleRouteWithChoice();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            ChoiceProcessor choiceProcessor = TestSupport.assertIsInstanceOf(ChoiceProcessor.class, channel.getNextProcessor());
            List<FilterProcessor> filters = choiceProcessor.getFilters();
            Assert.assertEquals("Should be two when clauses", 2, filters.size());
            Processor filter1 = filters.get(0);
            assertSendTo(TestSupport.unwrapChannel(getProcessor()).getNextProcessor(), "direct://b");
            Processor filter2 = filters.get(1);
            assertSendTo(TestSupport.unwrapChannel(getProcessor()).getNextProcessor(), "direct://c");
            assertSendTo(TestSupport.unwrapChannel(choiceProcessor.getOtherwise()).getNextProcessor(), "direct://d");
        }
    }

    @Test
    public void testCustomProcessor() throws Exception {
        List<Route> routes = buildCustomProcessor();
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
        }
    }

    @Test
    public void testCustomProcessorWithFilter() throws Exception {
        List<Route> routes = buildCustomProcessorWithFilter();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
        }
    }

    @Test
    public void testWireTap() throws Exception {
        List<Route> routes = buildWireTap();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            MulticastProcessor multicastProcessor = TestSupport.assertIsInstanceOf(MulticastProcessor.class, channel.getNextProcessor());
            List<Processor> endpoints = new java.util.ArrayList(multicastProcessor.getProcessors());
            Assert.assertEquals("Should have 2 endpoints", 2, endpoints.size());
            assertSendToProcessor(TestSupport.unwrapChannel(endpoints.get(0)).getNextProcessor(), "direct://tap");
            assertSendToProcessor(TestSupport.unwrapChannel(endpoints.get(1)).getNextProcessor(), "direct://b");
        }
    }

    @Test
    public void testRouteWithInterceptor() throws Exception {
        List<Route> routes = buildRouteWithInterceptor();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Pipeline line = TestSupport.assertIsInstanceOf(Pipeline.class, TestSupport.unwrap(consumer.getProcessor()));
            Assert.assertEquals(3, line.getProcessors().size());
            // last should be our seda
            List<Processor> processors = new java.util.ArrayList(line.getProcessors());
            Processor sendTo = TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(processors.get(2)).getNextProcessor());
            assertSendTo(sendTo, "direct://d");
        }
    }

    @Test
    public void testComplexExpressions() throws Exception {
        // START SNIPPET: e7
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                errorHandler(deadLetterChannel("mock:error"));
                from("direct:a").filter(TestSupport.header("foo").isEqualTo(123)).to("direct:b");
            }
        };
        // END SNIPPET: e7
        List<Route> routes = TestSupport.getRouteList(builder);
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
        }
    }

    @Test
    public void testRouteDynamicReceipentList() throws Exception {
        List<Route> routes = buildDynamicRecipientList();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            Pipeline line = TestSupport.assertIsInstanceOf(Pipeline.class, channel.getNextProcessor());
            Iterator<?> it = line.getProcessors().iterator();
            // EvaluateExpressionProcessor should be wrapped in error handler
            Object first = it.next();
            first = TestSupport.assertIsInstanceOf(DeadLetterChannel.class, first).getOutput();
            TestSupport.assertIsInstanceOf(EvaluateExpressionProcessor.class, first);
            // and the second should NOT be wrapped in error handler
            Object second = it.next();
            TestSupport.assertIsInstanceOf(RecipientList.class, second);
        }
    }

    @Test
    public void testSplitter() throws Exception {
        List<Route> routes = buildSplitter();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            TestSupport.assertIsInstanceOf(Splitter.class, channel.getNextProcessor());
        }
    }

    @Test
    public void testIdempotentConsumer() throws Exception {
        List<Route> routes = buildIdempotentConsumer();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Channel channel = TestSupport.unwrapChannel(consumer.getProcessor());
            IdempotentConsumer idempotentConsumer = TestSupport.assertIsInstanceOf(IdempotentConsumer.class, channel.getNextProcessor());
            Assert.assertEquals("messageIdExpression", "header(myMessageId)", idempotentConsumer.getMessageIdExpression().toString());
            TestSupport.assertIsInstanceOf(MemoryIdempotentRepository.class, idempotentConsumer.getIdempotentRepository());
            SendProcessor sendProcessor = TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(idempotentConsumer.getProcessor()).getNextProcessor());
            Assert.assertEquals("Endpoint URI", "direct://b", sendProcessor.getDestination().getEndpointUri());
        }
    }

    @Test
    public void testThreads() throws Exception {
        List<Route> routes = buildThreads();
        log.debug(("Created routes: " + routes));
        Assert.assertEquals("Number routes created", 1, routes.size());
        for (Route route : routes) {
            Endpoint key = route.getEndpoint();
            Assert.assertEquals("From endpoint", "direct://a", key.getEndpointUri());
            EventDrivenConsumerRoute consumer = TestSupport.assertIsInstanceOf(EventDrivenConsumerRoute.class, route);
            Pipeline line = TestSupport.assertIsInstanceOf(Pipeline.class, TestSupport.unwrap(consumer.getProcessor()));
            Iterator<Processor> it = line.getProcessors().iterator();
            TestSupport.assertIsInstanceOf(ThreadsProcessor.class, TestSupport.unwrapChannel(it.next()).getNextProcessor());
            TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(it.next()).getNextProcessor());
            TestSupport.assertIsInstanceOf(SendProcessor.class, TestSupport.unwrapChannel(it.next()).getNextProcessor());
        }
    }

    @Test
    public void testCorrectNumberOfRoutes() throws Exception {
        RouteBuilder builder = new RouteBuilder() {
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:error"));
                from("direct:start").to("direct:in");
                from("direct:in").to("mock:result");
            }
        };
        List<Route> routes = TestSupport.getRouteList(builder);
        Assert.assertEquals(2, routes.size());
    }
}

