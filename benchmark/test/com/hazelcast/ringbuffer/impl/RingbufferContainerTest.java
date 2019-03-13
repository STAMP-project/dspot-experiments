/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.ringbuffer.impl;


import InMemoryFormat.BINARY;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RingbufferContainerTest extends HazelcastTestSupport {
    private SerializationService serializationService;

    private NodeEngineImpl nodeEngine;

    // ======================= construction =======================
    @Test
    public void constructionNoTTL() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100).setTimeToLiveSeconds(0);
        RingbufferContainer container = getRingbufferContainer(config);
        Assert.assertEquals(config.getCapacity(), container.getCapacity());
        Assert.assertNull(container.getExpirationPolicy());
        Assert.assertSame(config, container.getConfig());
        ArrayRingbuffer ringbuffer = ((ArrayRingbuffer) (container.getRingbuffer()));
        Assert.assertNotNull(ringbuffer.getItems());
        Assert.assertEquals(config.getCapacity(), ringbuffer.getItems().length);
        Assert.assertEquals((-1), ringbuffer.tailSequence());
        Assert.assertEquals(0, ringbuffer.headSequence());
    }

    @Test
    public void constructionWithTTL() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100).setTimeToLiveSeconds(30);
        RingbufferContainer ringbuffer = getRingbufferContainer(config);
        Assert.assertEquals(config.getCapacity(), ringbuffer.getCapacity());
        Assert.assertNotNull(ringbuffer.getExpirationPolicy());
        Assert.assertSame(config, ringbuffer.getConfig());
        Assert.assertEquals(config.getCapacity(), ringbuffer.getExpirationPolicy().ringExpirationMs.length);
        Assert.assertSame(config, ringbuffer.getConfig());
        Assert.assertEquals((-1), ringbuffer.tailSequence());
        Assert.assertEquals(0, ringbuffer.headSequence());
    }

    @Test
    public void remainingCapacity_whenTTLDisabled() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100).setTimeToLiveSeconds(0);
        RingbufferContainer<Data, Data> ringbuffer = getRingbufferContainer(config);
        Assert.assertEquals(config.getCapacity(), ringbuffer.remainingCapacity());
        ringbuffer.add(toData("1"));
        ringbuffer.add(toData("2"));
        Assert.assertEquals(config.getCapacity(), ringbuffer.remainingCapacity());
    }

    @Test
    public void remainingCapacity_whenTTLEnabled() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100).setTimeToLiveSeconds(1);
        RingbufferContainer<Data, Data> ringbuffer = getRingbufferContainer(config);
        Assert.assertEquals(config.getCapacity(), ringbuffer.remainingCapacity());
        ringbuffer.add(toData("1"));
        Assert.assertEquals(((config.getCapacity()) - 1), ringbuffer.remainingCapacity());
        ringbuffer.add(toData("2"));
        Assert.assertEquals(((config.getCapacity()) - 2), ringbuffer.remainingCapacity());
    }

    // ======================= size =======================
    @Test
    public void size_whenEmpty() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100);
        RingbufferContainer ringbuffer = getRingbufferContainer(config);
        Assert.assertEquals(0, ringbuffer.size());
        Assert.assertTrue(ringbuffer.isEmpty());
    }

    @Test
    public void size_whenAddingManyItems() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(100);
        RingbufferContainer<Data, Data> ringbuffer = getRingbufferContainer(config);
        for (int k = 0; k < (config.getCapacity()); k++) {
            ringbuffer.add(toData(""));
            Assert.assertEquals((k + 1), ringbuffer.size());
        }
        Assert.assertFalse(ringbuffer.isEmpty());
        // at this point the ringbuffer is full. So if we add more items, the oldest item is overwritten
        // and therefor the size remains the same
        for (int k = 0; k < (config.getCapacity()); k++) {
            ringbuffer.add(toData(""));
            Assert.assertEquals(config.getCapacity(), ringbuffer.size());
        }
    }

    // ======================= add =======================
    @Test
    public void add() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(10);
        RingbufferContainer<Data, Data> ringbuffer = getRingbufferContainer(config);
        ringbuffer.add(toData("foo"));
        ringbuffer.add(toData("bar"));
        Assert.assertEquals(1, ringbuffer.tailSequence());
        Assert.assertEquals(0, ringbuffer.headSequence());
    }

    @Test
    public void add_whenWrapped() {
        RingbufferConfig config = new RingbufferConfig("foo").setInMemoryFormat(OBJECT).setCapacity(3);
        RingbufferContainer<Data, String> ringbuffer = getRingbufferContainer(config);
        ringbuffer.add(toData("1"));
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(0, ringbuffer.tailSequence());
        Assert.assertEquals(toData("1"), ringbuffer.readAsData(0));
        ringbuffer.add(toData("2"));
        Assert.assertEquals(1, ringbuffer.tailSequence());
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(toData("1"), ringbuffer.readAsData(0));
        Assert.assertEquals(toData("2"), ringbuffer.readAsData(1));
        ringbuffer.add(toData("3"));
        Assert.assertEquals(2, ringbuffer.tailSequence());
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(toData("1"), ringbuffer.readAsData(0));
        Assert.assertEquals(toData("2"), ringbuffer.readAsData(1));
        Assert.assertEquals(toData("3"), ringbuffer.readAsData(2));
        ringbuffer.add(toData("4"));
        Assert.assertEquals(3, ringbuffer.tailSequence());
        Assert.assertEquals(1, ringbuffer.headSequence());
        Assert.assertEquals(toData("2"), ringbuffer.readAsData(1));
        Assert.assertEquals(toData("3"), ringbuffer.readAsData(2));
        Assert.assertEquals(toData("4"), ringbuffer.readAsData(3));
        ringbuffer.add(toData("5"));
        Assert.assertEquals(4, ringbuffer.tailSequence());
        Assert.assertEquals(2, ringbuffer.headSequence());
        Assert.assertEquals(toData("3"), ringbuffer.readAsData(2));
        Assert.assertEquals(toData("4"), ringbuffer.readAsData(3));
        Assert.assertEquals(toData("5"), ringbuffer.readAsData(4));
    }

    @Test(expected = StaleSequenceException.class)
    public void read_whenStaleSequence() {
        RingbufferConfig config = new RingbufferConfig("foo").setCapacity(3);
        RingbufferContainer<Data, Data> ringbuffer = getRingbufferContainer(config);
        ringbuffer.add(toData("1"));
        ringbuffer.add(toData("2"));
        ringbuffer.add(toData("3"));
        // this one will overwrite the first item
        ringbuffer.add(toData("4"));
        ringbuffer.readAsData(0);
    }

    @Test
    public void add_whenBinaryInMemoryFormat() {
        RingbufferConfig config = new RingbufferConfig("foo").setInMemoryFormat(BINARY);
        RingbufferContainer<Data, Data> container = getRingbufferContainer(config);
        ArrayRingbuffer ringbuffer = ((ArrayRingbuffer) (container.getRingbuffer()));
        container.add(toData("foo"));
        HazelcastTestSupport.assertInstanceOf(Data.class, ringbuffer.getItems()[0]);
    }

    @Test
    public void add_inObjectInMemoryFormat() {
        RingbufferConfig config = new RingbufferConfig("foo").setInMemoryFormat(OBJECT);
        RingbufferContainer<Object, String> container = getRingbufferContainer(config);
        ArrayRingbuffer ringbuffer = ((ArrayRingbuffer) (container.getRingbuffer()));
        container.add("foo");
        HazelcastTestSupport.assertInstanceOf(String.class, ringbuffer.getItems()[0]);
        container.add(toData("bar"));
        HazelcastTestSupport.assertInstanceOf(String.class, ringbuffer.getItems()[1]);
    }
}

