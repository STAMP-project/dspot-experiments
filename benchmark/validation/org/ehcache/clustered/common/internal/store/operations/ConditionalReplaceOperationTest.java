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


import OperationCode.REPLACE_CONDITIONAL;
import java.nio.ByteBuffer;
import org.ehcache.clustered.client.TestTimeSource;
import org.ehcache.impl.serialization.LongSerializer;
import org.ehcache.impl.serialization.StringSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ConditionalReplaceOperationTest {
    private static final Serializer<Long> keySerializer = new LongSerializer();

    private static final Serializer<String> valueSerializer = new StringSerializer();

    private static final TestTimeSource TIME_SOURCE = new TestTimeSource();

    @Test
    public void testEncode() throws Exception {
        Long key = 1L;
        String newValue = "The one";
        String oldValue = "Another one";
        ConditionalReplaceOperation<Long, String> operation = new ConditionalReplaceOperation(key, oldValue, newValue, ConditionalReplaceOperationTest.TIME_SOURCE.getTimeMillis());
        ByteBuffer byteBuffer = operation.encode(ConditionalReplaceOperationTest.keySerializer, ConditionalReplaceOperationTest.valueSerializer);
        ByteBuffer expected = ByteBuffer.allocate(((((((Operation.BYTE_SIZE_BYTES) + (Operation.INT_SIZE_BYTES)) + (2 * (Operation.LONG_SIZE_BYTES))) + (Operation.INT_SIZE_BYTES)) + (oldValue.length())) + (newValue.length())));
        expected.put(REPLACE_CONDITIONAL.getValue());
        expected.putLong(ConditionalReplaceOperationTest.TIME_SOURCE.getTimeMillis());
        expected.putInt(Operation.LONG_SIZE_BYTES);
        expected.putLong(key);
        expected.putInt(oldValue.length());
        expected.put(oldValue.getBytes());
        expected.put(newValue.getBytes());
        expected.flip();
        Assert.assertArrayEquals(expected.array(), byteBuffer.array());
    }

    @Test
    public void testDecode() throws Exception {
        Long key = 1L;
        String newValue = "The one";
        String oldValue = "Another one";
        ByteBuffer blob = ByteBuffer.allocate(((((((Operation.BYTE_SIZE_BYTES) + (Operation.INT_SIZE_BYTES)) + (2 * (Operation.LONG_SIZE_BYTES))) + (Operation.INT_SIZE_BYTES)) + (oldValue.length())) + (newValue.length())));
        blob.put(REPLACE_CONDITIONAL.getValue());
        blob.putLong(ConditionalReplaceOperationTest.TIME_SOURCE.getTimeMillis());
        blob.putInt(Operation.LONG_SIZE_BYTES);
        blob.putLong(key);
        blob.putInt(oldValue.length());
        blob.put(oldValue.getBytes());
        blob.put(newValue.getBytes());
        blob.flip();
        ConditionalReplaceOperation<Long, String> operation = new ConditionalReplaceOperation(blob, ConditionalReplaceOperationTest.keySerializer, ConditionalReplaceOperationTest.valueSerializer);
        Assert.assertEquals(key, operation.getKey());
        Assert.assertEquals(newValue, operation.getValue());
        Assert.assertEquals(oldValue, operation.getOldValue());
    }

    @Test
    public void testEncodeDecodeInvariant() throws Exception {
        Long key = 1L;
        String newValue = "The value";
        String oldValue = "Another one";
        ConditionalReplaceOperation<Long, String> operation = new ConditionalReplaceOperation(key, oldValue, newValue, ConditionalReplaceOperationTest.TIME_SOURCE.getTimeMillis());
        ConditionalReplaceOperation<Long, String> decodedOperation = new ConditionalReplaceOperation(operation.encode(ConditionalReplaceOperationTest.keySerializer, ConditionalReplaceOperationTest.valueSerializer), ConditionalReplaceOperationTest.keySerializer, ConditionalReplaceOperationTest.valueSerializer);
        Assert.assertEquals(key, decodedOperation.getKey());
        Assert.assertEquals(newValue, decodedOperation.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeThrowsOnInvalidType() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{ 10 });
        new ConditionalReplaceOperation(buffer, ConditionalReplaceOperationTest.keySerializer, ConditionalReplaceOperationTest.valueSerializer);
    }

    @Test
    public void testApply() throws Exception {
        ConditionalReplaceOperation<Long, String> operation = new ConditionalReplaceOperation(1L, "one", "two", System.currentTimeMillis());
        Result<Long, String> result = operation.apply(null);
        Assert.assertNull(result);
        PutOperation<Long, String> anotherOperation = new PutOperation(1L, "one", System.currentTimeMillis());
        result = operation.apply(anotherOperation);
        Assert.assertThat(result, Is.is(new PutOperation(operation.getKey(), operation.getValue(), operation.timeStamp())));
        anotherOperation = new PutOperation(1L, "another one", System.currentTimeMillis());
        result = operation.apply(anotherOperation);
        Assert.assertSame(anotherOperation, result);
    }
}

