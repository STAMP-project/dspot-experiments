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


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.util.Callback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class OffsetStorageWriterTest {
    private static final String NAMESPACE = "namespace";

    // Connect format - any types should be accepted here
    private static final Map<String, String> OFFSET_KEY = Collections.singletonMap("key", "key");

    private static final Map<String, Integer> OFFSET_VALUE = Collections.singletonMap("key", 12);

    // Serialized
    private static final byte[] OFFSET_KEY_SERIALIZED = "key-serialized".getBytes();

    private static final byte[] OFFSET_VALUE_SERIALIZED = "value-serialized".getBytes();

    @Mock
    private OffsetBackingStore store;

    @Mock
    private Converter keyConverter;

    @Mock
    private Converter valueConverter;

    private OffsetStorageWriter writer;

    private static Exception exception = new RuntimeException("error");

    private ExecutorService service;

    @Test
    public void testWriteFlush() throws Exception {
        @SuppressWarnings("unchecked")
        Callback<Void> callback = PowerMock.createMock(Callback.class);
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, callback, false, null);
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback).get(1000, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }

    // It should be possible to set offset values to null
    @Test
    public void testWriteNullValueFlush() throws Exception {
        @SuppressWarnings("unchecked")
        Callback<Void> callback = PowerMock.createMock(Callback.class);
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, null, null, callback, false, null);
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, null);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback).get(1000, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }

    // It should be possible to use null keys. These aren't actually stored as null since the key is wrapped to include
    // info about the namespace (connector)
    @Test
    public void testWriteNullKeyFlush() throws Exception {
        @SuppressWarnings("unchecked")
        Callback<Void> callback = PowerMock.createMock(Callback.class);
        expectStore(null, null, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, callback, false, null);
        PowerMock.replayAll();
        writer.offset(null, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback).get(1000, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }

    @Test
    public void testNoOffsetsToFlush() {
        // If no offsets are flushed, we should finish immediately and not have made any calls to the
        // underlying storage layer
        PowerMock.replayAll();
        // Should not return a future
        Assert.assertFalse(writer.beginFlush());
        PowerMock.verifyAll();
    }

    @Test
    public void testFlushFailureReplacesOffsets() throws Exception {
        // When a flush fails, we shouldn't just lose the offsets. Instead, they should be restored
        // such that a subsequent flush will write them.
        @SuppressWarnings("unchecked")
        final Callback<Void> callback = PowerMock.createMock(Callback.class);
        // First time the write fails
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, callback, true, null);
        // Second time it succeeds
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, callback, false, null);
        // Third time it has no data to flush so we won't get past beginFlush()
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback).get(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback).get(1000, TimeUnit.MILLISECONDS);
        Assert.assertFalse(writer.beginFlush());
        PowerMock.verifyAll();
    }

    @Test(expected = ConnectException.class)
    public void testAlreadyFlushing() {
        @SuppressWarnings("unchecked")
        final Callback<Void> callback = PowerMock.createMock(Callback.class);
        // Trigger the send, but don't invoke the callback so we'll still be mid-flush
        CountDownLatch allowStoreCompleteCountdown = new CountDownLatch(1);
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, null, false, allowStoreCompleteCountdown);
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        writer.doFlush(callback);
        Assert.assertTrue(writer.beginFlush());// should throw

        PowerMock.verifyAll();
    }

    @Test
    public void testCancelBeforeAwaitFlush() {
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        writer.cancelFlush();
        PowerMock.verifyAll();
    }

    @Test
    public void testCancelAfterAwaitFlush() throws Exception {
        @SuppressWarnings("unchecked")
        Callback<Void> callback = PowerMock.createMock(Callback.class);
        CountDownLatch allowStoreCompleteCountdown = new CountDownLatch(1);
        // In this test, the write should be cancelled so the callback will not be invoked and is not
        // passed to the expectStore call
        expectStore(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_KEY_SERIALIZED, OffsetStorageWriterTest.OFFSET_VALUE, OffsetStorageWriterTest.OFFSET_VALUE_SERIALIZED, null, false, allowStoreCompleteCountdown);
        PowerMock.replayAll();
        writer.offset(OffsetStorageWriterTest.OFFSET_KEY, OffsetStorageWriterTest.OFFSET_VALUE);
        Assert.assertTrue(writer.beginFlush());
        // Start the flush, then immediately cancel before allowing the mocked store request to finish
        Future<Void> flushFuture = writer.doFlush(callback);
        writer.cancelFlush();
        allowStoreCompleteCountdown.countDown();
        flushFuture.get(1000, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }
}

