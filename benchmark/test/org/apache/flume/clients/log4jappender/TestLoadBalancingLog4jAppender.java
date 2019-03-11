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
package org.apache.flume.clients.log4jappender;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Assert;
import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.source.AvroSource;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.flume.source.avro.Status;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;


public class TestLoadBalancingLog4jAppender {
    private final List<TestLoadBalancingLog4jAppender.CountingAvroSource> sources = Lists.newArrayList();

    private Channel ch;

    private ChannelSelector rcs;

    private Logger fixture;

    private boolean slowDown = false;

    @Test
    public void testLog4jAppenderRoundRobin() throws IOException {
        int numberOfMsgs = 1000;
        int expectedPerSource = 500;
        String propertiesFile = "flume-loadbalancinglog4jtest.properties";
        startSources(propertiesFile, false, TestLoadBalancingLog4jAppender.getFreePorts(2));
        sendAndAssertMessages(numberOfMsgs);
        for (TestLoadBalancingLog4jAppender.CountingAvroSource source : sources) {
            Assert.assertEquals(expectedPerSource, source.appendCount.get());
        }
    }

    @Test
    public void testLog4jAppenderRandom() throws IOException {
        int numberOfMsgs = 1000;
        String propertiesFile = "flume-loadbalancing-rnd-log4jtest.properties";
        startSources(propertiesFile, false, TestLoadBalancingLog4jAppender.getFreePorts(10));
        sendAndAssertMessages(numberOfMsgs);
        int total = 0;
        Set<Integer> counts = new HashSet<Integer>();
        for (TestLoadBalancingLog4jAppender.CountingAvroSource source : sources) {
            total += source.appendCount.intValue();
            counts.add(source.appendCount.intValue());
        }
        // We are not testing distribution this is tested in the client
        Assert.assertTrue(("Very unusual distribution " + (counts.size())), ((counts.size()) > 2));
        Assert.assertTrue("Missing events", (total == numberOfMsgs));
    }

    @Test
    public void testRandomBackoff() throws Exception {
        String propertiesFile = "flume-loadbalancing-backoff-log4jtest.properties";
        startSources(propertiesFile, false, TestLoadBalancingLog4jAppender.getFreePorts(3));
        sources.get(0).setFail();
        sources.get(2).setFail();
        sendAndAssertMessages(50);
        Assert.assertEquals(50, sources.get(1).appendCount.intValue());
        Assert.assertEquals(0, sources.get(0).appendCount.intValue());
        Assert.assertEquals(0, sources.get(2).appendCount.intValue());
        sources.get(0).setOk();
        sources.get(1).setFail();// s0 should still be backed off

        try {
            send(1);
            // nothing should be able to process right now
            Assert.fail("Expected EventDeliveryException");
        } catch (FlumeException e) {
            Assert.assertTrue(((e.getCause()) instanceof EventDeliveryException));
        }
        Thread.sleep(2500);// wait for s0 to no longer be backed off

        sendAndAssertMessages(50);
        Assert.assertEquals(50, sources.get(0).appendCount.intValue());
        Assert.assertEquals(50, sources.get(1).appendCount.intValue());
        Assert.assertEquals(0, sources.get(2).appendCount.intValue());
    }

    @Test
    public void testRandomBackoffUnsafeMode() throws Exception {
        String propertiesFile = "flume-loadbalancing-backoff-log4jtest.properties";
        startSources(propertiesFile, true, TestLoadBalancingLog4jAppender.getFreePorts(3));
        sources.get(0).setFail();
        sources.get(1).setFail();
        sources.get(2).setFail();
        sendAndAssertFail();
    }

    @Test(expected = EventDeliveryException.class)
    public void testTimeout() throws Throwable {
        String propertiesFile = "flume-loadbalancinglog4jtest.properties";
        ch = new TestLog4jAppender.SlowMemoryChannel(2000);
        configureChannel();
        slowDown = true;
        startSources(propertiesFile, false, TestLoadBalancingLog4jAppender.getFreePorts(3));
        int level = 20000;
        String msg = "This is log message number" + (String.valueOf(level));
        try {
            fixture.log(Level.toLevel(level), msg);
        } catch (FlumeException ex) {
            throw ex.getCause();
        }
    }

    @Test(expected = EventDeliveryException.class)
    public void testRandomBackoffNotUnsafeMode() throws Throwable {
        String propertiesFile = "flume-loadbalancing-backoff-log4jtest.properties";
        startSources(propertiesFile, false, TestLoadBalancingLog4jAppender.getFreePorts(3));
        sources.get(0).setFail();
        sources.get(1).setFail();
        sources.get(2).setFail();
        try {
            sendAndAssertFail();
        } catch (FlumeException ex) {
            throw ex.getCause();
        }
    }

    static class CountingAvroSource extends AvroSource {
        AtomicInteger appendCount = new AtomicInteger();

        volatile boolean isFail = false;

        private final int port2;

        public CountingAvroSource(int port) {
            port2 = port;
        }

        public void setOk() {
            this.isFail = false;
        }

        public void setFail() {
            this.isFail = true;
        }

        @Override
        public String getName() {
            return "testing..." + (port2);
        }

        @Override
        public Status append(AvroFlumeEvent avroEvent) {
            if (isFail) {
                return Status.FAILED;
            }
            appendCount.incrementAndGet();
            return super.append(avroEvent);
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) {
            if (isFail) {
                return Status.FAILED;
            }
            appendCount.addAndGet(events.size());
            return super.appendBatch(events);
        }
    }
}

