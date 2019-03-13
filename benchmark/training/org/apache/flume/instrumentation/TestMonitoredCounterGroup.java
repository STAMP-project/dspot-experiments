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
package org.apache.flume.instrumentation;


import java.util.Random;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import junit.framework.Assert;
import org.junit.Test;


public class TestMonitoredCounterGroup {
    private static final int MAX_BOUNDS = 1000;

    private static final String ROOT_OBJ_NAME_PREFIX = "org.apache.flume.";

    private static final String SOURCE_OBJ_NAME_PREFIX = (TestMonitoredCounterGroup.ROOT_OBJ_NAME_PREFIX) + "source:type=";

    private static final String CHANNEL_OBJ_NAME_PREFIX = (TestMonitoredCounterGroup.ROOT_OBJ_NAME_PREFIX) + "channel:type=";

    private static final String SINK_OBJ_NAME_PREFIX = (TestMonitoredCounterGroup.ROOT_OBJ_NAME_PREFIX) + "sink:type=";

    private static final String ATTR_START_TIME = "StartTime";

    private static final String ATTR_STOP_TIME = "StopTime";

    private static final String SRC_ATTR_EVENT_RECEVIED_COUNT = "EventReceivedCount";

    private static final String SRC_ATTR_EVENT_ACCEPTED_COUNT = "EventAcceptedCount";

    private static final String SRC_ATTR_APPEND_RECEVIED_COUNT = "AppendReceivedCount";

    private static final String SRC_ATTR_APPEND_ACCEPTED_COUNT = "AppendAcceptedCount";

    private static final String SRC_ATTR_APPEND_BATCH_RECEVIED_COUNT = "AppendBatchReceivedCount";

    private static final String SRC_ATTR_APPEND_BATCH_ACCEPTED_COUNT = "AppendBatchAcceptedCount";

    private static final String CH_ATTR_CHANNEL_SIZE = "ChannelSize";

    private static final String CH_ATTR_EVENT_PUT_ATTEMPT = "EventPutAttemptCount";

    private static final String CH_ATTR_EVENT_TAKE_ATTEMPT = "EventTakeAttemptCount";

    private static final String CH_ATTR_EVENT_PUT_SUCCESS = "EventPutSuccessCount";

    private static final String CH_ATTR_EVENT_TAKE_SUCCESS = "EventTakeSuccessCount";

    private static final String SK_ATTR_CONN_CREATED = "ConnectionCreatedCount";

    private static final String SK_ATTR_CONN_CLOSED = "ConnectionClosedCount";

    private static final String SK_ATTR_CONN_FAILED = "ConnectionFailedCount";

    private static final String SK_ATTR_BATCH_EMPTY = "BatchEmptyCount";

    private static final String SK_ATTR_BATCH_UNDERFLOW = "BatchUnderflowCount";

    private static final String SK_ATTR_BATCH_COMPLETE = "BatchCompleteCount";

    private static final String SK_ATTR_EVENT_DRAIN_ATTEMPT = "EventDrainAttemptCount";

    private static final String SK_ATTR_EVENT_DRAIN_SUCCESS = "EventDrainSuccessCount";

    private MBeanServer mbServer;

    private Random random;

    @Test
    public void testSinkCounter() throws Exception {
        String name = getRandomName();
        SinkCounter skc = new SinkCounter(name);
        skc.register();
        ObjectName on = new ObjectName(((TestMonitoredCounterGroup.SINK_OBJ_NAME_PREFIX) + name));
        assertSkCounterState(on, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        skc.start();
        long start1 = getStartTime(on);
        Assert.assertTrue("StartTime", (start1 != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        int connCreated = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int connClosed = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int connFailed = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int batchEmpty = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int batchUnderflow = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int batchComplete = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int eventDrainAttempt = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int eventDrainSuccess = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        for (int i = 0; i < connCreated; i++) {
            skc.incrementConnectionCreatedCount();
        }
        for (int i = 0; i < connClosed; i++) {
            skc.incrementConnectionClosedCount();
        }
        for (int i = 0; i < connFailed; i++) {
            skc.incrementConnectionFailedCount();
        }
        for (int i = 0; i < batchEmpty; i++) {
            skc.incrementBatchEmptyCount();
        }
        for (int i = 0; i < batchUnderflow; i++) {
            skc.incrementBatchUnderflowCount();
        }
        for (int i = 0; i < batchComplete; i++) {
            skc.incrementBatchCompleteCount();
        }
        for (int i = 0; i < eventDrainAttempt; i++) {
            skc.incrementEventDrainAttemptCount();
        }
        for (int i = 0; i < eventDrainSuccess; i++) {
            skc.incrementEventDrainSuccessCount();
        }
        assertSkCounterState(on, connCreated, connClosed, connFailed, batchEmpty, batchUnderflow, batchComplete, eventDrainAttempt, eventDrainSuccess);
        skc.stop();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) != 0L));
        assertSkCounterState(on, connCreated, connClosed, connFailed, batchEmpty, batchUnderflow, batchComplete, eventDrainAttempt, eventDrainSuccess);
        // give start time a chance to increment
        Thread.sleep(5L);
        skc.start();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StartTime", ((getStartTime(on)) > start1));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        assertSkCounterState(on, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        int eventDrainAttempt2 = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int eventDrainSuccess2 = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        skc.addToEventDrainAttemptCount(eventDrainAttempt2);
        skc.addToEventDrainSuccessCount(eventDrainSuccess2);
        assertSkCounterState(on, 0L, 0L, 0L, 0L, 0L, 0L, eventDrainAttempt2, eventDrainSuccess2);
    }

