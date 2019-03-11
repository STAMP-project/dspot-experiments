/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.storm.metricstore.rocksdb;


import KeyType.COMPONENT_STRING;
import KeyType.TOPOLOGY_STRING;
import org.apache.storm.metricstore.AggLevel;
import org.junit.Assert;
import org.junit.Test;

import static KeyType.COMPONENT_STRING;
import static KeyType.TOPOLOGY_STRING;
import static RocksDbKey.KEY_SIZE;


public class RocksDbKeyTest {
    @Test
    public void testConstructors() {
        byte[] raw = new byte[KEY_SIZE];
        raw[0] = COMPONENT_STRING.getValue();
        raw[2] = 1;
        raw[3] = 2;
        raw[4] = 3;
        raw[5] = 4;
        RocksDbKey rawKey = new RocksDbKey(raw);
        RocksDbKey metadataKey = new RocksDbKey(COMPONENT_STRING, 16909060);
        Assert.assertEquals(0, metadataKey.compareTo(rawKey));
        Assert.assertEquals(COMPONENT_STRING, metadataKey.getType());
        metadataKey = new RocksDbKey(TOPOLOGY_STRING, 16909060);
        Assert.assertTrue(((metadataKey.compareTo(rawKey)) < 0));
        Assert.assertEquals(TOPOLOGY_STRING, metadataKey.getType());
        metadataKey = new RocksDbKey(COMPONENT_STRING, 16909061);
        Assert.assertTrue(((metadataKey.compareTo(rawKey)) > 0));
        Assert.assertEquals(16909060, rawKey.getTopologyId());
        Assert.assertEquals(COMPONENT_STRING, rawKey.getType());
    }

    @Test
    public void testMetricKey() {
        AggLevel aggLevel = AggLevel.AGG_LEVEL_10_MIN;
        int topologyId = 284261;
        long timestamp = System.currentTimeMillis();
        int metricId = -208576460;
        int componentId = -2104405967;
        int executorId = 4409144;
        int hostId = 70550420;
        int port = 3456;
        int streamId = -2078140074;
        RocksDbKey key = RocksDbKey.createMetricKey(aggLevel, topologyId, timestamp, metricId, componentId, executorId, hostId, port, streamId);
        Assert.assertEquals(topologyId, key.getTopologyId());
        Assert.assertEquals(timestamp, key.getTimestamp());
        Assert.assertEquals(metricId, key.getMetricId());
        Assert.assertEquals(componentId, key.getComponentId());
        Assert.assertEquals(executorId, key.getExecutorId());
        Assert.assertEquals(hostId, key.getHostnameId());
        Assert.assertEquals(port, key.getPort());
        Assert.assertEquals(streamId, key.getStreamId());
    }
}

