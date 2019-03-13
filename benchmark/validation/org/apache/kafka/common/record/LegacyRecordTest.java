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


import RecordBatch.NO_TIMESTAMP;
import TimestampType.CREATE_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static LegacyRecord.CRC_LENGTH;
import static LegacyRecord.CRC_OFFSET;
import static TimestampType.CREATE_TIME;


@RunWith(Parameterized.class)
public class LegacyRecordTest {
    private final byte magic;

    private final long timestamp;

    private final ByteBuffer key;

    private final ByteBuffer value;

    private final CompressionType compression;

    private final TimestampType timestampType;

    private final LegacyRecord record;

    public LegacyRecordTest(byte magic, long timestamp, byte[] key, byte[] value, CompressionType compression) {
        this.magic = magic;
        this.timestamp = timestamp;
        this.timestampType = CREATE_TIME;
        this.key = (key == null) ? null : ByteBuffer.wrap(key);
        this.value = (value == null) ? null : ByteBuffer.wrap(value);
        this.compression = compression;
        this.record = LegacyRecord.create(magic, timestamp, key, value, compression, timestampType);
    }

    @Test
    public void testFields() {
        Assert.assertEquals(compression, record.compressionType());
        Assert.assertEquals(((key) != null), record.hasKey());
        Assert.assertEquals(key, record.key());
        if ((key) != null)
            Assert.assertEquals(key.limit(), record.keySize());

        Assert.assertEquals(magic, record.magic());
        Assert.assertEquals(value, record.value());
        if ((value) != null)
            Assert.assertEquals(value.limit(), record.valueSize());

        if ((magic) > 0) {
            Assert.assertEquals(timestamp, record.timestamp());
            Assert.assertEquals(timestampType, record.timestampType());
        } else {
            Assert.assertEquals(NO_TIMESTAMP, record.timestamp());
            Assert.assertEquals(NO_TIMESTAMP_TYPE, record.timestampType());
        }
    }

    @Test
    public void testChecksum() {
        Assert.assertEquals(record.checksum(), record.computeChecksum());
        byte attributes = LegacyRecord.computeAttributes(magic, this.compression, CREATE_TIME);
        Assert.assertEquals(record.checksum(), LegacyRecord.computeChecksum(magic, attributes, this.timestamp, ((this.key) == null ? null : this.key.array()), ((this.value) == null ? null : this.value.array())));
        Assert.assertTrue(record.isValid());
        for (int i = (CRC_OFFSET) + (CRC_LENGTH); i < (record.sizeInBytes()); i++) {
            LegacyRecord copy = copyOf(record);
            copy.buffer().put(i, ((byte) (69)));
            Assert.assertFalse(copy.isValid());
            try {
                copy.ensureValid();
                Assert.fail("Should fail the above test.");
            } catch (InvalidRecordException e) {
                // this is good
            }
        }
    }

    @Test
    public void testEquality() {
        Assert.assertEquals(record, copyOf(record));
    }
}

