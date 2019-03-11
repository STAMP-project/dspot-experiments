/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource.Status;
import org.apache.flume.channel.ChannelProcessor;
import org.junit.Test;
import org.mockito.Mockito;


public class TestStressSource {
    private ChannelProcessor mockProcessor;

    @Test
    public void testMaxTotalEvents() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("maxTotalEvents", "35");
        source.configure(context);
        source.start();
        for (int i = 0; i < 50; i++) {
            source.process();
        }
        Mockito.verify(mockProcessor, Mockito.times(35)).processEvent(getEvent(source));
    }

    @Test
    public void testRateLimitedEventsNoBatch() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("maxTotalEvents", "20");
        context.put("maxEventsPerSecond", "20");
        source.configure(context);
        long startTime = System.currentTimeMillis();
        source.start();
        for (int i = 0; i < 20; i++) {
            source.process();
        }
        long finishTime = System.currentTimeMillis();
        // Expecting to see within a second +/- 30% for 20 events
        Assert.assertTrue(((finishTime - startTime) < 1300));
        Assert.assertTrue(((finishTime - startTime) > 700));
        source.stop();
    }

    @Test
    public void testNonRateLimitedEventsNoBatch() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        // Test with no limit - expect to see very fast performance
        context = new Context();
        context.put("maxTotalEvents", "20");
        context.put("maxEventsPerSecond", "0");
        source.configure(context);
        long startTime = System.currentTimeMillis();
        source.start();
        for (int i = 0; i <= 20; i++) {
            source.process();
        }
        long finishTime = System.currentTimeMillis();
        Assert.assertTrue(((finishTime - startTime) < 70));
    }

    @Test
    public void testRateLimitedEventsBatch() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("maxTotalEvents", "20");
        context.put("maxEventsPerSecond", "20");
        context.put("batchSize", "3");
        source.configure(context);
        long startTime = System.currentTimeMillis();
        source.start();
        for (int i = 0; i < 20; i++) {
            source.process();
        }
        long finishTime = System.currentTimeMillis();
        // Expecting to see within a second +/- 30% for 20 events
        Assert.assertTrue(((finishTime - startTime) < 1300));
        Assert.assertTrue(((finishTime - startTime) > 700));
        source.stop();
    }

    @Test
    public void testNonRateLimitedEventsBatch() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        // Test with no limit - expect to see very fast performance
        context.put("maxTotalEvents", "20");
        context.put("maxEventsPerSecond", "0");
        source.configure(context);
        long startTime = System.currentTimeMillis();
        source.start();
        for (int i = 0; i <= 20; i++) {
            source.process();
        }
        long finishTime = System.currentTimeMillis();
        Assert.assertTrue(((finishTime - startTime) < 70));
    }

    @Test
    public void testBatchEvents() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("maxTotalEvents", "35");
        context.put("batchSize", "10");
        source.configure(context);
        source.start();
        for (int i = 0; i < 50; i++) {
            if ((source.process()) == (Status.BACKOFF)) {
                TestCase.assertTrue("Source should have sent all events in 4 batches", (i == 4));
                break;
            }
            if (i < 3) {
                Mockito.verify(mockProcessor, Mockito.times((i + 1))).processEventBatch(getLastProcessedEventList(source));
            } else {
                Mockito.verify(mockProcessor, Mockito.times(1)).processEventBatch(getLastProcessedEventList(source));
            }
        }
        long successfulEvents = getCounterGroup(source).get("events.successful");
        TestCase.assertTrue(("Number of successful events should be 35 but was " + successfulEvents), (successfulEvents == 35));
        long failedEvents = getCounterGroup(source).get("events.failed");
        TestCase.assertTrue(("Number of failure events should be 0 but was " + failedEvents), (failedEvents == 0));
    }

    @Test
    public void testBatchEventsWithoutMatTotalEvents() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("batchSize", "10");
        source.configure(context);
        source.start();
        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(("StressSource with no maxTotalEvents should not return " + (Status.BACKOFF)), ((source.process()) == (Status.BACKOFF)));
        }
        Mockito.verify(mockProcessor, Mockito.times(10)).processEventBatch(getLastProcessedEventList(source));
        long successfulEvents = getCounterGroup(source).get("events.successful");
        TestCase.assertTrue(("Number of successful events should be 100 but was " + successfulEvents), (successfulEvents == 100));
        long failedEvents = getCounterGroup(source).get("events.failed");
        TestCase.assertTrue(("Number of failure events should be 0 but was " + failedEvents), (failedEvents == 0));
    }

    @Test
    public void testMaxSuccessfulEvents() throws InterruptedException, EventDeliveryException {
        StressSource source = new StressSource();
        source.setChannelProcessor(mockProcessor);
        Context context = new Context();
        context.put("maxSuccessfulEvents", "35");
        source.configure(context);
        source.start();
        for (int i = 0; i < 10; i++) {
            source.process();
        }
        // 1 failed call, 10 successful
        Mockito.doThrow(new ChannelException("stub")).when(mockProcessor).processEvent(getEvent(source));
        source.process();
        Mockito.doNothing().when(mockProcessor).processEvent(getEvent(source));
        for (int i = 0; i < 10; i++) {
            source.process();
        }
        // 1 failed call, 50 successful
        Mockito.doThrow(new ChannelException("stub")).when(mockProcessor).processEvent(getEvent(source));
        source.process();
        Mockito.doNothing().when(mockProcessor).processEvent(getEvent(source));
        for (int i = 0; i < 50; i++) {
            source.process();
        }
        // We should have called processEvent(evt) 37 times, twice for failures
        // and twice for successful events.
        Mockito.verify(mockProcessor, Mockito.times(37)).processEvent(getEvent(source));
    }
}

