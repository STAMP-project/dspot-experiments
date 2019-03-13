/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.io.flume.node;


import java.util.Map;
import junit.framework.Assert;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.annotations.Disposable;
import org.apache.flume.annotations.Recyclable;
import org.apache.flume.channel.AbstractChannel;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.FlumeConfiguration;
import org.apache.flume.sink.AbstractSink;
import org.apache.flume.source.AbstractSource;
import org.junit.Test;


public class TestAbstractConfigurationProvider {
    @Test
    public void testDispoableChannel() throws Exception {
        String agentName = "agent1";
        Map<String, String> properties = getPropertiesForChannel(agentName, TestAbstractConfigurationProvider.DisposableChannel.class.getName());
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config1 = getConfiguration();
        Channel channel1 = config1.getChannels().values().iterator().next();
        Assert.assertTrue((channel1 instanceof TestAbstractConfigurationProvider.DisposableChannel));
        MaterializedConfiguration config2 = getConfiguration();
        Channel channel2 = config2.getChannels().values().iterator().next();
        Assert.assertTrue((channel2 instanceof TestAbstractConfigurationProvider.DisposableChannel));
        Assert.assertNotSame(channel1, channel2);
    }

    @Test
    public void testReusableChannel() throws Exception {
        String agentName = "agent1";
        Map<String, String> properties = getPropertiesForChannel(agentName, TestAbstractConfigurationProvider.RecyclableChannel.class.getName());
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config1 = getConfiguration();
        Channel channel1 = config1.getChannels().values().iterator().next();
        Assert.assertTrue((channel1 instanceof TestAbstractConfigurationProvider.RecyclableChannel));
        MaterializedConfiguration config2 = getConfiguration();
        Channel channel2 = config2.getChannels().values().iterator().next();
        Assert.assertTrue((channel2 instanceof TestAbstractConfigurationProvider.RecyclableChannel));
        Assert.assertSame(channel1, channel2);
    }

    @Test
    public void testUnspecifiedChannel() throws Exception {
        String agentName = "agent1";
        Map<String, String> properties = getPropertiesForChannel(agentName, TestAbstractConfigurationProvider.UnspecifiedChannel.class.getName());
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config1 = getConfiguration();
        Channel channel1 = config1.getChannels().values().iterator().next();
        Assert.assertTrue((channel1 instanceof TestAbstractConfigurationProvider.UnspecifiedChannel));
        MaterializedConfiguration config2 = getConfiguration();
        Channel channel2 = config2.getChannels().values().iterator().next();
        Assert.assertTrue((channel2 instanceof TestAbstractConfigurationProvider.UnspecifiedChannel));
        Assert.assertSame(channel1, channel2);
    }

