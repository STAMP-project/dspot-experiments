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
package org.apache.camel.processor.aggregator;


import Exchange.AGGREGATED_COMPLETED_BY;
import Exchange.BATCH_COMPLETE;
import Exchange.BATCH_INDEX;
import Exchange.BATCH_SIZE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.BodyInAggregatingStrategy;
import org.apache.camel.processor.aggregate.AggregateProcessor;
import org.apache.camel.spi.ExceptionHandler;
import org.junit.Assert;
import org.junit.Test;


public class AggregateProcessorTest extends ContextTestSupport {
    private ExecutorService executorService;

    @Test
    public void testAggregateProcessorCompletionPredicate() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+B+END");
        mock.expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "predicate");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        Predicate complete = TestSupport.body().contains("END");
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionPredicate(complete);
        ap.setEagerCheckCompletion(false);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 123);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("END");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("D");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateProcessorCompletionPredicateEager() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+B+END");
        mock.expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "predicate");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        Predicate complete = TestSupport.body().isEqualTo("END");
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionPredicate(complete);
        ap.setEagerCheckCompletion(true);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 123);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("END");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("D");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateProcessorCompletionAggregatedSize() throws Exception {
        doTestAggregateProcessorCompletionAggregatedSize(false);
    }

    @Test
    public void testAggregateProcessorCompletionAggregatedSizeEager() throws Exception {
        doTestAggregateProcessorCompletionAggregatedSize(true);
    }

    @Test
    public void testAggregateProcessorCompletionTimeout() throws Exception {
        doTestAggregateProcessorCompletionTimeout(false);
    }

    @Test
    public void testAggregateProcessorCompletionTimeoutEager() throws Exception {
        doTestAggregateProcessorCompletionTimeout(true);
    }

    @Test
    public void testAggregateCompletionInterval() throws Exception {
        // camel context must be started
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+B+C", "D");
        mock.expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "interval");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionInterval(100);
        ap.setCompletionTimeoutCheckerInterval(10);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 123);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("C");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("D");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        Thread.sleep(250);
        ap.process(e4);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateIgnoreInvalidCorrelationKey() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+C+END");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        Predicate complete = TestSupport.body().contains("END");
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionPredicate(complete);
        ap.setIgnoreInvalidCorrelationKeys(true);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("C");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("END");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateBadCorrelationKey() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+C+END");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        Predicate complete = TestSupport.body().contains("END");
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionPredicate(complete);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("C");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("END");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        Exception e = e2.getException();
        Assert.assertNotNull(e);
        Assert.assertTrue(e.getMessage().startsWith("Invalid correlation key."));
        ap.process(e3);
        ap.process(e4);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateCloseCorrelationKeyOnCompletion() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+B+END");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        Predicate complete = TestSupport.body().contains("END");
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionPredicate(complete);
        ap.setCloseCorrelationKeyOnCompletion(1000);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 123);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("END");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("C");
        e4.getIn().setHeader("id", 123);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        Exception e = e4.getException();
        Assert.assertNotNull(e);
        Assert.assertTrue(e.getMessage().startsWith("The correlation key [123] has been closed."));
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateUseBatchSizeFromConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A+B", "C+D+E");
        mock.expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "consumer");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionSize(100);
        ap.setCompletionFromBatchConsumer(true);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        e1.setProperty(BATCH_INDEX, 0);
        e1.setProperty(BATCH_SIZE, 2);
        e1.setProperty(BATCH_COMPLETE, false);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 123);
        e2.setProperty(BATCH_INDEX, 1);
        e2.setProperty(BATCH_SIZE, 2);
        e2.setProperty(BATCH_COMPLETE, true);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("C");
        e3.getIn().setHeader("id", 123);
        e3.setProperty(BATCH_INDEX, 0);
        e3.setProperty(BATCH_SIZE, 3);
        e3.setProperty(BATCH_COMPLETE, false);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("D");
        e4.getIn().setHeader("id", 123);
        e4.setProperty(BATCH_INDEX, 1);
        e4.setProperty(BATCH_SIZE, 3);
        e4.setProperty(BATCH_COMPLETE, false);
        Exchange e5 = new org.apache.camel.support.DefaultExchange(context);
        e5.getIn().setBody("E");
        e5.getIn().setHeader("id", 123);
        e5.setProperty(BATCH_INDEX, 2);
        e5.setProperty(BATCH_SIZE, 3);
        e5.setProperty(BATCH_COMPLETE, true);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        ap.process(e5);
        assertMockEndpointsSatisfied();
        ap.stop();
    }

    @Test
    public void testAggregateLogFailedExchange() throws Exception {
        doTestAggregateLogFailedExchange(null);
    }

    @Test
    public void testAggregateHandleFailedExchange() throws Exception {
        final AtomicBoolean tested = new AtomicBoolean();
        ExceptionHandler myHandler = new ExceptionHandler() {
            public void handleException(Throwable exception) {
            }

            public void handleException(String message, Throwable exception) {
            }

            public void handleException(String message, Exchange exchange, Throwable exception) {
                Assert.assertEquals("Error processing aggregated exchange", message);
                Assert.assertEquals("B+Kaboom+END", exchange.getIn().getBody());
                Assert.assertEquals("Damn", exception.getMessage());
                tested.set(true);
            }
        };
        doTestAggregateLogFailedExchange(myHandler);
        Assert.assertEquals(true, tested.get());
    }

    @Test
    public void testAggregateForceCompletion() throws Exception {
        // camel context must be started
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceivedInAnyOrder("B+END", "A+END");
        mock.expectedPropertyReceived(AGGREGATED_COMPLETED_BY, "force");
        AsyncProcessor done = new org.apache.camel.processor.SendProcessor(context.getEndpoint("mock:result"));
        Expression corr = TestSupport.header("id");
        AggregationStrategy as = new BodyInAggregatingStrategy();
        AggregateProcessor ap = new AggregateProcessor(context, done, corr, as, executorService, true);
        ap.setCompletionSize(10);
        ap.start();
        Exchange e1 = new org.apache.camel.support.DefaultExchange(context);
        e1.getIn().setBody("A");
        e1.getIn().setHeader("id", 123);
        Exchange e2 = new org.apache.camel.support.DefaultExchange(context);
        e2.getIn().setBody("B");
        e2.getIn().setHeader("id", 456);
        Exchange e3 = new org.apache.camel.support.DefaultExchange(context);
        e3.getIn().setBody("END");
        e3.getIn().setHeader("id", 123);
        Exchange e4 = new org.apache.camel.support.DefaultExchange(context);
        e4.getIn().setBody("END");
        e4.getIn().setHeader("id", 456);
        ap.process(e1);
        ap.process(e2);
        ap.process(e3);
        ap.process(e4);
        Assert.assertEquals("should not have completed yet", 0, mock.getExchanges().size());
        ap.forceCompletionOfAllGroups();
        assertMockEndpointsSatisfied();
        ap.stop();
    }
}

