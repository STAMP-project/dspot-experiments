/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.distributed.cache.server.map;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import org.apache.nifi.distributed.cache.server.EvictionPolicy;
import org.junit.Assert;
import org.junit.Test;


public class TestSimpleMapCache {
    @Test
    public void testBasicOperations() throws Exception {
        final SimpleMapCache cache = new SimpleMapCache("service-id", 2, EvictionPolicy.FIFO);
        final ByteBuffer key1 = ByteBuffer.wrap("key1".getBytes());
        final ByteBuffer key2 = ByteBuffer.wrap("key2".getBytes());
        final ByteBuffer key3 = ByteBuffer.wrap("key3".getBytes());
        ByteBuffer value1 = ByteBuffer.wrap("value1-0".getBytes());
        ByteBuffer value2 = ByteBuffer.wrap("value2-0".getBytes());
        ByteBuffer value3 = ByteBuffer.wrap("value3-0".getBytes());
        // Initial state.
        Assert.assertNull(cache.get(key1));
        Assert.assertNull(cache.fetch(key1));
        // Put the 1st key.
        MapPutResult putResult = cache.put(key1, value1);
        Assert.assertTrue(putResult.isSuccessful());
        Assert.assertNull(putResult.getExisting());
        Assert.assertNull(putResult.getEvicted());
        Assert.assertEquals(0, putResult.getRecord().getRevision());
        // Update the same key.
        value1 = ByteBuffer.wrap("value1-1".getBytes());
        putResult = cache.put(key1, value1);
        Assert.assertTrue(putResult.isSuccessful());
        Assert.assertNotNull(putResult.getExisting());
        Assert.assertEquals(1, putResult.getRecord().getRevision());
        Assert.assertEquals(key1, putResult.getExisting().getKey());
        Assert.assertEquals("value1-0", new String(putResult.getExisting().getValue().array()));
        Assert.assertNull(putResult.getEvicted());
        // Put the 2nd key.
        putResult = cache.put(key2, value2);
        Assert.assertTrue(putResult.isSuccessful());
        Assert.assertNull(putResult.getExisting());
        Assert.assertNull(putResult.getEvicted());
        Assert.assertEquals(0, putResult.getRecord().getRevision());
        // Put the 3rd key.
        putResult = cache.put(key3, value3);
        Assert.assertTrue(putResult.isSuccessful());
        Assert.assertNull(putResult.getExisting());
        Assert.assertNotNull("The first key should be evicted", putResult.getEvicted());
        Assert.assertEquals("key1", new String(putResult.getEvicted().getKey().array()));
        Assert.assertEquals("value1-1", new String(putResult.getEvicted().getValue().array()));
        Assert.assertEquals(0, putResult.getRecord().getRevision());
        // Delete 2nd key.
        ByteBuffer removed = cache.remove(key2);
        Assert.assertNotNull(removed);
        Assert.assertEquals("value2-0", new String(removed.array()));
        // Put the 2nd key again.
        putResult = cache.put(key2, value2);
        Assert.assertTrue(putResult.isSuccessful());
        Assert.assertNull(putResult.getExisting());
        Assert.assertNull(putResult.getEvicted());
        Assert.assertEquals("Revision should start from 0", 0, putResult.getRecord().getRevision());
        // Get multiple keys
        Map<ByteBuffer, ByteBuffer> results = cache.subMap(Arrays.asList(key1, key2, key3));
        Assert.assertEquals(3, results.size());
    }

    @Test
    public void testOptimisticLock() throws Exception {
        final SimpleMapCache cache = new SimpleMapCache("service-id", 2, EvictionPolicy.FIFO);
        final ByteBuffer key = ByteBuffer.wrap("key1".getBytes());
        ByteBuffer valueC1 = ByteBuffer.wrap("valueC1-0".getBytes());
        ByteBuffer valueC2 = ByteBuffer.wrap("valueC2-0".getBytes());
        Assert.assertNull("If there's no existing key, fetch should return null.", cache.fetch(key));
        // Client 1 inserts the key.
        MapCacheRecord c1 = new MapCacheRecord(key, valueC1);
        MapPutResult putResult = cache.replace(c1);
        Assert.assertTrue("Replace should succeed if there's no existing key.", putResult.isSuccessful());
        MapCacheRecord c2 = new MapCacheRecord(key, valueC2);
        putResult = cache.replace(c2);
        Assert.assertFalse("Replace should fail.", putResult.isSuccessful());
        // Client 1 and 2 fetch the key
        c1 = cache.fetch(key);
        c2 = cache.fetch(key);
        Assert.assertEquals(0, c1.getRevision());
        Assert.assertEquals(0, c2.getRevision());
        // Client 1 replace
        valueC1 = ByteBuffer.wrap("valueC1-1".getBytes());
        putResult = cache.replace(new MapCacheRecord(key, valueC1, c1.getRevision()));
        Assert.assertTrue("Replace should succeed since revision matched.", putResult.isSuccessful());
        Assert.assertEquals(1, putResult.getRecord().getRevision());
        // Client 2 replace with the old revision
        valueC2 = ByteBuffer.wrap("valueC2-1".getBytes());
        putResult = cache.replace(new MapCacheRecord(key, valueC2, c2.getRevision()));
        Assert.assertFalse("Replace should fail.", putResult.isSuccessful());
    }
}