    @Test
    public void testReusableChannelNotReusedLater() throws Exception {
        String agentName = "agent1";
        Map<String, String> propertiesReusable = getPropertiesForChannel(agentName, TestAbstractConfigurationProvider.RecyclableChannel.class.getName());
        Map<String, String> propertiesDispoable = getPropertiesForChannel(agentName, TestAbstractConfigurationProvider.DisposableChannel.class.getName());
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, propertiesReusable);
        MaterializedConfiguration config1 = getConfiguration();
        Channel channel1 = config1.getChannels().values().iterator().next();
        Assert.assertTrue((channel1 instanceof TestAbstractConfigurationProvider.RecyclableChannel));
        provider.setProperties(propertiesDispoable);
        MaterializedConfiguration config2 = getConfiguration();
        Channel channel2 = config2.getChannels().values().iterator().next();
        Assert.assertTrue((channel2 instanceof TestAbstractConfigurationProvider.DisposableChannel));
        provider.setProperties(propertiesReusable);
        MaterializedConfiguration config3 = getConfiguration();
        Channel channel3 = config3.getChannels().values().iterator().next();
        Assert.assertTrue((channel3 instanceof TestAbstractConfigurationProvider.RecyclableChannel));
        Assert.assertNotSame(channel1, channel3);
    }

    @Test
    public void testSourceThrowsExceptionDuringConfiguration() throws Exception {
        String agentName = "agent1";
        String sourceType = TestAbstractConfigurationProvider.UnconfigurableSource.class.getName();
        String channelType = "memory";
        String sinkType = "null";
        Map<String, String> properties = getProperties(agentName, sourceType, channelType, sinkType);
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config = getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 0));
        Assert.assertTrue(((config.getChannels().size()) == 1));
        Assert.assertTrue(((config.getSinkRunners().size()) == 1));
    }

    @Test
    public void testChannelThrowsExceptionDuringConfiguration() throws Exception {
        String agentName = "agent1";
        String sourceType = "seq";
        String channelType = TestAbstractConfigurationProvider.UnconfigurableChannel.class.getName();
        String sinkType = "null";
        Map<String, String> properties = getProperties(agentName, sourceType, channelType, sinkType);
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config = getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 0));
        Assert.assertTrue(((config.getChannels().size()) == 0));
        Assert.assertTrue(((config.getSinkRunners().size()) == 0));
    }

    @Test
    public void testSinkThrowsExceptionDuringConfiguration() throws Exception {
        String agentName = "agent1";
        String sourceType = "seq";
        String channelType = "memory";
        String sinkType = TestAbstractConfigurationProvider.UnconfigurableSink.class.getName();
        Map<String, String> properties = getProperties(agentName, sourceType, channelType, sinkType);
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config = getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 1));
        Assert.assertTrue(((config.getChannels().size()) == 1));
        Assert.assertTrue(((config.getSinkRunners().size()) == 0));
    }

    @Test
    public void testSourceAndSinkThrowExceptionDuringConfiguration() throws Exception {
        String agentName = "agent1";
        String sourceType = TestAbstractConfigurationProvider.UnconfigurableSource.class.getName();
        String channelType = "memory";
        String sinkType = TestAbstractConfigurationProvider.UnconfigurableSink.class.getName();
        Map<String, String> properties = getProperties(agentName, sourceType, channelType, sinkType);
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config = getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 0));
        Assert.assertTrue(((config.getChannels().size()) == 0));
        Assert.assertTrue(((config.getSinkRunners().size()) == 0));
    }

    @Test
    public void testSinkSourceMismatchDuringConfiguration() throws Exception {
        String agentName = "agent1";
        String sourceType = "seq";
        String channelType = "memory";
        String sinkType = "avro";
        Map<String, String> properties = getProperties(agentName, sourceType, channelType, sinkType);
        properties.put((agentName + ".channels.channel1.capacity"), "1000");
        properties.put((agentName + ".channels.channel1.transactionCapacity"), "1000");
        properties.put((agentName + ".sources.source1.batchSize"), "1000");
        properties.put((agentName + ".sinks.sink1.batch-size"), "1000");
        properties.put((agentName + ".sinks.sink1.hostname"), "10.10.10.10");
        properties.put((agentName + ".sinks.sink1.port"), "1010");
        TestAbstractConfigurationProvider.MemoryConfigurationProvider provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        MaterializedConfiguration config = getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 1));
        Assert.assertTrue(((config.getChannels().size()) == 1));
        Assert.assertTrue(((config.getSinkRunners().size()) == 1));
        properties.put((agentName + ".sources.source1.batchSize"), "1001");
        properties.put((agentName + ".sinks.sink1.batch-size"), "1000");
        provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        config = provider.getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 0));
        Assert.assertTrue(((config.getChannels().size()) == 1));
        Assert.assertTrue(((config.getSinkRunners().size()) == 1));
        properties.put((agentName + ".sources.source1.batchSize"), "1000");
        properties.put((agentName + ".sinks.sink1.batch-size"), "1001");
        provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        config = provider.getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 1));
        Assert.assertTrue(((config.getChannels().size()) == 1));
        Assert.assertTrue(((config.getSinkRunners().size()) == 0));
        properties.put((agentName + ".sources.source1.batchSize"), "1001");
        properties.put((agentName + ".sinks.sink1.batch-size"), "1001");
        provider = new TestAbstractConfigurationProvider.MemoryConfigurationProvider(agentName, properties);
        config = provider.getConfiguration();
        Assert.assertTrue(((config.getSourceRunners().size()) == 0));
        Assert.assertTrue(((config.getChannels().size()) == 0));
        Assert.assertTrue(((config.getSinkRunners().size()) == 0));
    }

    public static class MemoryConfigurationProvider extends AbstractConfigurationProvider {
        private Map<String, String> properties;

        public MemoryConfigurationProvider(String agentName, Map<String, String> properties) {
            super(agentName);
            this.properties = properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        @Override
        protected FlumeConfiguration getFlumeConfiguration() {
            return new FlumeConfiguration(properties);
        }
    }

    @Disposable
    public static class DisposableChannel extends AbstractChannel {
        @Override
        public void put(Event event) throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Event take() throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Transaction getTransaction() {
            throw new UnsupportedOperationException();
        }
    }

    @Recyclable
    public static class RecyclableChannel extends AbstractChannel {
        @Override
        public void put(Event event) throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Event take() throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Transaction getTransaction() {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnspecifiedChannel extends AbstractChannel {
        @Override
        public void put(Event event) throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Event take() throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Transaction getTransaction() {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnconfigurableChannel extends AbstractChannel {
        @Override
        public void configure(Context context) {
            throw new RuntimeException("expected");
        }

        @Override
        public void put(Event event) throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Event take() throws ChannelException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Transaction getTransaction() {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnconfigurableSource extends AbstractSource implements Configurable {
        @Override
        public void configure(Context context) {
            throw new RuntimeException("expected");
        }
    }

    public static class UnconfigurableSink extends AbstractSink implements Configurable {
        @Override
        public void configure(Context context) {
            throw new RuntimeException("expected");
        }

        @Override
        public Status process() throws EventDeliveryException {
            throw new UnsupportedOperationException();
        }
    }
}

