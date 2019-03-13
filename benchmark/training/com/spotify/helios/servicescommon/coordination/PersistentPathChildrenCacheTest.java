/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.servicescommon.coordination;


import PersistentPathChildrenCache.Listener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.SettableFuture;
import com.spotify.helios.Parallelized;
import com.spotify.helios.Polling;
import com.spotify.helios.ZooKeeperTestManager;
import com.spotify.helios.common.Json;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(Parallelized.class)
public class PersistentPathChildrenCacheTest {
    public static class DataPojo {
        public DataPojo() {
        }

        public DataPojo(final String name) {
            this.name = name;
        }

        public String name;

        public int bar = 17;

        public Map<String, String> baz = ImmutableMap.of("foos", "bars");

        @Override
        public boolean equals(final Object obj) {
            if ((this) == obj) {
                return true;
            }
            if ((obj == null) || ((getClass()) != (obj.getClass()))) {
                return false;
            }
            final PersistentPathChildrenCacheTest.DataPojo dataPojo = ((PersistentPathChildrenCacheTest.DataPojo) (obj));
            if ((bar) != (dataPojo.bar)) {
                return false;
            }
            if ((baz) != null ? !(baz.equals(dataPojo.baz)) : (dataPojo.baz) != null) {
                return false;
            }
            if ((name) != null ? !(name.equals(dataPojo.name)) : (dataPojo.name) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + (bar);
            result = (31 * result) + ((baz) != null ? baz.hashCode() : 0);
            return result;
        }
    }

    private static final String PATH = "/foos";

    private ZooKeeperTestManager zk;

    private PersistentPathChildrenCache<PersistentPathChildrenCacheTest.DataPojo> cache;

    private Path directory;

    private Path stateFile;

    private Listener listener = Mockito.mock(Listener.class);

    @Test
    public void verifyListenerCalledOnNodeAdd() throws Exception {
        final PersistentPathChildrenCacheTest.DataPojo created = new PersistentPathChildrenCacheTest.DataPojo("foo");
        ensure("/foos/foo", created);
        Mockito.verify(listener, Mockito.timeout(60000).atLeastOnce()).nodesChanged(cache);
        final PersistentPathChildrenCacheTest.DataPojo read = Iterables.getOnlyElement(cache.getNodes().values());
        Assert.assertEquals(created, read);
    }

    @Test
    public void verifyListenerCalledOnNodeChange() throws Exception {
        final PersistentPathChildrenCacheTest.DataPojo created = new PersistentPathChildrenCacheTest.DataPojo("foo");
        ensure("/foos/foo", created);
        Mockito.verify(listener, Mockito.timeout(60000).atLeastOnce()).nodesChanged(cache);
        Mockito.reset(listener);
        final PersistentPathChildrenCacheTest.DataPojo changed = new PersistentPathChildrenCacheTest.DataPojo("foo-changed");
        zk.curatorWithSuperAuth().setData().forPath("/foos/foo", Json.asBytesUnchecked(changed));
        Mockito.verify(listener, Mockito.timeout(60000).atLeastOnce()).nodesChanged(cache);
        final PersistentPathChildrenCacheTest.DataPojo read = Iterables.getOnlyElement(cache.getNodes().values());
        Assert.assertEquals(changed, read);
    }

    @Test
    public void verifyListenerCalledOnNodeRemoved() throws Exception {
        ensure("/foos/foo", new PersistentPathChildrenCacheTest.DataPojo("foo"));
        Mockito.verify(listener, Mockito.timeout(60000).atLeastOnce()).nodesChanged(cache);
        Mockito.reset(listener);
        try {
            zk.curatorWithSuperAuth().delete().forPath("/foos/foo");
        } catch (NoNodeException ignore) {
            // ignored
        }
        Mockito.verify(listener, Mockito.timeout(60000).atLeastOnce()).nodesChanged(cache);
        Assert.assertTrue(cache.getNodes().isEmpty());
    }

    @Test
    public void verifyNodesAreRetainedWhenZkGoesDown() throws Exception {
        // Create two nodes
        final String foo1 = "/foos/foo1";
        final String foo2 = "/foos/foo2";
        final Set<String> paths = ImmutableSet.of(foo1, foo2);
        for (final String path : paths) {
            ensure(path, new PersistentPathChildrenCacheTest.DataPojo(path));
        }
        // Wait for the cache to pick them up
        Polling.await(5, TimeUnit.MINUTES, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return cache.getNodes().keySet().equals(paths) ? true : null;
            }
        });
        Mockito.verify(listener, Mockito.atLeastOnce()).nodesChanged(cache);
        // Wait for connection to be lost
        final SettableFuture<Void> connectionLost = SettableFuture.create();
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                if ((invocationOnMock.getArguments()[0]) == (ConnectionState.LOST)) {
                    connectionLost.set(null);
                }
                return null;
            }
        }).when(listener).connectionStateChanged(ArgumentMatchers.any(ConnectionState.class));
        // Take down zk
        zk.stop();
        connectionLost.get(5, TimeUnit.MINUTES);
        // Keep probing for 30 seconds to build some confidence that the snapshot is not going away
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            Assert.assertEquals(paths, cache.getNodes().keySet());
        }
    }

    @Test
    public void verifyNodesRemovedWhilePathChildrenCacheIsDownAreDetected() throws Exception {
        // Create two nodes
        final String foo1 = "/foos/foo1";
        final String foo2 = "/foos/foo2";
        final Set<String> paths = ImmutableSet.of(foo1, foo2);
        for (final String path : paths) {
            ensure(path, new PersistentPathChildrenCacheTest.DataPojo(path));
        }
        // Wait for the cache to pick them up
        Polling.await(5, TimeUnit.MINUTES, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return cache.getNodes().keySet().equals(paths) ? true : null;
            }
        });
        Mockito.verify(listener, Mockito.atLeastOnce()).nodesChanged(cache);
        // Stop the cache
        stopCache();
        // Remove a node
        try {
            zk.curatorWithSuperAuth().delete().forPath(foo1);
        } catch (NoNodeException ignore) {
            // ignored
        }
        // Start the cache
        startCache();
        // Wait for the cache to reflect that there's only one node left
        final Set<String> postDeletePaths = ImmutableSet.of(foo2);
        Polling.await(5, TimeUnit.MINUTES, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return cache.getNodes().keySet().equals(postDeletePaths) ? true : null;
            }
        });
        Mockito.verify(listener, Mockito.atLeastOnce()).nodesChanged(cache);
    }
}

