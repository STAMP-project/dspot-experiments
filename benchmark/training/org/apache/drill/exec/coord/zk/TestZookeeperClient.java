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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.coord.zk;


import PathChildrenCache.StartMode.BUILD_INITIAL_CACHE;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.test.TestingServer;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.exec.exception.VersionMismatchException;
import org.apache.drill.exec.store.sys.store.DataChangeVersion;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestZookeeperClient {
    private static final String root = "/test";

    private static final String path = "test-key";

    private static final String abspath = PathUtils.join(TestZookeeperClient.root, TestZookeeperClient.path);

    private static final byte[] data = "testing".getBytes();

    private static final CreateMode mode = CreateMode.PERSISTENT;

    private TestingServer server;

    private CuratorFramework curator;

    private ZookeeperClient client;

    private static class ClientWithMockCache extends ZookeeperClient {
        private final PathChildrenCache cacheMock = Mockito.mock(PathChildrenCache.class);

        ClientWithMockCache(final CuratorFramework curator, final String root, final CreateMode mode) {
            super(curator, root, mode);
        }

        @Override
        public PathChildrenCache getCache() {
            return cacheMock;
        }
    }

    @Test
    public void testStartingClientEnablesCacheAndEnsuresRootNodeExists() throws Exception {
        Assert.assertTrue("start must create the root node", client.hasPath("", true));
        Mockito.verify(client.getCache()).start(BUILD_INITIAL_CACHE);
    }

    @Test
    public void testHasPathWithEventualConsistencyHitsCache() {
        final String path = "test-key";
        final String absPath = PathUtils.join(TestZookeeperClient.root, path);
        Mockito.when(client.getCache().getCurrentData(absPath)).thenReturn(null);
        Assert.assertFalse(client.hasPath(path));// test convenience method

        Mockito.when(client.getCache().getCurrentData(absPath)).thenReturn(new ChildData(absPath, null, null));
        Assert.assertTrue(client.hasPath(path, false));// test actual method

    }

    @Test(expected = DrillRuntimeException.class)
    public void testHasPathThrowsDrillRuntimeException() {
        final String path = "test-key";
        final String absPath = PathUtils.join(TestZookeeperClient.root, path);
        Mockito.when(client.getCache().getCurrentData(absPath)).thenThrow(RuntimeException.class);
        client.hasPath(path);
    }

    @Test
    public void testHasPathTrueWithVersion() {
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version0 = new DataChangeVersion();
        Assert.assertTrue(client.hasPath(TestZookeeperClient.path, true, version0));
        Assert.assertEquals("Versions should match", 0, version0.getVersion());
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version1 = new DataChangeVersion();
        Assert.assertTrue(client.hasPath(TestZookeeperClient.path, true, version1));
        Assert.assertEquals("Versions should match", 1, version1.getVersion());
    }

    @Test
    public void testHasPathFalseWithVersion() {
        DataChangeVersion version0 = new DataChangeVersion();
        version0.setVersion((-1));
        Assert.assertFalse(client.hasPath("unknown_path", true, version0));
        Assert.assertEquals("Versions should not have changed", (-1), version0.getVersion());
    }

    @Test
    public void testPutAndGetWorks() {
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        final byte[] actual = client.get(TestZookeeperClient.path, true);
        Assert.assertArrayEquals("data mismatch", TestZookeeperClient.data, actual);
    }

    @Test
    public void testGetWithEventualConsistencyHitsCache() {
        Mockito.when(client.getCache().getCurrentData(TestZookeeperClient.abspath)).thenReturn(null);
        Assert.assertNull("get should return null", client.get(TestZookeeperClient.path));
        Mockito.when(client.getCache().getCurrentData(TestZookeeperClient.abspath)).thenReturn(new ChildData(TestZookeeperClient.abspath, null, TestZookeeperClient.data));
        Assert.assertEquals("get should return data", TestZookeeperClient.data, client.get(TestZookeeperClient.path, false));
    }

    @Test
    public void testCreate() throws Exception {
        client.create(TestZookeeperClient.path);
        Assert.assertTrue("path must exist", client.hasPath(TestZookeeperClient.path, true));
        // ensure invoking create also rebuilds cache
        Mockito.verify(client.getCache(), Mockito.times(1)).rebuildNode(TestZookeeperClient.abspath);
    }

    @Test
    public void testDelete() throws Exception {
        client.create(TestZookeeperClient.path);
        Assert.assertTrue("path must exist", client.hasPath(TestZookeeperClient.path, true));
        client.delete(TestZookeeperClient.path);
        Assert.assertFalse("path must not exist", client.hasPath(TestZookeeperClient.path, true));
        // ensure cache is rebuilt
        Mockito.verify(client.getCache(), Mockito.times(2)).rebuildNode(TestZookeeperClient.abspath);
    }

    @Test
    public void testEntriesReturnsRelativePaths() {
        final ChildData child = Mockito.mock(ChildData.class);
        Mockito.when(child.getPath()).thenReturn(TestZookeeperClient.abspath);
        Mockito.when(child.getData()).thenReturn(TestZookeeperClient.data);
        final List<ChildData> children = Lists.newArrayList(child);
        Mockito.when(client.getCache().getCurrentData()).thenReturn(children);
        final Iterator<Map.Entry<String, byte[]>> entries = client.entries();
        // returned entry must contain the given relative path
        final Map.Entry<String, byte[]> expected = new org.apache.drill.common.collections.ImmutableEntry(TestZookeeperClient.path, TestZookeeperClient.data);
        Assert.assertEquals("entries do not match", expected, entries.next());
    }

    @Test
    public void testGetWithVersion() {
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version0 = new DataChangeVersion();
        client.get(TestZookeeperClient.path, version0);
        Assert.assertEquals("Versions should match", 0, version0.getVersion());
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version1 = new DataChangeVersion();
        client.get(TestZookeeperClient.path, version1);
        Assert.assertEquals("Versions should match", 1, version1.getVersion());
    }

    @Test
    public void testPutWithMatchingVersion() {
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version = new DataChangeVersion();
        client.get(TestZookeeperClient.path, version);
        client.put(TestZookeeperClient.path, TestZookeeperClient.data, version);
    }

    @Test(expected = VersionMismatchException.class)
    public void testPutWithNonMatchingVersion() {
        client.put(TestZookeeperClient.path, TestZookeeperClient.data);
        DataChangeVersion version = new DataChangeVersion();
        version.setVersion(123);
        client.put(TestZookeeperClient.path, TestZookeeperClient.data, version);
    }

    @Test
    public void testPutIfAbsentWhenAbsent() {
        Assert.assertNull(client.putIfAbsent(TestZookeeperClient.path, TestZookeeperClient.data));
    }

    @Test
    public void testPutIfAbsentWhenPresent() {
        client.putIfAbsent(TestZookeeperClient.path, TestZookeeperClient.data);
        Assert.assertEquals("Data should match", new String(TestZookeeperClient.data), new String(client.putIfAbsent(TestZookeeperClient.path, "new_data".getBytes())));
    }
}

