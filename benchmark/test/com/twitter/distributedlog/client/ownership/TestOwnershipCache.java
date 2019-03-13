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
package com.twitter.distributedlog.client.ownership;


import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test Case for Ownership Cache
 */
public class TestOwnershipCache {
    @Rule
    public TestName runtime = new TestName();

    @Test(timeout = 60000)
    public void testUpdateOwner() {
        OwnershipCache cache = TestOwnershipCache.createOwnershipCache();
        SocketAddress addr = TestOwnershipCache.createSocketAddress(1000);
        String stream = runtime.getMethodName();
        Assert.assertTrue("Should successfully update owner if no owner exists before", cache.updateOwner(stream, addr));
        Assert.assertEquals(((("Owner should be " + addr) + " for stream ") + stream), addr, cache.getOwner(stream));
        Assert.assertTrue("Should successfully update owner if old owner is same", cache.updateOwner(stream, addr));
        Assert.assertEquals(((("Owner should be " + addr) + " for stream ") + stream), addr, cache.getOwner(stream));
    }

    @Test(timeout = 60000)
    public void testRemoveOwnerFromStream() {
        OwnershipCache cache = TestOwnershipCache.createOwnershipCache();
        int initialPort = 2000;
        int numProxies = 2;
        int numStreamsPerProxy = 2;
        for (int i = 0; i < numProxies; i++) {
            SocketAddress addr = TestOwnershipCache.createSocketAddress((initialPort + i));
            for (int j = 0; j < numStreamsPerProxy; j++) {
                String stream = ((((runtime.getMethodName()) + "_") + i) + "_") + j;
                cache.updateOwner(stream, addr);
            }
        }
        Map<String, SocketAddress> ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        Map<SocketAddress, Set<String>> ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should be " + numProxies) + " proxies cached"), numProxies, ownershipDistribution.size());
        String stream = (runtime.getMethodName()) + "_0_0";
        SocketAddress owner = TestOwnershipCache.createSocketAddress(initialPort);
        // remove non-existent mapping won't change anything
        SocketAddress nonExistentAddr = TestOwnershipCache.createSocketAddress((initialPort + 999));
        cache.removeOwnerFromStream(stream, nonExistentAddr, "remove-non-existent-addr");
        Assert.assertEquals((("Owner " + owner) + " should not be removed"), owner, cache.getOwner(stream));
        ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        // remove existent mapping should remove ownership mapping
        cache.removeOwnerFromStream(stream, owner, "remove-owner");
        Assert.assertNull((("Owner " + owner) + " should be removed"), cache.getOwner(stream));
        ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + ((numProxies * numStreamsPerProxy) - 1)) + " entries left in cache"), ((numProxies * numStreamsPerProxy) - 1), ownershipMap.size());
        ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should still be " + numProxies) + " proxies cached"), numProxies, ownershipDistribution.size());
        Set<String> ownedStreams = ownershipDistribution.get(owner);
        Assert.assertEquals(((("There should be only " + (numStreamsPerProxy - 1)) + " streams owned for ") + owner), (numStreamsPerProxy - 1), ownedStreams.size());
        Assert.assertFalse(((("Stream " + stream) + " should not be owned by ") + owner), ownedStreams.contains(stream));
    }

    @Test(timeout = 60000)
    public void testRemoveAllStreamsFromOwner() {
        OwnershipCache cache = TestOwnershipCache.createOwnershipCache();
        int initialPort = 2000;
        int numProxies = 2;
        int numStreamsPerProxy = 2;
        for (int i = 0; i < numProxies; i++) {
            SocketAddress addr = TestOwnershipCache.createSocketAddress((initialPort + i));
            for (int j = 0; j < numStreamsPerProxy; j++) {
                String stream = ((((runtime.getMethodName()) + "_") + i) + "_") + j;
                cache.updateOwner(stream, addr);
            }
        }
        Map<String, SocketAddress> ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        Map<SocketAddress, Set<String>> ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should be " + numProxies) + " proxies cached"), numProxies, ownershipDistribution.size());
        SocketAddress owner = TestOwnershipCache.createSocketAddress(initialPort);
        // remove non-existent host won't change anything
        SocketAddress nonExistentAddr = TestOwnershipCache.createSocketAddress((initialPort + 999));
        cache.removeAllStreamsFromOwner(nonExistentAddr);
        ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should still be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should still be " + numProxies) + " proxies cached"), numProxies, ownershipDistribution.size());
        // remove existent host should remove ownership mapping
        cache.removeAllStreamsFromOwner(owner);
        ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + ((numProxies - 1) * numStreamsPerProxy)) + " entries left in cache"), ((numProxies - 1) * numStreamsPerProxy), ownershipMap.size());
        ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should be " + (numProxies - 1)) + " proxies cached"), (numProxies - 1), ownershipDistribution.size());
        Assert.assertFalse((("Host " + owner) + " should not be cached"), ownershipDistribution.containsKey(owner));
    }

    @Test(timeout = 60000)
    public void testReplaceOwner() {
        OwnershipCache cache = TestOwnershipCache.createOwnershipCache();
        int initialPort = 2000;
        int numProxies = 2;
        int numStreamsPerProxy = 2;
        for (int i = 0; i < numProxies; i++) {
            SocketAddress addr = TestOwnershipCache.createSocketAddress((initialPort + i));
            for (int j = 0; j < numStreamsPerProxy; j++) {
                String stream = ((((runtime.getMethodName()) + "_") + i) + "_") + j;
                cache.updateOwner(stream, addr);
            }
        }
        Map<String, SocketAddress> ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        Map<SocketAddress, Set<String>> ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should be " + numProxies) + " proxies cached"), numProxies, ownershipDistribution.size());
        String stream = (runtime.getMethodName()) + "_0_0";
        SocketAddress oldOwner = TestOwnershipCache.createSocketAddress(initialPort);
        SocketAddress newOwner = TestOwnershipCache.createSocketAddress((initialPort + 999));
        cache.updateOwner(stream, newOwner);
        Assert.assertEquals(((((("Owner of " + stream) + " should be changed from ") + oldOwner) + " to ") + newOwner), newOwner, cache.getOwner(stream));
        ownershipMap = cache.getStreamOwnerMapping();
        Assert.assertEquals((("There should be " + (numProxies * numStreamsPerProxy)) + " entries in cache"), (numProxies * numStreamsPerProxy), ownershipMap.size());
        Assert.assertEquals(((("Owner of " + stream) + " should be ") + newOwner), newOwner, ownershipMap.get(stream));
        ownershipDistribution = cache.getStreamOwnershipDistribution();
        Assert.assertEquals((("There should be " + (numProxies + 1)) + " proxies cached"), (numProxies + 1), ownershipDistribution.size());
        Set<String> oldOwnedStreams = ownershipDistribution.get(oldOwner);
        Assert.assertEquals(((("There should be only " + (numStreamsPerProxy - 1)) + " streams owned by ") + oldOwner), (numStreamsPerProxy - 1), oldOwnedStreams.size());
        Assert.assertFalse(((("Stream " + stream) + " should not be owned by ") + oldOwner), oldOwnedStreams.contains(stream));
        Set<String> newOwnedStreams = ownershipDistribution.get(newOwner);
        Assert.assertEquals(((("There should be only " + (numStreamsPerProxy - 1)) + " streams owned by ") + newOwner), 1, newOwnedStreams.size());
        Assert.assertTrue(((("Stream " + stream) + " should be owned by ") + newOwner), newOwnedStreams.contains(stream));
    }
}

