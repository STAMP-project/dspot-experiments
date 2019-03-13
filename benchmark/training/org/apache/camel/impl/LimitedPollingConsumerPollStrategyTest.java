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
package org.apache.camel.impl;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.support.service.ServiceHelper;
import org.junit.Assert;
import org.junit.Test;


public class LimitedPollingConsumerPollStrategyTest extends ContextTestSupport {
    private LimitedPollingConsumerPollStrategy strategy;

    @Test
    public void testLimitedPollingConsumerPollStrategy() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(3);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        start();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        stop();
    }

    @Test
    public void testLimitAtTwoLimitedPollingConsumerPollStrategy() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(2);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        start();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        stop();
    }

    @Test
    public void testLimitedPollingConsumerPollStrategySuccess() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(3);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        start();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        // now force success
        consumer.setExceptionToThrowOnPoll(null);
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        stop();
    }

    @Test
    public void testLimitedPollingConsumerPollStrategySuccessThenFail() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(3);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        start();
        // fail 2 times
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        // now force success 2 times
        consumer.setExceptionToThrowOnPoll(null);
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        // now fail again, after hitting limit at 3
        consumer.setExceptionToThrowOnPoll(expectedException);
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        stop();
    }

    @Test
    public void testTwoConsumersLimitedPollingConsumerPollStrategy() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(3);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        MockScheduledPollConsumer consumer2 = new MockScheduledPollConsumer(endpoint, null);
        consumer2.setPollStrategy(strategy);
        start();
        start();
        run();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        Assert.assertTrue("Should still be started", isStarted());
        run();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        Assert.assertTrue("Should still be started", isStarted());
        run();
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        Assert.assertTrue("Should still be started", isStarted());
        stop();
        stop();
    }

    @Test
    public void testRestartManuallyLimitedPollingConsumerPollStrategy() throws Exception {
        Exception expectedException = new Exception("Hello");
        strategy = new LimitedPollingConsumerPollStrategy();
        strategy.setLimit(3);
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(strategy);
        start();
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        // now start the consumer again
        ServiceHelper.resumeService(consumer);
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should be suspended", isSuspended());
        // now start the consumer again
        ServiceHelper.resumeService(consumer);
        // and let it succeed
        consumer.setExceptionToThrowOnPoll(null);
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        run();
        Assert.assertTrue("Should still be started", isStarted());
        stop();
    }
}

