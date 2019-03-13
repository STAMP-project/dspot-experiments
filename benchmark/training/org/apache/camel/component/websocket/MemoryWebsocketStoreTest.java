/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.websocket;


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MemoryWebsocketStoreTest {
    private static final String KEY_1 = "one";

    private static final String KEY_2 = "two";

    @Mock
    private WebsocketConsumer consumer;

    @Mock
    private NodeSynchronization sync;

    @Mock
    private DefaultWebsocket websocket1 = new DefaultWebsocket(sync, null, consumer);

    @Mock
    private DefaultWebsocket websocket2 = new DefaultWebsocket(sync, null, consumer);

    private MemoryWebsocketStore store;

    @Test
    public void testAdd() {
        Assert.assertNotNull(websocket1.getConnectionKey());
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        store.add(websocket2);
        Assert.assertEquals(websocket2, store.get(MemoryWebsocketStoreTest.KEY_2));
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullValue() {
        store.add(null);
    }

    @Test
    public void testRemoveDefaultWebsocket() {
        // first call of websocket1.getConnectionKey()
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        // second call of websocket1.getConnectionKey()
        store.remove(websocket1);
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_1));
    }

    @Test
    public void testRemoveDefaultWebsocketKeyNotSet() {
        // first call of websocket1.getConnectionKey()
        store.add(websocket1);
        // overload getConnectionKey() after store.add() - otherwise npe
        Mockito.when(websocket1.getConnectionKey()).thenReturn(null);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        try {
            store.remove(websocket1);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    public void testRemoveNotExisting() {
        websocket1.setConnectionKey(MemoryWebsocketStoreTest.KEY_1);
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_2));
        store.remove(websocket2);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_2));
    }

    @Test
    public void testRemoveString() {
        websocket1.setConnectionKey(MemoryWebsocketStoreTest.KEY_1);
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        store.remove(MemoryWebsocketStoreTest.KEY_1);
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_1));
    }

    @Test
    public void testRemoveStringNotExisting() {
        websocket1.setConnectionKey(MemoryWebsocketStoreTest.KEY_1);
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_2));
        store.remove(MemoryWebsocketStoreTest.KEY_2);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_2));
    }

    @Test
    public void testGetString() {
        websocket1.setConnectionKey(MemoryWebsocketStoreTest.KEY_1);
        store.add(websocket1);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertNull(store.get(MemoryWebsocketStoreTest.KEY_2));
        websocket2.setConnectionKey(MemoryWebsocketStoreTest.KEY_2);
        store.add(websocket2);
        Assert.assertEquals(websocket1, store.get(MemoryWebsocketStoreTest.KEY_1));
        Assert.assertEquals(websocket2, store.get(MemoryWebsocketStoreTest.KEY_2));
    }

    @Test
    public void testGetAll() {
        Collection<DefaultWebsocket> sockets = store.getAll();
        Assert.assertNotNull(sockets);
        Assert.assertEquals(0, sockets.size());
        websocket1.setConnectionKey(MemoryWebsocketStoreTest.KEY_1);
        store.add(websocket1);
        sockets = store.getAll();
        Assert.assertNotNull(sockets);
        Assert.assertEquals(1, sockets.size());
        Assert.assertTrue(sockets.contains(websocket1));
        websocket2.setConnectionKey(MemoryWebsocketStoreTest.KEY_2);
        store.add(websocket2);
        sockets = store.getAll();
        Assert.assertNotNull(sockets);
        Assert.assertEquals(2, sockets.size());
        Assert.assertTrue(sockets.contains(websocket1));
        Assert.assertTrue(sockets.contains(websocket2));
    }
}

