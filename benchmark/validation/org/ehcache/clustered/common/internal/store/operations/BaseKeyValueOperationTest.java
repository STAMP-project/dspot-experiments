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


import java.nio.ByteBuffer;
import org.ehcache.clustered.client.TestTimeSource;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.junit.Assert;
import org.junit.Test;


public abstract class BaseKeyValueOperationTest {
    protected static final Serializer<Long> keySerializer = new LongSerializer();

    protected static final Serializer<String> valueSerializer = new StringSerializer();

    private static final TestTimeSource TIME_SOURCE = new TestTimeSource();

    @Test
    public void testEncode() throws Exception {
        Long key = 12L;
        String value = "The value";
        Operation<Long, String> operation = getNewOperation(key, value, BaseKeyValueOperationTest.TIME_SOURCE.getTimeMillis());
        ByteBuffer byteBuffer = operation.encode(BaseKeyValueOperationTest.keySerializer, BaseKeyValueOperationTest.valueSerializer);
        ByteBuffer expected = ByteBuffer.allocate(((((Operation.BYTE_SIZE_BYTES) + (Operation.INT_SIZE_BYTES)) + (2 * (Operation.LONG_SIZE_BYTES))) + (value.length())));
        expected.put(getOperationCode().getValue());
        expected.putLong(BaseKeyValueOperationTest.TIME_SOURCE.getTimeMillis());
        expected.putInt(Operation.LONG_SIZE_BYTES);
        expected.putLong(key);
        expected.put(value.getBytes());
        expected.flip();
        Assert.assertArrayEquals(expected.array(), byteBuffer.array());
    }

    @Test
    public void testDecode() throws Exception {
        Long key = 12L;
        String value = "The value";
        ByteBuffer blob = ByteBuffer.allocate(((((Operation.BYTE_SIZE_BYTES) + (Operation.INT_SIZE_BYTES)) + (2 * (Operation.LONG_SIZE_BYTES))) + (value.length())));
        blob.put(getOperationCode().getValue());
        blob.putLong(BaseKeyValueOperationTest.TIME_SOURCE.getTimeMillis());
        blob.putInt(Operation.LONG_SIZE_BYTES);
        blob.putLong(key);
        blob.put(value.getBytes());
        blob.flip();
        BaseKeyValueOperation<Long, String> operation = getNewOperation(blob, BaseKeyValueOperationTest.keySerializer, BaseKeyValueOperationTest.valueSerializer);
        Assert.assertEquals(getOperationCode(), operation.getOpCode());
        Assert.assertEquals(key, operation.getKey());
        Assert.assertEquals(value, operation.getValue());
    }

    @Test
    public void testEncodeDecodeInvariant() throws Exception {
        Long key = 12L;
        String value = "The value";
        Operation<Long, String> operation = getNewOperation(key, value, BaseKeyValueOperationTest.TIME_SOURCE.getTimeMillis());
        BaseKeyValueOperation<Long, String> decodedOperation = getNewOperation(operation.encode(BaseKeyValueOperationTest.keySerializer, BaseKeyValueOperationTest.valueSerializer), BaseKeyValueOperationTest.keySerializer, BaseKeyValueOperationTest.valueSerializer);
        Assert.assertEquals(key, decodedOperation.getKey());
        Assert.assertEquals(value, decodedOperation.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeThrowsOnInvalidType() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{ 2 });
        getNewOperation(buffer, BaseKeyValueOperationTest.keySerializer, BaseKeyValueOperationTest.valueSerializer);
    }
}

