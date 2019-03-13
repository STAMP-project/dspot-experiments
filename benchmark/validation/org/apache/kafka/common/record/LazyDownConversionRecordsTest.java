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


import RecordBatch.CURRENT_MAGIC_VALUE;
import RecordBatch.NO_PARTITION_LEADER_EPOCH;
import Time.SYSTEM;
import TimestampType.CREATE_TIME;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompressionType.GZIP;
import static CompressionType.NONE;
import static ControlRecordType.COMMIT;
import static RecordBatch.CURRENT_MAGIC_VALUE;
import static RecordBatch.MAGIC_VALUE_V0;


public class LazyDownConversionRecordsTest {
    /**
     * Test the lazy down-conversion path in the presence of commit markers. When converting to V0 or V1, these batches
     * are dropped. If there happen to be no more batches left to convert, we must get an overflow message batch after
     * conversion.
     */
    @Test
    public void testConversionOfCommitMarker() throws IOException {
        MemoryRecords recordsToConvert = MemoryRecords.withEndTransactionMarker(0, SYSTEM.milliseconds(), NO_PARTITION_LEADER_EPOCH, 1, ((short) (1)), new EndTransactionMarker(COMMIT, 0));
        MemoryRecords convertedRecords = LazyDownConversionRecordsTest.convertRecords(recordsToConvert, ((byte) (1)), recordsToConvert.sizeInBytes());
        ByteBuffer buffer = convertedRecords.buffer();
        // read the offset and the batch length
        buffer.getLong();
        int sizeOfConvertedRecords = buffer.getInt();
        // assert we got an overflow message batch
        Assert.assertTrue((sizeOfConvertedRecords > (buffer.limit())));
        Assert.assertFalse(convertedRecords.batchIterator().hasNext());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedConversionTest {
        private final CompressionType compressionType;

        private final byte toMagic;

        public ParameterizedConversionTest(CompressionType compressionType, byte toMagic) {
            this.compressionType = compressionType;
            this.toMagic = toMagic;
        }

        @Parameterized.Parameters(name = "compressionType={0}, toMagic={1}")
        public static Collection<Object[]> data() {
            List<Object[]> values = new ArrayList<>();
            for (byte toMagic = MAGIC_VALUE_V0; toMagic <= (CURRENT_MAGIC_VALUE); toMagic++) {
                values.add(new Object[]{ NONE, toMagic });
                values.add(new Object[]{ GZIP, toMagic });
            }
            return values;
        }

        /**
         * Test the lazy down-conversion path.
         */
        @Test
        public void testConversion() throws IOException {
            doTestConversion(false);
        }

        /**
         * Test the lazy down-conversion path where the number of bytes we want to convert is much larger than the
         * number of bytes we get after conversion. This causes overflow message batch(es) to be appended towards the
         * end of the converted output.
         */
        @Test
        public void testConversionWithOverflow() throws IOException {
            doTestConversion(true);
        }

        private void doTestConversion(boolean testConversionOverflow) throws IOException {
            List<Long> offsets = Arrays.asList(0L, 2L, 3L, 9L, 11L, 15L, 16L, 17L, 22L, 24L);
            Header[] headers = new Header[]{ new RecordHeader("headerKey1", "headerValue1".getBytes()), new RecordHeader("headerKey2", "headerValue2".getBytes()), new RecordHeader("headerKey3", "headerValue3".getBytes()) };
            List<SimpleRecord> records = Arrays.asList(new SimpleRecord(1L, "k1".getBytes(), "hello".getBytes()), new SimpleRecord(2L, "k2".getBytes(), "goodbye".getBytes()), new SimpleRecord(3L, "k3".getBytes(), "hello again".getBytes()), new SimpleRecord(4L, "k4".getBytes(), "goodbye for now".getBytes()), new SimpleRecord(5L, "k5".getBytes(), "hello again".getBytes()), new SimpleRecord(6L, "k6".getBytes(), "I sense indecision".getBytes()), new SimpleRecord(7L, "k7".getBytes(), "what now".getBytes()), new SimpleRecord(8L, "k8".getBytes(), "running out".getBytes(), headers), new SimpleRecord(9L, "k9".getBytes(), "ok, almost done".getBytes()), new SimpleRecord(10L, "k10".getBytes(), "finally".getBytes(), headers));
            Assert.assertEquals("incorrect test setup", offsets.size(), records.size());
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L);
            for (int i = 0; i < 3; i++)
                builder.appendWithOffset(offsets.get(i), records.get(i));

            builder.close();
            builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L);
            for (int i = 3; i < 6; i++)
                builder.appendWithOffset(offsets.get(i), records.get(i));

            builder.close();
            builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L);
            for (int i = 6; i < 10; i++)
                builder.appendWithOffset(offsets.get(i), records.get(i));

            builder.close();
            buffer.flip();
            MemoryRecords recordsToConvert = MemoryRecords.readableRecords(buffer);
            int numBytesToConvert = recordsToConvert.sizeInBytes();
            if (testConversionOverflow)
                numBytesToConvert *= 2;

            MemoryRecords convertedRecords = LazyDownConversionRecordsTest.convertRecords(recordsToConvert, toMagic, numBytesToConvert);
            LazyDownConversionRecordsTest.verifyDownConvertedRecords(records, offsets, convertedRecords, compressionType, toMagic);
        }
    }
}

