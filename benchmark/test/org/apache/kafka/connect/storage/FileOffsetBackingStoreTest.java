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
package org.apache.kafka.connect.storage;


import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.connect.runtime.standalone.StandaloneConfig;
import org.apache.kafka.connect.util.Callback;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;


public class FileOffsetBackingStoreTest {
    FileOffsetBackingStore store;

    Map<String, String> props;

    StandaloneConfig config;

    File tempFile;

    private static Map<ByteBuffer, ByteBuffer> firstSet = new HashMap<>();

    static {
        FileOffsetBackingStoreTest.firstSet.put(FileOffsetBackingStoreTest.buffer("key"), FileOffsetBackingStoreTest.buffer("value"));
        FileOffsetBackingStoreTest.firstSet.put(null, null);
    }

    @Test
    public void testGetSet() throws Exception {
        Callback<Void> setCallback = expectSuccessfulSetCallback();
        Callback<Map<ByteBuffer, ByteBuffer>> getCallback = expectSuccessfulGetCallback();
        PowerMock.replayAll();
        store.set(FileOffsetBackingStoreTest.firstSet, setCallback).get();
        Map<ByteBuffer, ByteBuffer> values = store.get(Arrays.asList(FileOffsetBackingStoreTest.buffer("key"), FileOffsetBackingStoreTest.buffer("bad")), getCallback).get();
        Assert.assertEquals(FileOffsetBackingStoreTest.buffer("value"), values.get(FileOffsetBackingStoreTest.buffer("key")));
        Assert.assertEquals(null, values.get(FileOffsetBackingStoreTest.buffer("bad")));
        PowerMock.verifyAll();
    }

    @Test
    public void testSaveRestore() throws Exception {
        Callback<Void> setCallback = expectSuccessfulSetCallback();
        Callback<Map<ByteBuffer, ByteBuffer>> getCallback = expectSuccessfulGetCallback();
        PowerMock.replayAll();
        store.set(FileOffsetBackingStoreTest.firstSet, setCallback).get();
        store.stop();
        // Restore into a new store to ensure correct reload from scratch
        FileOffsetBackingStore restore = new FileOffsetBackingStore();
        restore.configure(config);
        restore.start();
        Map<ByteBuffer, ByteBuffer> values = restore.get(Arrays.asList(FileOffsetBackingStoreTest.buffer("key")), getCallback).get();
        Assert.assertEquals(FileOffsetBackingStoreTest.buffer("value"), values.get(FileOffsetBackingStoreTest.buffer("key")));
        PowerMock.verifyAll();
    }
}

