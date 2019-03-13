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


import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PollingConsumerPollStrategy;
import org.junit.Assert;
import org.junit.Test;


public class ScheduledPollConsumerTest extends ContextTestSupport {
    private static boolean rollback;

    private static int counter;

    private static String event = "";

    @Test
    public void testExceptionOnPollAndCanStartAgain() throws Exception {
        final Exception expectedException = new Exception("Hello, I should be thrown on shutdown only!");
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        consumer.setPollStrategy(new PollingConsumerPollStrategy() {
            public boolean begin(Consumer consumer, Endpoint endpoint) {
                return true;
            }

            public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
            }

            public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
                if (e == expectedException) {
                    ScheduledPollConsumerTest.rollback = true;
                }
                return false;
            }
        });
        start();
        // poll that throws an exception
        run();
        stop();
        Assert.assertEquals("Should have rollback", true, ScheduledPollConsumerTest.rollback);
        // prepare for 2nd run but this time it should not thrown an exception on poll
        ScheduledPollConsumerTest.rollback = false;
        consumer.setExceptionToThrowOnPoll(null);
        // start it again and we should be able to run
        start();
        run();
        // should be able to stop with no problem
        stop();
        Assert.assertEquals("Should not have rollback", false, ScheduledPollConsumerTest.rollback);
    }

    @Test
    public void testRetryAtMostThreeTimes() throws Exception {
        ScheduledPollConsumerTest.counter = 0;
        ScheduledPollConsumerTest.event = "";
        final Exception expectedException = new Exception("Hello, I should be thrown on shutdown only!");
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        setPollStrategy(new PollingConsumerPollStrategy() {
            public boolean begin(Consumer consumer, Endpoint endpoint) {
                return true;
            }

            public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
                ScheduledPollConsumerTest.event += "commit";
            }

            public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
                ScheduledPollConsumerTest.event += "rollback";
                (ScheduledPollConsumerTest.counter)++;
                if (retryCounter < 3) {
                    return true;
                }
                return false;
            }
        });
        setUseFixedDelay(true);
        setDelay(60000);
        start();
        // poll that throws an exception
        run();
        stop();
        // 3 retries + 1 last failed attempt when we give up
        Assert.assertEquals(4, ScheduledPollConsumerTest.counter);
        Assert.assertEquals("rollbackrollbackrollbackrollback", ScheduledPollConsumerTest.event);
    }

    @Test
    public void testNoExceptionOnPoll() throws Exception {
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, null);
        start();
        run();
        stop();
    }
}

