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
package org.apache.flume.agent.embedded;


import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.flume.source.avro.AvroSourceProtocol;
import org.apache.flume.source.avro.Status;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestEmbeddedAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestEmbeddedAgent.class);

    private static final String HOSTNAME = "localhost";

    private static AtomicInteger serialNumber = new AtomicInteger(0);

    private EmbeddedAgent agent;

    private Map<String, String> properties;

    private TestEmbeddedAgent.EventCollector eventCollector;

    private NettyServer nettyServer;

    private Map<String, String> headers;

    private byte[] body;

    @Test(timeout = 30000L)
    public void testPut() throws Exception {
        agent.configure(properties);
        agent.start();
        agent.put(EventBuilder.withBody(body, headers));
        Event event;
        while ((event = eventCollector.poll()) == null) {
            Thread.sleep(500L);
        } 
        Assert.assertNotNull(event);
        Assert.assertArrayEquals(body, event.getBody());
        Assert.assertEquals(headers, event.getHeaders());
    }

    @Test(timeout = 30000L)
    public void testPutAll() throws Exception {
        List<Event> events = Lists.newArrayList();
        events.add(EventBuilder.withBody(body, headers));
        agent.configure(properties);
        agent.start();
        agent.putAll(events);
        Event event;
        while ((event = eventCollector.poll()) == null) {
            Thread.sleep(500L);
        } 
        Assert.assertNotNull(event);
        Assert.assertArrayEquals(body, event.getBody());
        Assert.assertEquals(headers, event.getHeaders());
    }

    @Test(timeout = 30000L)
    public void testPutWithInterceptors() throws Exception {
        properties.put("source.interceptors", "i1");
        properties.put("source.interceptors.i1.type", "static");
        properties.put("source.interceptors.i1.key", "key2");
        properties.put("source.interceptors.i1.value", "value2");
        agent.configure(properties);
        agent.start();
        agent.put(EventBuilder.withBody(body, headers));
        Event event;
        while ((event = eventCollector.poll()) == null) {
            Thread.sleep(500L);
        } 
        Assert.assertNotNull(event);
        Assert.assertArrayEquals(body, event.getBody());
        Map<String, String> newHeaders = new HashMap<String, String>(headers);
        newHeaders.put("key2", "value2");
        Assert.assertEquals(newHeaders, event.getHeaders());
    }

    @Test(timeout = 30000L)
    public void testEmbeddedAgentName() throws Exception {
        EmbeddedAgent embedAgent = new EmbeddedAgent(("test 1 2" + (TestEmbeddedAgent.serialNumber.incrementAndGet())));
        List<Event> events = Lists.newArrayList();
        events.add(EventBuilder.withBody(body, headers));
        embedAgent.configure(properties);
        embedAgent.start();
        embedAgent.putAll(events);
        Event event;
        while ((event = eventCollector.poll()) == null) {
            Thread.sleep(500L);
        } 
        Assert.assertNotNull(event);
        Assert.assertArrayEquals(body, event.getBody());
        Assert.assertEquals(headers, event.getHeaders());
        if (embedAgent != null) {
            try {
                embedAgent.stop();
            } catch (Exception e) {
                TestEmbeddedAgent.LOGGER.debug("Error shutting down agent", e);
            }
        }
    }

    static class EventCollector implements AvroSourceProtocol {
        private final Queue<AvroFlumeEvent> eventQueue = new LinkedBlockingQueue<AvroFlumeEvent>();

        public Event poll() {
            AvroFlumeEvent avroEvent = eventQueue.poll();
            if (avroEvent != null) {
                return EventBuilder.withBody(avroEvent.getBody().array(), TestEmbeddedAgent.toStringMap(avroEvent.getHeaders()));
            }
            return null;
        }

        @Override
        public Status append(AvroFlumeEvent event) throws AvroRemoteException {
            eventQueue.add(event);
            return Status.OK;
        }

        @Override
        public Status appendBatch(List<AvroFlumeEvent> events) throws AvroRemoteException {
            Preconditions.checkState(eventQueue.addAll(events));
            return Status.OK;
        }
    }
}

