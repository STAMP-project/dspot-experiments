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
package org.apache.hadoop.yarn.event;


import YarnConfiguration.DISPATCHER_DRAIN_EVENTS_TIMEOUT;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestAsyncDispatcher {
    /* This test checks whether dispatcher hangs on close if following two things
    happen :
    1. A thread which was putting event to event queue is interrupted.
    2. Event queue is empty on close.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 10000)
    public void testDispatcherOnCloseIfQueueEmpty() throws Exception {
        BlockingQueue<Event> eventQueue = Mockito.spy(new LinkedBlockingQueue<Event>());
        Event event = Mockito.mock(Event.class);
        Mockito.doThrow(new InterruptedException()).when(eventQueue).put(event);
        DrainDispatcher disp = new DrainDispatcher(eventQueue);
        disp.init(new Configuration());
        setDrainEventsOnStop();
        start();
        // Wait for event handler thread to start and begin waiting for events.
        disp.waitForEventThreadToWait();
        try {
            disp.getEventHandler().handle(event);
            Assert.fail("Expected YarnRuntimeException");
        } catch (YarnRuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof InterruptedException));
        }
        // Queue should be empty and dispatcher should not hang on close
        Assert.assertTrue("Event Queue should have been empty", eventQueue.isEmpty());
        close();
    }

    // Test dispatcher should timeout on draining events.
    @Test(timeout = 10000)
    public void testDispatchStopOnTimeout() throws Exception {
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
        eventQueue = Mockito.spy(eventQueue);
        // simulate dispatcher is not drained.
        Mockito.when(eventQueue.isEmpty()).thenReturn(false);
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(DISPATCHER_DRAIN_EVENTS_TIMEOUT, 2000);
        DrainDispatcher disp = new DrainDispatcher(eventQueue);
        disp.init(conf);
        setDrainEventsOnStop();
        start();
        disp.waitForEventThreadToWait();
        close();
    }

    @SuppressWarnings("rawtypes")
    private static class DummyHandler implements EventHandler<Event> {
        @Override
        public void handle(Event event) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }

    private enum DummyType {

        DUMMY;}

    // Test if drain dispatcher drains events on stop.
    @SuppressWarnings({ "rawtypes" })
    @Test(timeout = 10000)
    public void testDrainDispatcherDrainEventsOnStop() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setInt(DISPATCHER_DRAIN_EVENTS_TIMEOUT, 2000);
        BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
        DrainDispatcher disp = new DrainDispatcher(queue);
        disp.init(conf);
        register(TestAsyncDispatcher.DummyType.class, new TestAsyncDispatcher.DummyHandler());
        setDrainEventsOnStop();
        start();
        disp.waitForEventThreadToWait();
        dispatchDummyEvents(disp, 2);
        close();
        Assert.assertEquals(0, queue.size());
    }
}

