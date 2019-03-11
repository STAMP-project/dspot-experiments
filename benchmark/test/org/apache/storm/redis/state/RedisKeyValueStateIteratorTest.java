/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.redis.state;


import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.storm.redis.common.adapter.RedisCommandsAdapterJedis;
import org.apache.storm.redis.common.container.RedisCommandsInstanceContainer;
import org.apache.storm.state.DefaultStateEncoder;
import org.apache.storm.state.Serializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;


/**
 * Test for RedisKeyValueStateIterator.
 */
public class RedisKeyValueStateIteratorTest {
    private byte[] namespace;

    private RedisCommandsInstanceContainer mockContainer;

    private RedisCommandsAdapterJedis mockJedis;

    private int chunkSize = 1000;

    private Serializer<byte[]> keySerializer = new org.apache.storm.state.DefaultStateSerializer();

    private Serializer<byte[]> valueSerializer = new org.apache.storm.state.DefaultStateSerializer();

    private DefaultStateEncoder<byte[], byte[]> encoder;

    @Test
    public void testGetEntriesFromFirstPartOfChunkInRedis() {
        // pendingPrepare has no entries
        NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        // pendingCommit has no entries
        NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        // Redis has a chunk but no more
        NavigableMap<byte[], byte[]> chunkMap = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap, "key0".getBytes(), "value0".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key2".getBytes(), "value2".getBytes());
        ScanResult<Map.Entry<byte[], byte[]>> scanResultFirst = new ScanResult("12345".getBytes(), new ArrayList(chunkMap.entrySet()));
        ScanResult<Map.Entry<byte[], byte[]>> scanResultSecond = new ScanResult(ScanParams.SCAN_POINTER_START_BINARY, new ArrayList<Map.Entry<byte[], byte[]>>());
        Mockito.when(mockJedis.hscan(ArgumentMatchers.eq(namespace), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(ScanParams.class))).thenReturn(scanResultFirst, scanResultSecond);
        RedisKeyValueStateIterator<byte[], byte[]> kvIterator = new RedisKeyValueStateIterator(namespace, mockContainer, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        assertNextEntry(kvIterator, "key0".getBytes(), "value0".getBytes());
        // key1 shouldn't in iterator
        assertNextEntry(kvIterator, "key2".getBytes(), "value2".getBytes());
        Assert.assertFalse(kvIterator.hasNext());
    }

    @Test
    public void testGetEntriesFromThirdPartOfChunkInRedis() {
        // pendingPrepare has no entries
        NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        // pendingCommit has no entries
        NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        // Redis has three chunks which last chunk only has entries
        NavigableMap<byte[], byte[]> chunkMap = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap, "key0".getBytes(), "value0".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key2".getBytes(), "value2".getBytes());
        ScanResult<Map.Entry<byte[], byte[]>> scanResultFirst = new ScanResult("12345".getBytes(), new ArrayList<Map.Entry<byte[], byte[]>>());
        ScanResult<Map.Entry<byte[], byte[]>> scanResultSecond = new ScanResult("23456".getBytes(), new ArrayList<Map.Entry<byte[], byte[]>>());
        ScanResult<Map.Entry<byte[], byte[]>> scanResultThird = new ScanResult(ScanParams.SCAN_POINTER_START_BINARY, new ArrayList(chunkMap.entrySet()));
        Mockito.when(mockJedis.hscan(ArgumentMatchers.eq(namespace), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(ScanParams.class))).thenReturn(scanResultFirst, scanResultSecond, scanResultThird);
        RedisKeyValueStateIterator<byte[], byte[]> kvIterator = new RedisKeyValueStateIterator(namespace, mockContainer, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        assertNextEntry(kvIterator, "key0".getBytes(), "value0".getBytes());
        // key1 shouldn't in iterator
        assertNextEntry(kvIterator, "key2".getBytes(), "value2".getBytes());
        Assert.assertFalse(kvIterator.hasNext());
    }

    @Test
    public void testGetEntriesRemovingDuplicationKeys() {
        NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        putEncodedKeyValueToMap(pendingPrepare, "key0".getBytes(), "value0".getBytes());
        putTombstoneToMap(pendingPrepare, "key1".getBytes());
        NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        putEncodedKeyValueToMap(pendingCommit, "key1".getBytes(), "value1".getBytes());
        putEncodedKeyValueToMap(pendingCommit, "key2".getBytes(), "value2".getBytes());
        NavigableMap<byte[], byte[]> chunkMap = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap, "key2".getBytes(), "value2".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key3".getBytes(), "value3".getBytes());
        NavigableMap<byte[], byte[]> chunkMap2 = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap2, "key3".getBytes(), "value3".getBytes());
        putEncodedKeyValueToMap(chunkMap2, "key4".getBytes(), "value4".getBytes());
        ScanResult<Map.Entry<byte[], byte[]>> scanResultFirst = new ScanResult("12345".getBytes(), new ArrayList(chunkMap.entrySet()));
        ScanResult<Map.Entry<byte[], byte[]>> scanResultSecond = new ScanResult("23456".getBytes(), new ArrayList(chunkMap2.entrySet()));
        ScanResult<Map.Entry<byte[], byte[]>> scanResultThird = new ScanResult(ScanParams.SCAN_POINTER_START_BINARY, new ArrayList<Map.Entry<byte[], byte[]>>());
        Mockito.when(mockJedis.hscan(ArgumentMatchers.eq(namespace), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(ScanParams.class))).thenReturn(scanResultFirst, scanResultSecond, scanResultThird);
        RedisKeyValueStateIterator<byte[], byte[]> kvIterator = new RedisKeyValueStateIterator(namespace, mockContainer, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        // keys shouldn't appear twice
        assertNextEntry(kvIterator, "key0".getBytes(), "value0".getBytes());
        // key1 shouldn't be in iterator since it's marked as deleted
        assertNextEntry(kvIterator, "key2".getBytes(), "value2".getBytes());
        assertNextEntry(kvIterator, "key3".getBytes(), "value3".getBytes());
        assertNextEntry(kvIterator, "key4".getBytes(), "value4".getBytes());
        Assert.assertFalse(kvIterator.hasNext());
    }

    @Test
    public void testGetEntryNotAvailable() {
        NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        ScanResult<Map.Entry<byte[], byte[]>> scanResult = new ScanResult(ScanParams.SCAN_POINTER_START_BINARY, new ArrayList<Map.Entry<byte[], byte[]>>());
        Mockito.when(mockJedis.hscan(ArgumentMatchers.eq(namespace), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(ScanParams.class))).thenReturn(scanResult);
        RedisKeyValueStateIterator<byte[], byte[]> kvIterator = new RedisKeyValueStateIterator(namespace, mockContainer, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        Assert.assertFalse(kvIterator.hasNext());
    }
}

