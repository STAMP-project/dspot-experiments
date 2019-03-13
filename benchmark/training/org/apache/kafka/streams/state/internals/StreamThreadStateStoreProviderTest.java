/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.internals.StateDirectory;
import org.apache.kafka.streams.processor.internals.StreamTask;
import org.apache.kafka.streams.processor.internals.StreamThread;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.junit.Assert;
import org.junit.Test;


public class StreamThreadStateStoreProviderTest {
    private StreamTask taskOne;

    private StreamThreadStateStoreProvider provider;

    private StateDirectory stateDirectory;

    private File stateDir;

    private final String topicName = "topic";

    private StreamThread threadMock;

    private Map<TaskId, StreamTask> tasks;

    @Test
    public void shouldFindKeyValueStores() {
        mockThread(true);
        final List<ReadOnlyKeyValueStore<String, String>> kvStores = provider.stores("kv-store", QueryableStoreTypes.keyValueStore());
        Assert.assertEquals(2, kvStores.size());
    }

    @Test
    public void shouldFindWindowStores() {
        mockThread(true);
        final List<ReadOnlyWindowStore<Object, Object>> windowStores = provider.stores("window-store", QueryableStoreTypes.windowStore());
        Assert.assertEquals(2, windowStores.size());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowInvalidStoreExceptionIfWindowStoreClosed() {
        mockThread(true);
        taskOne.getStore("window-store").close();
        provider.stores("window-store", QueryableStoreTypes.windowStore());
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowInvalidStoreExceptionIfKVStoreClosed() {
        mockThread(true);
        taskOne.getStore("kv-store").close();
        provider.stores("kv-store", QueryableStoreTypes.keyValueStore());
    }

    @Test
    public void shouldReturnEmptyListIfNoStoresFoundWithName() {
        mockThread(true);
        Assert.assertEquals(Collections.emptyList(), provider.stores("not-a-store", QueryableStoreTypes.keyValueStore()));
    }

    @Test
    public void shouldReturnEmptyListIfStoreExistsButIsNotOfTypeValueStore() {
        mockThread(true);
        Assert.assertEquals(Collections.emptyList(), provider.stores("window-store", QueryableStoreTypes.keyValueStore()));
    }

    @Test(expected = InvalidStateStoreException.class)
    public void shouldThrowInvalidStoreExceptionIfNotAllStoresAvailable() {
        mockThread(false);
        provider.stores("kv-store", QueryableStoreTypes.keyValueStore());
    }
}

