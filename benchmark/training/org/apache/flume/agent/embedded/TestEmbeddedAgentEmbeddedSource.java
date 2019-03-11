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


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.SinkRunner;
import org.apache.flume.SourceRunner;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.node.MaterializedConfiguration;
import org.junit.Test;
import org.mockito.Mockito;


public class TestEmbeddedAgentEmbeddedSource {
    private EmbeddedAgent agent;

    private Map<String, String> properties;

    private MaterializedConfiguration config;

    private EmbeddedSource source;

    private SourceRunner sourceRunner;

    private Channel channel;

    private SinkRunner sinkRunner;

    @Test
    public void testStart() {
        agent.configure(properties);
        agent.start();
        Mockito.verify(sourceRunner, Mockito.times(1)).start();
        Mockito.verify(channel, Mockito.times(1)).start();
        Mockito.verify(sinkRunner, Mockito.times(1)).start();
    }

    @Test
    public void testStop() {
        agent.configure(properties);
        agent.start();
        agent.stop();
        Mockito.verify(sourceRunner, Mockito.times(1)).stop();
        Mockito.verify(channel, Mockito.times(1)).stop();
        Mockito.verify(sinkRunner, Mockito.times(1)).stop();
    }

    @Test
    public void testStartSourceThrowsException() {
        Mockito.doThrow(new TestEmbeddedAgentEmbeddedSource.LocalRuntimeException()).when(sourceRunner).start();
        startExpectingLocalRuntimeException();
    }

    @Test
    public void testStartChannelThrowsException() {
        Mockito.doThrow(new TestEmbeddedAgentEmbeddedSource.LocalRuntimeException()).when(channel).start();
        startExpectingLocalRuntimeException();
    }

    @Test
    public void testStartSinkThrowsException() {
        Mockito.doThrow(new TestEmbeddedAgentEmbeddedSource.LocalRuntimeException()).when(sinkRunner).start();
        startExpectingLocalRuntimeException();
    }

    private static class LocalRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 116546244849853151L;
    }

    @Test
    public void testPut() throws EventDeliveryException {
        Event event = new SimpleEvent();
        agent.configure(properties);
        agent.start();
        agent.put(event);
        Mockito.verify(source, Mockito.times(1)).put(event);
    }

    @Test
    public void testPutAll() throws EventDeliveryException {
        Event event = new SimpleEvent();
        List<Event> events = Lists.newArrayList();
        events.add(event);
        agent.configure(properties);
        agent.start();
        agent.putAll(events);
        Mockito.verify(source, Mockito.times(1)).putAll(events);
    }

    @Test(expected = IllegalStateException.class)
    public void testPutNotStarted() throws EventDeliveryException {
        Event event = new SimpleEvent();
        agent.configure(properties);
        agent.put(event);
    }

    @Test(expected = IllegalStateException.class)
    public void testPutAllNotStarted() throws EventDeliveryException {
        Event event = new SimpleEvent();
        List<Event> events = Lists.newArrayList();
        events.add(event);
        agent.configure(properties);
        agent.putAll(events);
    }
}

