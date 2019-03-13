/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.common.internal.store.operations;


import OperationCode.REMOVE;
import java.nio.ByteBuffer;
import org.ehcache.clustered.client.TestTimeSource;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.junit.Assert;
import org.junit.Test;


public class RemoveOperationTest {
    private static final Serializer<Long> keySerializer = new LongSerializer();

    private static final Serializer<String> valueSerializer = new StringSerializer();

    private static final TestTimeSource TIME_SOURCE = new TestTimeSource();

    @Test
    public void testEncode() throws Exception {
        Long key = 12L;
        RemoveOperation<Long, String> operation = new RemoveOperation(key, RemoveOperationTest.TIME_SOURCE.getTimeMillis());
        ByteBuffer byteBuffer = operation.encode(RemoveOperationTest.keySerializer, RemoveOperationTest.valueSerializer);
        ByteBuffer expected = ByteBuffer.allocate(((Operation.BYTE_SIZE_BYTES) + (2 * (Operation.LONG_SIZE_BYTES))));
        expected.put(REMOVE.getValue());
        expected.putLong(RemoveOperationTest.TIME_SOURCE.getTimeMillis());
        expected.putLong(key);
        expected.flip();
        Assert.assertArrayEquals(expected.array(), byteBuffer.array());
    }

    @Test
    public void testDecode() throws Exception {
        Long key = 12L;
        ByteBuffer blob = ByteBuffer.allocate(((Operation.BYTE_SIZE_BYTES) + (2 * (Operation.LONG_SIZE_BYTES))));
        blob.put(REMOVE.getValue());
        blob.putLong(RemoveOperationTest.TIME_SOURCE.getTimeMillis());
        blob.putLong(key);
        blob.flip();
        RemoveOperation<Long, String> operation = new RemoveOperation(blob, RemoveOperationTest.keySerializer);
        Assert.assertEquals(key, operation.getKey());
    }

    @Test
    public void testEncodeDecodeInvariant() throws Exception {
        Long key = 12L;
        RemoveOperation<Long, String> operation = new RemoveOperation(key, System.currentTimeMillis());
        RemoveOperation<Long, String> decodedOperation = new RemoveOperation(operation.encode(RemoveOperationTest.keySerializer, RemoveOperationTest.valueSerializer), RemoveOperationTest.keySerializer);
        Assert.assertEquals(key, decodedOperation.getKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeThrowsOnInvalidType() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{ 10 });
        new RemoveOperation<Long, String>(buffer, RemoveOperationTest.keySerializer);
    }

    @Test
    public void testApply() throws Exception {
        RemoveOperation<Long, String> operation = new RemoveOperation(1L, System.currentTimeMillis());
        Result<Long, String> result = operation.apply(null);
        Assert.assertNull(result);
        PutOperation<Long, String> anotherOperation = new PutOperation(1L, "another one", System.currentTimeMillis());
        result = operation.apply(anotherOperation);
        Assert.assertNull(result);
    }
}

