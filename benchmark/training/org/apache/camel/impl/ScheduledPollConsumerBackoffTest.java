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


public class ScheduledPollConsumerBackoffTest extends ContextTestSupport {
    private static int commits;

    private static int errors;

    @Test
    public void testBackoffIdle() throws Exception {
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, null);
        setBackoffMultiplier(4);
        setBackoffIdleThreshold(2);
        consumer.setPollStrategy(new PollingConsumerPollStrategy() {
            public boolean begin(Consumer consumer, Endpoint endpoint) {
                return true;
            }

            public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
                (ScheduledPollConsumerBackoffTest.commits)++;
            }

            public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
                return false;
            }
        });
        start();
        run();
        run();
        Assert.assertEquals(2, ScheduledPollConsumerBackoffTest.commits);
        // now it should backoff 4 times
        run();
        run();
        run();
        run();
        Assert.assertEquals(2, ScheduledPollConsumerBackoffTest.commits);
        // and now we poll again
        run();
        run();
        Assert.assertEquals(4, ScheduledPollConsumerBackoffTest.commits);
        // now it should backoff 4 times
        run();
        run();
        run();
        run();
        Assert.assertEquals(4, ScheduledPollConsumerBackoffTest.commits);
        run();
        Assert.assertEquals(5, ScheduledPollConsumerBackoffTest.commits);
        stop();
    }

    @Test
    public void testBackoffError() throws Exception {
        final Endpoint endpoint = getMockEndpoint("mock:foo");
        final Exception expectedException = new Exception("Hello, I should be thrown on shutdown only!");
        MockScheduledPollConsumer consumer = new MockScheduledPollConsumer(endpoint, expectedException);
        setBackoffMultiplier(4);
        setBackoffErrorThreshold(3);
        setPollStrategy(new PollingConsumerPollStrategy() {
            public boolean begin(Consumer consumer, Endpoint endpoint) {
                return true;
            }

            public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
                (ScheduledPollConsumerBackoffTest.commits)++;
            }

            public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) throws Exception {
                (ScheduledPollConsumerBackoffTest.errors)++;
                return false;
            }
        });
        start();
        run();
        run();
        run();
        Assert.assertEquals(3, ScheduledPollConsumerBackoffTest.errors);
        // now it should backoff 4 times
        run();
        run();
        run();
        run();
        Assert.assertEquals(3, ScheduledPollConsumerBackoffTest.errors);
        // and now we poll again
        run();
        run();
        run();
        Assert.assertEquals(6, ScheduledPollConsumerBackoffTest.errors);
        // now it should backoff 4 times
        run();
        run();
        run();
        run();
        Assert.assertEquals(6, ScheduledPollConsumerBackoffTest.errors);
        stop();
    }
}

