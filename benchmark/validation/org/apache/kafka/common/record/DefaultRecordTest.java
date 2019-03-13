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
package org.apache.kafka.common.record;


import RecordBatch.NO_SEQUENCE;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.utils.ByteBufferOutputStream;
import org.apache.kafka.common.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;


public class DefaultRecordTest {
    @Test
    public void testBasicSerde() throws IOException {
        Header[] headers = new Header[]{ new RecordHeader("foo", "value".getBytes()), new RecordHeader("bar", ((byte[]) (null))), new RecordHeader("\"A\\u00ea\\u00f1\\u00fcC\"", "value".getBytes()) };
        SimpleRecord[] records = new SimpleRecord[]{ new SimpleRecord("hi".getBytes(), "there".getBytes()), new SimpleRecord(null, "there".getBytes()), new SimpleRecord("hi".getBytes(), null), new SimpleRecord(null, null), new SimpleRecord(15L, "hi".getBytes(), "there".getBytes(), headers) };
        for (SimpleRecord record : records) {
            int baseSequence = 723;
            long baseOffset = 37;
            int offsetDelta = 10;
            long baseTimestamp = System.currentTimeMillis();
            long timestampDelta = 323;
            ByteBufferOutputStream out = new ByteBufferOutputStream(1024);
            DefaultRecord.writeTo(new DataOutputStream(out), offsetDelta, timestampDelta, record.key(), record.value(), record.headers());
            ByteBuffer buffer = out.buffer();
            buffer.flip();
            DefaultRecord logRecord = DefaultRecord.readFrom(buffer, baseOffset, baseTimestamp, baseSequence, null);
            Assert.assertNotNull(logRecord);
            Assert.assertEquals((baseOffset + offsetDelta), logRecord.offset());
            Assert.assertEquals((baseSequence + offsetDelta), logRecord.sequence());
            Assert.assertEquals((baseTimestamp + timestampDelta), logRecord.timestamp());
            Assert.assertEquals(record.key(), logRecord.key());
            Assert.assertEquals(record.value(), logRecord.value());
            Assert.assertArrayEquals(record.headers(), logRecord.headers());
            Assert.assertEquals(DefaultRecord.sizeInBytes(offsetDelta, timestampDelta, record.key(), record.value(), record.headers()), logRecord.sizeInBytes());
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testBasicSerdeInvalidHeaderCountTooHigh() throws IOException {
        Header[] headers = new Header[]{ new RecordHeader("foo", "value".getBytes()), new RecordHeader("bar", ((byte[]) (null))), new RecordHeader("\"A\\u00ea\\u00f1\\u00fcC\"", "value".getBytes()) };
        SimpleRecord record = new SimpleRecord(15L, "hi".getBytes(), "there".getBytes(), headers);
        int baseSequence = 723;
        long baseOffset = 37;
        int offsetDelta = 10;
        long baseTimestamp = System.currentTimeMillis();
        long timestampDelta = 323;
        ByteBufferOutputStream out = new ByteBufferOutputStream(1024);
        DefaultRecord.writeTo(new DataOutputStream(out), offsetDelta, timestampDelta, record.key(), record.value(), record.headers());
        ByteBuffer buffer = out.buffer();
        buffer.flip();
        buffer.put(14, ((byte) (8)));
        DefaultRecord logRecord = DefaultRecord.readFrom(buffer, baseOffset, baseTimestamp, baseSequence, null);
        // force iteration through the record to validate the number of headers
        Assert.assertEquals(DefaultRecord.sizeInBytes(offsetDelta, timestampDelta, record.key(), record.value(), record.headers()), logRecord.sizeInBytes());
    }

    @Test(expected = InvalidRecordException.class)
    public void testBasicSerdeInvalidHeaderCountTooLow() throws IOException {
        Header[] headers = new Header[]{ new RecordHeader("foo", "value".getBytes()), new RecordHeader("bar", ((byte[]) (null))), new RecordHeader("\"A\\u00ea\\u00f1\\u00fcC\"", "value".getBytes()) };
        SimpleRecord record = new SimpleRecord(15L, "hi".getBytes(), "there".getBytes(), headers);
        int baseSequence = 723;
        long baseOffset = 37;
        int offsetDelta = 10;
        long baseTimestamp = System.currentTimeMillis();
        long timestampDelta = 323;
        ByteBufferOutputStream out = new ByteBufferOutputStream(1024);
        DefaultRecord.writeTo(new DataOutputStream(out), offsetDelta, timestampDelta, record.key(), record.value(), record.headers());
        ByteBuffer buffer = out.buffer();
        buffer.flip();
        buffer.put(14, ((byte) (4)));
        DefaultRecord logRecord = DefaultRecord.readFrom(buffer, baseOffset, baseTimestamp, baseSequence, null);
        // force iteration through the record to validate the number of headers
        Assert.assertEquals(DefaultRecord.sizeInBytes(offsetDelta, timestampDelta, record.key(), record.value(), record.headers()), logRecord.sizeInBytes());
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidKeySize() {
        byte attributes = 0;
        long timestampDelta = 2;
        int offsetDelta = 1;
        int sizeOfBodyInBytes = 100;
        int keySize = 105;// use a key size larger than the full message

        ByteBuffer buf = ByteBuffer.allocate((sizeOfBodyInBytes + (ByteUtils.sizeOfVarint(sizeOfBodyInBytes))));
        ByteUtils.writeVarint(sizeOfBodyInBytes, buf);
        buf.put(attributes);
        ByteUtils.writeVarlong(timestampDelta, buf);
        ByteUtils.writeVarint(offsetDelta, buf);
        ByteUtils.writeVarint(keySize, buf);
        buf.position(buf.limit());
        buf.flip();
        DefaultRecord.readFrom(buf, 0L, 0L, NO_SEQUENCE, null);
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidValueSize() throws IOException {
        byte attributes = 0;
        long timestampDelta = 2;
        int offsetDelta = 1;
        int sizeOfBodyInBytes = 100;
        int valueSize = 105;// use a value size larger than the full message

        ByteBuffer buf = ByteBuffer.allocate((sizeOfBodyInBytes + (ByteUtils.sizeOfVarint(sizeOfBodyInBytes))));
        ByteUtils.writeVarint(sizeOfBodyInBytes, buf);
        buf.put(attributes);
        ByteUtils.writeVarlong(timestampDelta, buf);
        ByteUtils.writeVarint(offsetDelta, buf);
        ByteUtils.writeVarint((-1), buf);// null key

        ByteUtils.writeVarint(valueSize, buf);
        buf.position(buf.limit());
        buf.flip();
        DefaultRecord.readFrom(buf, 0L, 0L, NO_SEQUENCE, null);
    }

    @Test(expected = InvalidRecordException.class)
    public void testUnderflowReadingTimestamp() {
        byte attributes = 0;
        int sizeOfBodyInBytes = 1;
        ByteBuffer buf = ByteBuffer.allocate((sizeOfBodyInBytes + (ByteUtils.sizeOfVarint(sizeOfBodyInBytes))));
        ByteUtils.writeVarint(sizeOfBodyInBytes, buf);
        buf.put(attributes);
        buf.flip();
        DefaultRecord.readFrom(buf, 0L, 0L, NO_SEQUENCE, null);
    }

    @Test(expected = InvalidRecordException.class)
    public void testUnderflowReadingVarlong() {
        byte attributes = 0;
        int sizeOfBodyInBytes = 2;// one byte for attributes, one byte for partial timestamp

        ByteBuffer buf = ByteBuffer.allocate(((sizeOfBodyInBytes + (ByteUtils.sizeOfVarint(sizeOfBodyInBytes))) + 1));
        ByteUtils.writeVarint(sizeOfBodyInBytes, buf);
        buf.put(attributes);
        ByteUtils.writeVarlong(156, buf);// needs 2 bytes to represent

        buf.position(((buf.limit()) - 1));
        buf.flip();
        DefaultRecord.readFrom(buf, 0L, 0L, NO_SEQUENCE, null);
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidVarlong() {
        byte attributes = 0;
        int sizeOfBodyInBytes = 11;// one byte for attributes, 10 bytes for max timestamp

        ByteBuffer buf = ByteBuffer.allocate(((sizeOfBodyInBytes + (ByteUtils.sizeOfVarint(sizeOfBodyInBytes))) + 1));
        ByteUtils.writeVarint(sizeOfBodyInBytes, buf);
        int recordStartPosition = buf.position();
        buf.put(attributes);
        ByteUtils.writeVarlong(Long.MAX_VALUE, buf);// takes 10 bytes

        buf.put((recordStartPosition + 10), Byte.MIN_VALUE);// use an invalid final byte

        buf.flip();
        DefaultRecord.readFrom(buf, 0L, 0L, NO_SEQUENCE, null);
    }

    @Test
    public void testSerdeNoSequence() throws IOException {
        ByteBuffer key = ByteBuffer.wrap("hi".getBytes());
        ByteBuffer value = ByteBuffer.wrap("there".getBytes());
        long baseOffset = 37;
        int offsetDelta = 10;
        long baseTimestamp = System.currentTimeMillis();
        long timestampDelta = 323;
        ByteBufferOutputStream out = new ByteBufferOutputStream(1024);
        DefaultRecord.writeTo(new DataOutputStream(out), offsetDelta, timestampDelta, key, value, new Header[0]);
        ByteBuffer buffer = out.buffer();
        buffer.flip();
        DefaultRecord record = DefaultRecord.readFrom(buffer, baseOffset, baseTimestamp, NO_SEQUENCE, null);
        Assert.assertNotNull(record);
        Assert.assertEquals(NO_SEQUENCE, record.sequence());
    }
}

