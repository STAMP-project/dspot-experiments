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


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.test.TestingServer;
import org.apache.drill.exec.coord.store.TransientStoreConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestEphemeralStore {
    private static final String root = "/test";

    private static final String path = "test-key";

    private static final String value = "testing";

    private TestingServer server;

    private CuratorFramework curator;

    private TransientStoreConfig<String> config;

    private ZkEphemeralStore<String> store;

    static class StoreWithMockClient<V> extends ZkEphemeralStore<V> {
        private final ZookeeperClient client = Mockito.mock(ZookeeperClient.class);

        public StoreWithMockClient(final TransientStoreConfig<V> config, final CuratorFramework curator) {
            super(config, curator);
        }

        @Override
        protected ZookeeperClient getClient() {
            return client;
        }
    }

    /**
     * This test ensures store subscribes to receive events from underlying client. Dispatcher tests ensures listeners
     * are fired on incoming events. These two sets of tests ensure observer pattern in {@code TransientStore} works fine.
     */
    @Test
    public void testStoreRegistersDispatcherAndStartsItsClient() throws Exception {
        final TestEphemeralStore.StoreWithMockClient<String> store = new TestEphemeralStore.StoreWithMockClient(config, curator);
        final PathChildrenCache cache = Mockito.mock(PathChildrenCache.class);
        final ZookeeperClient client = store.getClient();
        Mockito.when(client.getCache()).thenReturn(cache);
        final ListenerContainer<PathChildrenCacheListener> container = Mockito.mock(ListenerContainer.class);
        Mockito.when(cache.getListenable()).thenReturn(container);
        store.start();
        Mockito.verify(container).addListener(store.dispatcher);
        Mockito.verify(client).start();
    }

    @Test
    public void testPutAndGetWorksAntagonistacally() {
        store.put(TestEphemeralStore.path, TestEphemeralStore.value);
        final String actual = store.get(TestEphemeralStore.path);
        Assert.assertEquals("value mismatch", TestEphemeralStore.value, actual);
    }
}

