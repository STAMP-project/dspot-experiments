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
package org.apache.flink.runtime.taskexecutor;


import TaskManagerOptions.MANAGED_MEMORY_FRACTION;
import TaskManagerOptions.MANAGED_MEMORY_SIZE;
import TaskManagerOptions.MEMORY_OFF_HEAP;
import TaskManagerOptions.MEMORY_SEGMENT_SIZE;
import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_FRACTION;
import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MAX;
import TaskManagerOptions.NETWORK_BUFFERS_MEMORY_MIN;
import TaskManagerOptions.NETWORK_NUM_BUFFERS;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link TaskManagerServices}.
 */
public class TaskManagerServicesTest extends TestLogger {
    /**
     * Test for {@link TaskManagerServices#calculateNetworkBufferMemory(long, Configuration)} using old
     * configurations via {@link TaskManagerOptions#NETWORK_NUM_BUFFERS}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void calculateNetworkBufOld() {
        Configuration config = new Configuration();
        config.setInteger(NETWORK_NUM_BUFFERS, 1);
        // note: actual network buffer memory size is independent of the totalJavaMemorySize
        Assert.assertEquals(MemorySize.parse(MEMORY_SEGMENT_SIZE.defaultValue()).getBytes(), TaskManagerServices.calculateNetworkBufferMemory((10L << 20), config));
        Assert.assertEquals(MemorySize.parse(MEMORY_SEGMENT_SIZE.defaultValue()).getBytes(), TaskManagerServices.calculateNetworkBufferMemory((64L << 20), config));
        // test integer overflow in the memory size
        int numBuffers = ((int) ((2L << 32) / (MemorySize.parse(MEMORY_SEGMENT_SIZE.defaultValue()).getBytes())));// 2^33

        config.setInteger(NETWORK_NUM_BUFFERS, numBuffers);
        Assert.assertEquals((2L << 32), TaskManagerServices.calculateNetworkBufferMemory((2L << 33), config));
    }

    /**
     * Test for {@link TaskManagerServices#calculateNetworkBufferMemory(long, Configuration)} using new
     * configurations via {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_FRACTION},
     * {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_MIN} and
     * {@link TaskManagerOptions#NETWORK_BUFFERS_MEMORY_MAX}.
     */
    @Test
    public void calculateNetworkBufNew() throws Exception {
        Configuration config = new Configuration();
        // (1) defaults
        final Float defaultFrac = NETWORK_BUFFERS_MEMORY_FRACTION.defaultValue();
        final Long defaultMin = MemorySize.parse(NETWORK_BUFFERS_MEMORY_MIN.defaultValue()).getBytes();
        final Long defaultMax = MemorySize.parse(NETWORK_BUFFERS_MEMORY_MAX.defaultValue()).getBytes();
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 20))), defaultMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((64L << (20 + 1)), config));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 30))), defaultMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((10L << 30), config));
        TaskManagerServicesTest.calculateNetworkBufNew(config);
    }

    /**
     * Test for {@link TaskManagerServices#calculateNetworkBufferMemory(long, Configuration)} using mixed
     * old/new configurations.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void calculateNetworkBufMixed() throws Exception {
        Configuration config = new Configuration();
        config.setInteger(NETWORK_NUM_BUFFERS, 1);
        final Float defaultFrac = NETWORK_BUFFERS_MEMORY_FRACTION.defaultValue();
        final Long defaultMin = MemorySize.parse(NETWORK_BUFFERS_MEMORY_MIN.defaultValue()).getBytes();
        final Long defaultMax = MemorySize.parse(NETWORK_BUFFERS_MEMORY_MAX.defaultValue()).getBytes();
        // old + 1 new parameter = new:
        Configuration config1 = config.clone();
        config1.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (0.1F * (10L << 20))), defaultMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((64L << (20 + 1)), config1));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (0.1F * (10L << 30))), defaultMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((10L << 30), config1));
        config1 = config.clone();
        long newMin = MemorySize.parse(MEMORY_SEGMENT_SIZE.defaultValue()).getBytes();// smallest value possible

        config1.setString(NETWORK_BUFFERS_MEMORY_MIN, String.valueOf(newMin));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 20))), newMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((10L << 20), config1));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 30))), newMin, defaultMax), TaskManagerServices.calculateNetworkBufferMemory((10L << 30), config1));
        config1 = config.clone();
        long newMax = Math.max((64L << (20 + 1)), MemorySize.parse(NETWORK_BUFFERS_MEMORY_MIN.defaultValue()).getBytes());
        config1.setString(NETWORK_BUFFERS_MEMORY_MAX, String.valueOf(newMax));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 20))), defaultMin, newMax), TaskManagerServices.calculateNetworkBufferMemory((64L << (20 + 1)), config1));
        Assert.assertEquals(TaskManagerServicesTest.enforceBounds(((long) (defaultFrac * (10L << 30))), defaultMin, newMax), TaskManagerServices.calculateNetworkBufferMemory((10L << 30), config1));
        Assert.assertTrue(TaskManagerServicesConfiguration.hasNewNetworkBufConf(config1));
        // old + any new parameter = new:
        TaskManagerServicesTest.calculateNetworkBufNew(config);
    }

    /**
     * Test for {@link TaskManagerServices#calculateHeapSizeMB(long, Configuration)} with some
     * manually calculated scenarios.
     */
    @Test
    public void calculateHeapSizeMB() throws Exception {
        Configuration config = new Configuration();
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        config.setString(NETWORK_BUFFERS_MEMORY_MIN, String.valueOf((64L << 20)));// 64MB

        config.setString(NETWORK_BUFFERS_MEMORY_MAX, String.valueOf((1L << 30)));// 1GB

        config.setBoolean(MEMORY_OFF_HEAP, false);
        Assert.assertEquals(900, TaskManagerServices.calculateHeapSizeMB(1000, config));
        config.setBoolean(MEMORY_OFF_HEAP, false);
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.2F);
        Assert.assertEquals(800, TaskManagerServices.calculateHeapSizeMB(1000, config));
        config.setBoolean(MEMORY_OFF_HEAP, true);
        config.setFloat(NETWORK_BUFFERS_MEMORY_FRACTION, 0.1F);
        config.setString(MANAGED_MEMORY_SIZE, "10m");// 10MB

        Assert.assertEquals(890, TaskManagerServices.calculateHeapSizeMB(1000, config));
        config.setString(MANAGED_MEMORY_SIZE, "0");// use fraction of given memory

        config.setFloat(MANAGED_MEMORY_FRACTION, 0.1F);// 10%

        Assert.assertEquals(810, TaskManagerServices.calculateHeapSizeMB(1000, config));
    }
}

