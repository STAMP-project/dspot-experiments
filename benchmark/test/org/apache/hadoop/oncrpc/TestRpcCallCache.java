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
package org.apache.hadoop.oncrpc;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.oncrpc.RpcCallCache.CacheEntry;
import org.apache.hadoop.oncrpc.RpcCallCache.ClientRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link RpcCallCache}
 */
public class TestRpcCallCache {
    @Test(expected = IllegalArgumentException.class)
    public void testRpcCallCacheConstructorIllegalArgument0() {
        new RpcCallCache("test", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRpcCallCacheConstructorIllegalArgumentNegative() {
        new RpcCallCache("test", (-1));
    }

    @Test
    public void testRpcCallCacheConstructor() {
        RpcCallCache cache = new RpcCallCache("test", 100);
        Assert.assertEquals("test", cache.getProgram());
    }

    @Test
    public void testAddRemoveEntries() throws UnknownHostException {
        RpcCallCache cache = new RpcCallCache("test", 100);
        InetAddress clientIp = InetAddress.getByName("1.1.1.1");
        int xid = 100;
        // Ensure null is returned when there is no entry in the cache
        // An entry is added to indicate the request is in progress
        CacheEntry e = cache.checkOrAddToCache(clientIp, xid);
        Assert.assertNull(e);
        e = cache.checkOrAddToCache(clientIp, xid);
        validateInprogressCacheEntry(e);
        // Set call as completed
        RpcResponse response = Mockito.mock(RpcResponse.class);
        cache.callCompleted(clientIp, xid, response);
        e = cache.checkOrAddToCache(clientIp, xid);
        validateCompletedCacheEntry(e, response);
    }

    @Test
    public void testCacheEntry() {
        CacheEntry c = new CacheEntry();
        validateInprogressCacheEntry(c);
        Assert.assertTrue(c.isInProgress());
        Assert.assertFalse(c.isCompleted());
        Assert.assertNull(c.getResponse());
        RpcResponse response = Mockito.mock(RpcResponse.class);
        c.setResponse(response);
        validateCompletedCacheEntry(c, response);
    }

    @Test
    public void testCacheFunctionality() throws UnknownHostException {
        RpcCallCache cache = new RpcCallCache("Test", 10);
        // Add 20 entries to the cache and only last 10 should be retained
        int size = 0;
        for (int clientId = 0; clientId < 20; clientId++) {
            InetAddress clientIp = InetAddress.getByName(("1.1.1." + clientId));
            System.out.println(("Adding " + clientIp));
            cache.checkOrAddToCache(clientIp, 0);
            size = Math.min((++size), 10);
            System.out.println(("Cache size " + (cache.size())));
            Assert.assertEquals(size, cache.size());// Ensure the cache size is correct

            // Ensure the cache entries are correct
            int startEntry = Math.max(((clientId - 10) + 1), 0);
            Iterator<Map.Entry<ClientRequest, CacheEntry>> iterator = cache.iterator();
            for (int i = 0; i < size; i++) {
                ClientRequest key = iterator.next().getKey();
                System.out.println(("Entry " + (key.getClientId())));
                Assert.assertEquals(InetAddress.getByName(("1.1.1." + (startEntry + i))), key.getClientId());
            }
            // Ensure cache entries are returned as in progress.
            for (int i = 0; i < size; i++) {
                CacheEntry e = cache.checkOrAddToCache(InetAddress.getByName(("1.1.1." + (startEntry + i))), 0);
                Assert.assertNotNull(e);
                Assert.assertTrue(e.isInProgress());
                Assert.assertFalse(e.isCompleted());
            }
        }
    }
}