    @Test
    public void testChannelCounter() throws Exception {
        String name = getRandomName();
        ChannelCounter chc = new ChannelCounter(name);
        chc.register();
        ObjectName on = new ObjectName(((TestMonitoredCounterGroup.CHANNEL_OBJ_NAME_PREFIX) + name));
        assertChCounterState(on, 0L, 0L, 0L, 0L, 0L);
        Assert.assertTrue("StartTime", ((getStartTime(on)) == 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        chc.start();
        long start1 = getStartTime(on);
        Assert.assertTrue("StartTime", (start1 != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        int numChannelSize = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventPutAttempt = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventTakeAttempt = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventPutSuccess = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventTakeSuccess = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        chc.setChannelSize(numChannelSize);
        for (int i = 0; i < numEventPutAttempt; i++) {
            chc.incrementEventPutAttemptCount();
        }
        for (int i = 0; i < numEventTakeAttempt; i++) {
            chc.incrementEventTakeAttemptCount();
        }
        chc.addToEventPutSuccessCount(numEventPutSuccess);
        chc.addToEventTakeSuccessCount(numEventTakeSuccess);
        assertChCounterState(on, numChannelSize, numEventPutAttempt, numEventTakeAttempt, numEventPutSuccess, numEventTakeSuccess);
        chc.stop();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) != 0L));
        assertChCounterState(on, numChannelSize, numEventPutAttempt, numEventTakeAttempt, numEventPutSuccess, numEventTakeSuccess);
        // give start time a chance to increment
        Thread.sleep(5L);
        chc.start();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StartTime", ((getStartTime(on)) > start1));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        assertChCounterState(on, 0L, 0L, 0L, 0L, 0L);
    }

    @Test
    public void testSourceCounter() throws Exception {
        String name = getRandomName();
        SourceCounter srcc = new SourceCounter(name);
        srcc.register();
        ObjectName on = new ObjectName(((TestMonitoredCounterGroup.SOURCE_OBJ_NAME_PREFIX) + name));
        assertSrcCounterState(on, 0L, 0L, 0L, 0L, 0L, 0L);
        Assert.assertTrue("StartTime", ((getStartTime(on)) == 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        srcc.start();
        long start1 = getStartTime(on);
        Assert.assertTrue("StartTime", (start1 != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        int numEventReceived = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventAccepted = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numAppendReceived = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numAppendAccepted = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numAppendBatchReceived = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numAppendBatchAccepted = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        srcc.addToEventReceivedCount(numEventReceived);
        srcc.addToEventAcceptedCount(numEventAccepted);
        for (int i = 0; i < numAppendReceived; i++) {
            srcc.incrementAppendReceivedCount();
        }
        for (int i = 0; i < numAppendAccepted; i++) {
            srcc.incrementAppendAcceptedCount();
        }
        for (int i = 0; i < numAppendBatchReceived; i++) {
            srcc.incrementAppendBatchReceivedCount();
        }
        for (int i = 0; i < numAppendBatchAccepted; i++) {
            srcc.incrementAppendBatchAcceptedCount();
        }
        assertSrcCounterState(on, numEventReceived, numEventAccepted, numAppendReceived, numAppendAccepted, numAppendBatchReceived, numAppendBatchAccepted);
        srcc.stop();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) != 0L));
        assertSrcCounterState(on, numEventReceived, numEventAccepted, numAppendReceived, numAppendAccepted, numAppendBatchReceived, numAppendBatchAccepted);
        // give start time a chance to increment
        Thread.sleep(5L);
        srcc.start();
        Assert.assertTrue("StartTime", ((getStartTime(on)) != 0L));
        Assert.assertTrue("StartTime", ((getStartTime(on)) > start1));
        Assert.assertTrue("StopTime", ((getStopTime(on)) == 0L));
        assertSrcCounterState(on, 0L, 0L, 0L, 0L, 0L, 0L);
        int numEventReceived2 = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        int numEventAccepted2 = random.nextInt(TestMonitoredCounterGroup.MAX_BOUNDS);
        for (int i = 0; i < numEventReceived2; i++) {
            srcc.incrementEventReceivedCount();
        }
        for (int i = 0; i < numEventAccepted2; i++) {
            srcc.incrementEventAcceptedCount();
        }
        assertSrcCounterState(on, numEventReceived2, numEventAccepted2, 0L, 0L, 0L, 0L);
    }

    @Test
    public void testRegisterTwice() throws Exception {
        String name = "re-register-" + (getRandomName());
        SourceCounter c1 = new SourceCounter(name);
        c1.register();
        ObjectName on = new ObjectName(((TestMonitoredCounterGroup.SOURCE_OBJ_NAME_PREFIX) + name));
        Assert.assertEquals("StartTime", 0L, getStartTime(on));
        Assert.assertEquals("StopTime", 0L, getStopTime(on));
        c1.start();
        c1.stop();
        Assert.assertTrue("StartTime", ((getStartTime(on)) > 0L));
        Assert.assertTrue("StopTime", ((getStopTime(on)) > 0L));
        SourceCounter c2 = new SourceCounter(name);
        c2.register();
        Assert.assertEquals("StartTime", 0L, getStartTime(on));
        Assert.assertEquals("StopTime", 0L, getStopTime(on));
    }
}

