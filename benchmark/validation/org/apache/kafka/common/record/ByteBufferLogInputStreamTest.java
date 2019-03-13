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


import CompressionType.NONE;
import TimestampType.CREATE_TIME;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.kafka.common.errors.CorruptRecordException;
import org.junit.Assert;
import org.junit.Test;

import static DefaultRecordBatch.LENGTH_OFFSET;
import static DefaultRecordBatch.MAGIC_OFFSET;


public class ByteBufferLogInputStreamTest {
    @Test
    public void iteratorIgnoresIncompleteEntries() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 0L);
        builder.append(15L, "a".getBytes(), "1".getBytes());
        builder.append(20L, "b".getBytes(), "2".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 2L);
        builder.append(30L, "c".getBytes(), "3".getBytes());
        builder.append(40L, "d".getBytes(), "4".getBytes());
        builder.close();
        buffer.flip();
        buffer.limit(((buffer.limit()) - 5));
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        Iterator<MutableRecordBatch> iterator = records.batches().iterator();
        Assert.assertTrue(iterator.hasNext());
        MutableRecordBatch first = iterator.next();
        Assert.assertEquals(1L, first.lastOffset());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = CorruptRecordException.class)
    public void iteratorRaisesOnTooSmallRecords() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 0L);
        builder.append(15L, "a".getBytes(), "1".getBytes());
        builder.append(20L, "b".getBytes(), "2".getBytes());
        builder.close();
        int position = buffer.position();
        builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 2L);
        builder.append(30L, "c".getBytes(), "3".getBytes());
        builder.append(40L, "d".getBytes(), "4".getBytes());
        builder.close();
        buffer.flip();
        buffer.putInt((position + (LENGTH_OFFSET)), 9);
        ByteBufferLogInputStream logInputStream = new ByteBufferLogInputStream(buffer, Integer.MAX_VALUE);
        Assert.assertNotNull(logInputStream.nextBatch());
        logInputStream.nextBatch();
    }

    @Test(expected = CorruptRecordException.class)
    public void iteratorRaisesOnInvalidMagic() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 0L);
        builder.append(15L, "a".getBytes(), "1".getBytes());
        builder.append(20L, "b".getBytes(), "2".getBytes());
        builder.close();
        int position = buffer.position();
        builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 2L);
        builder.append(30L, "c".getBytes(), "3".getBytes());
        builder.append(40L, "d".getBytes(), "4".getBytes());
        builder.close();
        buffer.flip();
        buffer.put((position + (MAGIC_OFFSET)), ((byte) (37)));
        ByteBufferLogInputStream logInputStream = new ByteBufferLogInputStream(buffer, Integer.MAX_VALUE);
        Assert.assertNotNull(logInputStream.nextBatch());
        logInputStream.nextBatch();
    }

    @Test(expected = CorruptRecordException.class)
    public void iteratorRaisesOnTooLargeRecords() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 0L);
        builder.append(15L, "a".getBytes(), "1".getBytes());
        builder.append(20L, "b".getBytes(), "2".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 2L);
        builder.append(30L, "c".getBytes(), "3".getBytes());
        builder.append(40L, "d".getBytes(), "4".getBytes());
        builder.close();
        buffer.flip();
        ByteBufferLogInputStream logInputStream = new ByteBufferLogInputStream(buffer, 25);
        Assert.assertNotNull(logInputStream.nextBatch());
        logInputStream.nextBatch();
    }
}

