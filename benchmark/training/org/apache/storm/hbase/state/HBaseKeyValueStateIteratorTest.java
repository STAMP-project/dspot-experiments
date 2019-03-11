/**
 * Copyright 2016 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.hbase.state;


import java.util.NavigableMap;
import org.apache.storm.hbase.common.HBaseClient;
import org.apache.storm.state.DefaultStateEncoder;
import org.apache.storm.state.Serializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for HBaseKeyValueStateIterator.
 */
public class HBaseKeyValueStateIteratorTest {
    private String namespace;

    private byte[] keyNamespace;

    private byte[] columnFamily;

    private HBaseClient mockHBaseClient;

    private int chunkSize = 1000;

    private Serializer<byte[]> keySerializer = new org.apache.storm.state.DefaultStateSerializer();

    private Serializer<byte[]> valueSerializer = new org.apache.storm.state.DefaultStateSerializer();

    private DefaultStateEncoder<byte[], byte[]> encoder;

    @Test
    public void testGetEntriesInHBase() throws Exception {
        // pendingPrepare has no entries
        final NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        // pendingCommit has no entries
        final NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        // HBase has some entries
        NavigableMap<byte[], byte[]> chunkMap = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap, "key0".getBytes(), "value0".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key2".getBytes(), "value2".getBytes());
        applyPendingStateToHBase(chunkMap);
        HBaseKeyValueStateIterator<byte[], byte[]> kvIterator = new HBaseKeyValueStateIterator(namespace, columnFamily, mockHBaseClient, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        assertNextEntry(kvIterator, "key0".getBytes(), "value0".getBytes());
        // key1 shouldn't in iterator
        assertNextEntry(kvIterator, "key2".getBytes(), "value2".getBytes());
        Assert.assertFalse(kvIterator.hasNext());
    }

    @Test
    public void testGetEntriesRemovingDuplicationKeys() throws Exception {
        NavigableMap<byte[], byte[]> pendingPrepare = getBinaryTreeMap();
        putEncodedKeyValueToMap(pendingPrepare, "key0".getBytes(), "value0".getBytes());
        putTombstoneToMap(pendingPrepare, "key1".getBytes());
        NavigableMap<byte[], byte[]> pendingCommit = getBinaryTreeMap();
        putEncodedKeyValueToMap(pendingCommit, "key1".getBytes(), "value1".getBytes());
        putEncodedKeyValueToMap(pendingCommit, "key2".getBytes(), "value2".getBytes());
        NavigableMap<byte[], byte[]> chunkMap = getBinaryTreeMap();
        putEncodedKeyValueToMap(chunkMap, "key2".getBytes(), "value2".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key3".getBytes(), "value3".getBytes());
        putEncodedKeyValueToMap(chunkMap, "key4".getBytes(), "value4".getBytes());
        applyPendingStateToHBase(chunkMap);
        HBaseKeyValueStateIterator<byte[], byte[]> kvIterator = new HBaseKeyValueStateIterator(namespace, columnFamily, mockHBaseClient, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
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
        HBaseKeyValueStateIterator<byte[], byte[]> kvIterator = new HBaseKeyValueStateIterator(namespace, columnFamily, mockHBaseClient, pendingPrepare.entrySet().iterator(), pendingCommit.entrySet().iterator(), chunkSize, keySerializer, valueSerializer);
        Assert.assertFalse(kvIterator.hasNext());
    }
}

