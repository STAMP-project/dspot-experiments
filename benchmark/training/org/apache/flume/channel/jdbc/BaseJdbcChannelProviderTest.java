/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.channel.jdbc;


import ConfigurationConstants.CONFIG_MAX_CAPACITY;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.channel.jdbc.impl.JdbcChannelProviderImpl;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseJdbcChannelProviderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJdbcChannelProviderTest.class);

    private Context derbyCtx = new Context();

    private File derbyDbDir;

    private JdbcChannelProviderImpl provider;

    @Test
    public void testDerbyChannelCapacity() {
        provider = new JdbcChannelProviderImpl();
        derbyCtx.put(CONFIG_MAX_CAPACITY, "10");
        provider.initialize(derbyCtx);
        Set<MockEvent> events = new HashSet<MockEvent>();
        for (int i = 1; i < 12; i++) {
            events.add(MockEventUtils.generateMockEvent(i, i, i, (61 % i), 1));
        }
        Iterator<MockEvent> meIt = events.iterator();
        int count = 0;
        while (meIt.hasNext()) {
            count++;
            MockEvent me = meIt.next();
            String chName = me.getChannel();
            try {
                provider.persistEvent(chName, me);
                if (count == 11) {
                    Assert.fail();
                }
            } catch (JdbcChannelException ex) {
                // This is expected if the count is 10
                Assert.assertEquals(11, count);
            }
            // Now should be able to remove one event and add this one
            Event e = provider.removeEvent(chName);
            Assert.assertNotNull(e);
            // The current event should safely persist now
            provider.persistEvent(chName, me);
        } 
    }

    @Test
    public void testDerbySetup() {
        provider = new JdbcChannelProviderImpl();
        provider.initialize(derbyCtx);
        Transaction tx1 = provider.getTransaction();
        tx1.begin();
        Transaction tx2 = provider.getTransaction();
        Assert.assertSame(tx1, tx2);
        tx2.begin();
        tx2.close();
        tx1.close();
        Transaction tx3 = provider.getTransaction();
        Assert.assertNotSame(tx1, tx3);
        tx3.begin();
        tx3.close();
        provider.close();
        provider = null;
    }

    /**
     * Creates 120 events split over 10 channels, stores them via multiple
     * simulated sources and consumes them via multiple simulated channels.
     */
    @Test
    public void testEventWithSimulatedSourceAndSinks() throws Exception {
        provider = new JdbcChannelProviderImpl();
        provider.initialize(derbyCtx);
        Map<String, List<MockEvent>> eventMap = new HashMap<String, List<MockEvent>>();
        for (int i = 1; i < 121; i++) {
            MockEvent me = MockEventUtils.generateMockEvent(i, i, i, (61 % i), 10);
            List<MockEvent> meList = eventMap.get(me.getChannel());
            if (meList == null) {
                meList = new ArrayList<MockEvent>();
                eventMap.put(me.getChannel(), meList);
            }
            meList.add(me);
        }
        List<BaseJdbcChannelProviderTest.MockSource> sourceList = new ArrayList<BaseJdbcChannelProviderTest.MockSource>();
        List<BaseJdbcChannelProviderTest.MockSink> sinkList = new ArrayList<BaseJdbcChannelProviderTest.MockSink>();
        for (String channel : eventMap.keySet()) {
            List<MockEvent> meList = eventMap.get(channel);
            sourceList.add(new BaseJdbcChannelProviderTest.MockSource(channel, meList, provider));
            sinkList.add(new BaseJdbcChannelProviderTest.MockSink(channel, meList, provider));
        }
        ExecutorService sourceExecutor = Executors.newFixedThreadPool(10);
        ExecutorService sinkExecutor = Executors.newFixedThreadPool(10);
        List<Future<Integer>> srcResults = sourceExecutor.invokeAll(sourceList, 300, TimeUnit.SECONDS);
        Thread.sleep(MockEventUtils.generateSleepInterval(3000));
        List<Future<Integer>> sinkResults = sinkExecutor.invokeAll(sinkList, 300, TimeUnit.SECONDS);
        int srcCount = 0;
        for (Future<Integer> srcOutput : srcResults) {
            srcCount += srcOutput.get();
        }
        Assert.assertEquals(120, srcCount);
        int sinkCount = 0;
        for (Future<Integer> sinkOutput : sinkResults) {
            sinkCount += sinkOutput.get();
        }
        Assert.assertEquals(120, sinkCount);
    }

    /**
     * creates 80 events split over 5 channels, stores them
     */
    @Test
    public void testPeristingEvents() {
        provider = new JdbcChannelProviderImpl();
        provider.initialize(derbyCtx);
        Map<String, List<MockEvent>> eventMap = new HashMap<String, List<MockEvent>>();
        Set<MockEvent> events = new HashSet<MockEvent>();
        for (int i = 1; i < 81; i++) {
            events.add(MockEventUtils.generateMockEvent(i, i, i, (61 % i), 5));
        }
        Iterator<MockEvent> meIt = events.iterator();
        while (meIt.hasNext()) {
            MockEvent me = meIt.next();
            String chName = me.getChannel();
            List<MockEvent> eventList = eventMap.get(chName);
            if (eventList == null) {
                eventList = new ArrayList<MockEvent>();
                eventMap.put(chName, eventList);
            }
            eventList.add(me);
            provider.persistEvent(me.getChannel(), me);
        } 
        // Now retrieve the events and they should be in the persistence order
        for (String chName : eventMap.keySet()) {
            List<MockEvent> meList = eventMap.get(chName);
            Iterator<MockEvent> it = meList.iterator();
            while (it.hasNext()) {
                MockEvent me = it.next();
                Event event = provider.removeEvent(chName);
                BaseJdbcChannelProviderTest.assertEquals(me, event);
            } 
            // Now the there should be no more events for this channel
            Event nullEvent = provider.removeEvent(chName);
            Assert.assertNull(nullEvent);
        }
        provider.close();
        provider = null;
    }

    private static class MockSink implements Callable<Integer> {
        private final String channel;

        private final List<MockEvent> events;

        private final JdbcChannelProviderImpl provider;

        private MockSink(String channel, List<MockEvent> events, JdbcChannelProviderImpl provider) {
            this.channel = channel;
            this.events = events;
            this.provider = provider;
        }

        @Override
        public Integer call() throws Exception {
            BaseJdbcChannelProviderTest.LOGGER.debug((("Sink for channel[" + (channel)) + "]: starting"));
            if ((events) == null) {
                return 0;
            }
            Iterator<MockEvent> it = events.iterator();
            while (it.hasNext()) {
                MockEvent me = it.next();
                Event event = null;
                while (event == null) {
                    event = provider.removeEvent(channel);
                    if (event == null) {
                        BaseJdbcChannelProviderTest.LOGGER.debug((("Sink for channel[" + (channel)) + "]: empty queue"));
                        try {
                            Thread.sleep(MockEventUtils.generateSleepInterval(1000));
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        BaseJdbcChannelProviderTest.LOGGER.debug(((("Sink for channel[" + (channel)) + "]: removed event: ") + event));
                    }
                } 
                BaseJdbcChannelProviderTest.assertEquals(me, event);
            } 
            BaseJdbcChannelProviderTest.LOGGER.debug((("Sink for channel[" + (channel)) + "]: retrieved all events"));
            return events.size();
        }
    }

    private static class MockSource implements Callable<Integer> {
        private final String channel;

        private final List<MockEvent> events;

        private final JdbcChannelProviderImpl provider;

        private MockSource(String channel, List<MockEvent> events, JdbcChannelProviderImpl provider) {
            this.channel = channel;
            this.events = events;
            this.provider = provider;
        }

        @Override
        public Integer call() throws Exception {
            BaseJdbcChannelProviderTest.LOGGER.debug((("Source for channel[" + (channel)) + "]: starting"));
            if ((events) == null) {
                return 0;
            }
            Iterator<MockEvent> it = events.iterator();
            while (it.hasNext()) {
                MockEvent me = it.next();
                Assert.assertEquals(channel, me.getChannel());
                provider.persistEvent(channel, me);
                try {
                    Thread.sleep(MockEventUtils.generateSleepInterval(1000));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } 
            BaseJdbcChannelProviderTest.LOGGER.debug((("Source for channel[" + (channel)) + "]: submitted all events"));
            return events.size();
        }
    }
}

